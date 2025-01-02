package dev.veryniche.stitchcounter.mobile.purchase

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.google.common.collect.ImmutableList
import dev.veryniche.stitchcounter.mobile.R
import dev.veryniche.stitchcounter.mobile.purchase.Subscription.Plan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

data class InAppProduct(
    val productId: String,
    val productName: String,
    val productDescription: String,
    val purchasePrice: String?,
    val purchaseCurrency: String?,
    val purchased: Boolean?,
) {
    val displayedPrice = "$purchasePrice $purchaseCurrency"
}

data class Subscription(
    val productId: String,
    val productName: String,
    val productDescription: String,
    val plans: List<Plan>?,
    val purchased: Boolean?,
) {

    data class Plan(
        val planId: String,
        val purchasePrice: String?,
        val purchaseCurrency: String?,
        val offerToken: String,
    ) {
        val displayedPrice = "$purchasePrice $purchaseCurrency"
    }
}

internal fun getProductQuery(id: String) =
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId(id)
        .setProductType(BillingClient.ProductType.INAPP)
        .build()

internal fun getSubscriptionQuery(id: String) =
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId(id)
        .setProductType(BillingClient.ProductType.SUBS)
        .build()

class PurchaseManager(
    private val activity: Activity,
    private val coroutineScope: CoroutineScope
) {
    private val _purchases = MutableStateFlow<List<String>>(emptyList())
    val purchases = _purchases.asStateFlow()

    private val _subscriptions = MutableStateFlow<List<String>>(emptyList())
    val subscriptions = _subscriptions.asStateFlow()

    private val _availableProducts = MutableStateFlow<List<InAppProduct>>(emptyList())
    val availableProducts = _availableProducts.asStateFlow()

    private val _availableSubscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val availableSubscriptions = _availableSubscriptions.asStateFlow()

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { _, _ ->
            coroutineScope.launch(Dispatchers.IO) {
                processPurchases()
                processSubscriptions()
            }
        }

    val acknowledgePurchaseResponseListener =
        AcknowledgePurchaseResponseListener {
            coroutineScope.launch(Dispatchers.IO) {
                processPurchases()
                processSubscriptions()
            }
        }

    private var billingClient = BillingClient.newBuilder(activity)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    fun connectToBilling() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    coroutineScope.launch {
                        processAvailableProducts()
                        processAvailableSubscriptions()
                        processPurchases()
                        processSubscriptions()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                billingClient.startConnection(this)
            }
        })
    }

    suspend fun processAvailableProducts() {
        if (appProductList.isNotEmpty()) {
            val params = QueryProductDetailsParams.newBuilder()
            params.setProductList(appProductList)

            // leverage queryProductDetails Kotlin extension function
            val productDetailsResult = withContext(Dispatchers.IO) {
                billingClient.queryProductDetails(params.build())
            }

            // Process the result.
            // Update products list
            _availableProducts.update {
                val newList = it.toMutableList()
                newList.addAll(
                    productDetailsResult.productDetailsList?.map {
                        InAppProduct(
                            productId = it.productId,
                            productName = it.name,
                            productDescription = it.description,
                            purchasePrice = it.oneTimePurchaseOfferDetails?.formattedPrice,
                            purchaseCurrency = it.oneTimePurchaseOfferDetails?.priceCurrencyCode,
                            purchased = null
                        )
                    } ?: listOf()
                )
                newList
            }
        }
    }

    suspend fun processAvailableSubscriptions() {
        if (appSubscriptionList.isNotEmpty()) {
            val params = QueryProductDetailsParams.newBuilder()
            params.setProductList(appSubscriptionList)

            // leverage queryProductDetails Kotlin extension function
            val productDetailsResult = withContext(Dispatchers.IO) {
                billingClient.queryProductDetails(params.build())
            }

            // Process the result.
            // Update products list
            _availableSubscriptions.update {
                val newList = it.toMutableList()
                newList.addAll(
                    productDetailsResult.productDetailsList?.map {
                        Subscription(
                            productId = it.productId,
                            productName = it.name,
                            productDescription = it.description,
                            plans = it.subscriptionOfferDetails?.map {
                                Plan(
                                    planId = it.basePlanId,
                                    purchasePrice = it.pricingPhases.pricingPhaseList.firstOrNull()?.formattedPrice,
                                    purchaseCurrency = it.pricingPhases.pricingPhaseList.firstOrNull()?.priceCurrencyCode,
                                    offerToken = it.offerToken,
                                )
                            },
                            purchased = null
                        )
                    } ?: listOf()
                )
                newList
            }
        }
    }

    suspend fun processPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)

        // uses queryPurchasesAsync Kotlin extension function
        val purchasesResult = withContext(Dispatchers.IO) {
            billingClient.queryPurchasesAsync(params.build())
        }

        _purchases.update {
            purchasesResult.purchasesList.filter {
                it.purchaseState == PurchaseState.PURCHASED
            }.map { purchase ->
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                    withContext(Dispatchers.IO) {
                        billingClient.acknowledgePurchase(
                            acknowledgePurchaseParams.build(),
                            acknowledgePurchaseResponseListener
                        )
                    }
                }
                purchase.products.firstOrNull().toString()
            }
        }
    }

    suspend fun processSubscriptions() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)

        // uses queryPurchasesAsync Kotlin extension function
        val purchasesResult = withContext(Dispatchers.IO) {
            billingClient.queryPurchasesAsync(params.build())
        }

        _subscriptions.update {
            purchasesResult.purchasesList.filter {
                it.purchaseState == PurchaseState.PURCHASED
            }.map { purchase ->
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                    withContext(Dispatchers.IO) {
                        billingClient.acknowledgePurchase(
                            acknowledgePurchaseParams.build(),
                            acknowledgePurchaseResponseListener
                        )
                    }
                }
                purchase.products.firstOrNull().toString()
            }
        }
    }

    fun purchaseProduct(
        productId: String,
        onError: (message: Int) -> Unit,
        retryCount: Int = 0
    ) {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetails = productDetailsList.firstOrNull { productDetails ->
                    productDetails.productId == productId
                }
                if (productDetails != null) {
                    initiateBillingProduct(productDetails)
                    Timber.i("Initiating purchase of $productId")
                } else {
                    onError.invoke(R.string.purchase_error_generic)
                    Timber.e("Product details for $productId are null")
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (retryCount < 3) {
                                purchaseProduct(productId, onError, retryCount + 1)
                            } else {
                                onError.invoke(R.string.purchase_error_disconnected)
                                Timber.e("Can't connect to billing client - too many retries")
                            }
                        } else {
                            onError.invoke(R.string.purchase_error_disconnected)
                            Timber.e("Can't connect to billing client")
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        onError.invoke(R.string.purchase_error_disconnected)
                        Timber.e("Can't connect to billing client")
                    }
                })
            }
        }
    }

    fun purchaseSubscription(
        productId: String,
        offerToken: String,
        onError: (message: Int) -> Unit,
        retryCount: Int = 0
    ) {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetails = productDetailsList.firstOrNull { productDetails ->
                    productDetails.productId == productId
                }
                if (productDetails != null) {
                    initiateBillingSubscription(productDetails, offerToken)
                    Timber.i("Initiating purchase of $productId")
                } else {
                    onError.invoke(R.string.purchase_error_generic)
                    Timber.e("Product details for $productId are null")
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (retryCount < 3) {
                                purchaseSubscription(productId, offerToken, onError, retryCount + 1)
                            } else {
                                onError.invoke(R.string.purchase_error_disconnected)
                                Timber.e("Can't connect to billing client - too many retries")
                            }
                        } else {
                            onError.invoke(R.string.purchase_error_disconnected)
                            Timber.e("Can't connect to billing client")
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        onError.invoke(R.string.purchase_error_disconnected)
                        Timber.e("Can't connect to billing client")
                    }
                })
            }
        }
    }

    private fun initiateBillingProduct(productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private fun initiateBillingSubscription(productDetails: ProductDetails, offerToken: String) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }
}
