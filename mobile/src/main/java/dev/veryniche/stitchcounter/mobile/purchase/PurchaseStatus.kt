package dev.veryniche.stitchcounter.mobile.purchase

data class PurchaseStatus(
    val isBundlePurchased: Boolean = false,
)

enum class PurchaseAction {
    BUNDLE
}
