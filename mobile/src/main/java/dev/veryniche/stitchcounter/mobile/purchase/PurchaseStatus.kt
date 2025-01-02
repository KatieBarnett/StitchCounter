package dev.veryniche.stitchcounter.mobile.purchase

data class PurchaseStatus(
    val isBundleSubscribed: Boolean = false,
)

sealed class PurchaseAction(productId: String) {

    data class Subscribe(val productId: String, val offerToken: String) : PurchaseAction(productId)
}
