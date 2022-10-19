package dev.katiebarnett.stitchcounter.models

data class Project(
    val id: Int,
    val name: String,
    val elapsedTime: Long = 0L,
    val counters: List<Counter> = listOf()
)
