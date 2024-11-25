package dev.veryniche.stitchcounter.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: Int? = null,
    val name: String,
    val elapsedTime: Long = 0L,
    val counters: List<Counter> = listOf(),
    val lastModified: Long = System.currentTimeMillis()
)