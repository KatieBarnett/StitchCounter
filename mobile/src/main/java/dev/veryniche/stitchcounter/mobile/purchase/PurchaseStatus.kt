package dev.veryniche.stitchcounter.mobile.purchase

data class PurchaseStatus(
    val isAdRemovalPurchased: Boolean,
    val isSyncPurchased: Boolean,
    val isBundlePurchased: Boolean,
)

enum class PurchaseAction {
    AD_REMOVAL, SYNC, BUNDLE
}
