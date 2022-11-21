package dev.katiebarnett.stitchcounter.data.models

data class Project(
    val id: Int,
    val name: String,
    val elapsedTime: Long = 0L,
    val counters: List<Counter> = listOf(),
    val lastModified: Long = System.currentTimeMillis()
)
