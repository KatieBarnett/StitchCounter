package dev.veryniche.stitchcounter.mobile

import kotlinx.serialization.Serializable

@Serializable
object AboutDestination

@Serializable
object SettingsDestination

@Serializable
object ProjectListDestination

@Serializable
data class ProjectDestination(val id: Int, val inEditMode: Boolean)

@Serializable
data class CounterDestination(val projectId: Int, val counterId: Int)