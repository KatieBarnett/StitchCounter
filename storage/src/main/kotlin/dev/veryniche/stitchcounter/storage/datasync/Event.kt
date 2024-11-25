package dev.veryniche.stitchcounter.storage.datasync

import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.storage.datasync.DataLayerListenerService.Companion.KEY_PROJECT
import dev.veryniche.stitchcounter.storage.datasync.DataLayerListenerService.Companion.KEY_PROJECT_ID
import dev.veryniche.stitchcounter.storage.datasync.DataLayerListenerService.Companion.PROJECT_DELETE_PATH
import dev.veryniche.stitchcounter.storage.datasync.DataLayerListenerService.Companion.PROJECT_UPDATE_PATH
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class Event(
    val path: String,
    val key: String,
    val data: String
)

fun Project.toUpdateEvent() = Event(
    path = PROJECT_UPDATE_PATH,
    key = KEY_PROJECT,
    data = Json.encodeToString<Project>(this)
)

fun Int.toDeleteEvent() = Event(
    path = PROJECT_DELETE_PATH,
    key = KEY_PROJECT_ID,
    data = this.toString()
)
