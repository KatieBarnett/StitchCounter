package dev.veryniche.mobile.data.models

data class Counter(
    val id: Int,
    val name: String,
    val currentCount: Int = 0,
    val maxCount: Int = 0
) {

}
