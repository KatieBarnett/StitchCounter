package dev.veryniche.stitchcounter.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Counter(
    val id: Int,
    val name: String,
    val currentCount: Int = 0,
    val maxCount: Int = 0
)