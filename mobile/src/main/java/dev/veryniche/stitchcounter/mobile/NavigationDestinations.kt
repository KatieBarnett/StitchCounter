package dev.veryniche.stitchcounter.mobile

import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import kotlinx.serialization.Serializable

@Serializable
object AboutDestination

@Serializable
object ProjectListDestination

@Serializable
data class ProjectDestination(val id: Int, val inEditMode: Boolean)

@Serializable
data class CounterDestination(val projectId: Int, val counterId: Int)