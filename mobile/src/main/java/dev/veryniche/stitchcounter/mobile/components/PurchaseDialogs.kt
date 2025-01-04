package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.veryniche.stitchcounter.core.Analytics.Action
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.mobile.R
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.purchase.Products
import dev.veryniche.stitchcounter.mobile.purchase.Products.bundleOfferAnnual
import dev.veryniche.stitchcounter.mobile.purchase.Products.bundleOfferMonthly
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseAction
import dev.veryniche.stitchcounter.mobile.purchase.Subscription
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun PurchaseDialog(
    message: Int,
    availableSubscriptions: List<Subscription>,
    onPurchaseClick: (PurchaseAction) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(stringResource(R.string.purchase_limit_title)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimen.spacingQuad)
            ) {
                InfoText(message, bold = true)
                InfoText(dev.veryniche.stitchcounter.core.R.string.purchases_pro_description)
                UnorderedListText(
                    listOf(
                        dev.veryniche.stitchcounter.core.R.string.purchases_pro_feature_1,
                        dev.veryniche.stitchcounter.core.R.string.purchases_pro_feature_2,
                        dev.veryniche.stitchcounter.core.R.string.purchases_pro_feature_3,
                        dev.veryniche.stitchcounter.core.R.string.purchases_pro_feature_4,
                        dev.veryniche.stitchcounter.core.R.string.purchases_pro_feature_5,
                    )
                )
                availableSubscriptions.forEach { subscription ->
                    subscription.plans?.forEach {
                        val buttonTextRes = when (it.planId) {
                            bundleOfferAnnual -> dev.veryniche.stitchcounter.core.R.string.purchases_buy_pro_annual
                            bundleOfferMonthly -> dev.veryniche.stitchcounter.core.R.string.purchases_buy_pro_monthly
                            else -> dev.veryniche.stitchcounter.core.R.string.purchases_buy_pro_misc
                        }
                        Button(content = {
                            Text(text = stringResource(buttonTextRes, it.displayedPrice))
                        }, onClick = {
                            trackEvent("${Action.PurchasePro} ${it.planId}", isMobile = true)
                            onPurchaseClick.invoke(
                                PurchaseAction.Subscribe(subscription.productId, it.offerToken)
                            )
                        })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) {
                Text(stringResource(R.string.purchase_limit_button_cancel))
            }
        }
    )
}

@PreviewComponent
@Composable
fun PurchaseDialogPreview() {
    StitchCounterTheme {
        PurchaseDialog(
            message = R.string.purchase_limit_projects,
            onDismiss = {},
            onPurchaseClick = {},
            availableSubscriptions = listOf(
                Subscription(
                    productId = Products.bundle,
                    productName = "Unlimited Bundle",
                    productDescription = "Unlimited Bundle Description",
                    purchased = false,
                    plans = listOf(
                        Subscription.Plan(
                            planId = bundleOfferMonthly,
                            offerToken = "",
                            purchasePrice = "1.00",
                            purchaseCurrency = "AUD",
                        ),
                        Subscription.Plan(
                            planId = bundleOfferAnnual,
                            offerToken = "",
                            purchasePrice = "11.00",
                            purchaseCurrency = "AUD",
                        )
                    )
                )
            ),
        )
    }
}
