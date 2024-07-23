package dev.veryniche.stitchcounter.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

@Serializable
data class ScreenOnState(
    val counterScreenOn: Boolean = true,
    val projectScreenOn: Boolean = true,
) {
    companion object {
        fun fromJsonString(jsonString: String): ScreenOnState? {
            return try {
                Json.decodeFromString<ScreenOnState>(jsonString)
            } catch (e: SerializationException) {
                Timber.e(e, "Error de-serializing ScreenOnState of $jsonString")
                null
            }
        }
    }

    fun toJsonString() = Json.encodeToString(this)
}