package dev.veryniche.stitchcounter.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.veryniche.stitchcounter.data.models.ScreenOnState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class UserPreferences(
    val whatsNewLastSeen: Int,
    val keepScreenOn: ScreenOnState,
    val lastReviewDate: Long,
)

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    private object PreferencesKeys {
        val WHATS_NEW_LAST_SEEN = intPreferencesKey("whats_new_last_seen")
        val KEEP_SCREEN_ON_STATE = stringPreferencesKey("keep_screen_on_state")
        val LAST_REVIEW_DATE = longPreferencesKey("last_review_date")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val whatsNewLastSeen = preferences[PreferencesKeys.WHATS_NEW_LAST_SEEN] ?: 0
            val keepScreenOnStateString = preferences[PreferencesKeys.KEEP_SCREEN_ON_STATE]
            val keepScreenOnState = keepScreenOnStateString?.let {
                ScreenOnState.fromJsonString(it)
            } ?: ScreenOnState()
            val lastReviewDate = preferences[PreferencesKeys.LAST_REVIEW_DATE] ?: -1L
            UserPreferences(whatsNewLastSeen, keepScreenOnState, lastReviewDate)
        }

    suspend fun updateWhatsNewLastSeen(whatsNewLastSeen: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WHATS_NEW_LAST_SEEN] = whatsNewLastSeen
        }
    }

    suspend fun updateKeepScreenOn(keepScreenOn: ScreenOnState) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEEP_SCREEN_ON_STATE] = keepScreenOn.toJsonString()
        }
    }

    suspend fun updateLastReviewDate() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_REVIEW_DATE] = System.currentTimeMillis()
        }
    }
}
