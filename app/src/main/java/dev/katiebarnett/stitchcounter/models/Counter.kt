package dev.katiebarnett.stitchcounter.models

data class Counter(
    val id: Int,
    val name: String,
    val currentCount: Int = 0
) {

}
