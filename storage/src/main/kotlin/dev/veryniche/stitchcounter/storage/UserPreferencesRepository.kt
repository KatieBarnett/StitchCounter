package dev.veryniche.stitchcounter.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
    val themeMode: ThemeMode,
    val hasBeenAskedForReview: Boolean,
    val lastReviewDate: Long,
    val tileProjectId: Int?,
    val tileCounterId: Int?,
    val isProPurchased: Boolean,
    val isConectedAppInfoDoNotShow: Boolean,
)

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val isMobile: Boolean,
) {

    private object PreferencesKeys {
        val WHATS_NEW_LAST_SEEN = intPreferencesKey("whats_new_last_seen")
        val CONNECTED_APP_INFO_DO_NOT_SHOW = booleanPreferencesKey("connected_app_info_do_not_show")
        val KEEP_SCREEN_ON_STATE = stringPreferencesKey("keep_screen_on_state")
        val LAST_REVIEW_DATE = longPreferencesKey("last_review_date")
        val HAS_BEEN_ASKED_FOR_REVIEW = booleanPreferencesKey("has_been_asked_for_review")
        val TILE_PROJECT_ID = intPreferencesKey("tile_project_id")
        val TILE_COUNTER_ID = intPreferencesKey("tile_counter_id")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PRO_PURCHASED = booleanPreferencesKey("pro_purchased")
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
            } ?: if (isMobile) {
                ScreenOnState(false, false)
            } else {
                ScreenOnState(true, true)
            }
            val themeModeString = preferences[PreferencesKeys.THEME_MODE]
            val themeMode = themeModeString?.let {
                ThemeMode.valueOf(it)
            } ?: ThemeMode.Auto
            val lastReviewDate = preferences[PreferencesKeys.LAST_REVIEW_DATE] ?: -1L
            val hasBeenAskedForReview = preferences[PreferencesKeys.HAS_BEEN_ASKED_FOR_REVIEW] == true
            val tileProjectId = preferences[PreferencesKeys.TILE_PROJECT_ID]
            val tileCounterId = preferences[PreferencesKeys.TILE_COUNTER_ID]
            val isProPurchased = preferences[PreferencesKeys.PRO_PURCHASED] == true
            val isConnectedAppInfoDoNotShow = preferences[PreferencesKeys.CONNECTED_APP_INFO_DO_NOT_SHOW] == true
            UserPreferences(
                whatsNewLastSeen = whatsNewLastSeen,
                keepScreenOn = keepScreenOnState,
                themeMode = themeMode,
                lastReviewDate = lastReviewDate,
                tileProjectId = tileProjectId,
                tileCounterId = tileCounterId,
                isProPurchased = isProPurchased,
                isConectedAppInfoDoNotShow = isConnectedAppInfoDoNotShow,
                hasBeenAskedForReview = hasBeenAskedForReview,
            )
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

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun updateLastReviewDate() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_REVIEW_DATE] = System.currentTimeMillis()
        }
    }

    suspend fun updateHasBeenAskedForReview(hasBeenAskedForReview: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_BEEN_ASKED_FOR_REVIEW] = hasBeenAskedForReview
        }
    }

    suspend fun updateTileProjectId(id: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TILE_PROJECT_ID] = id
        }
    }

    suspend fun updateTileCounterId(id: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TILE_COUNTER_ID] = id
        }
    }

    suspend fun updateProPurchased(isProPurchased: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRO_PURCHASED] = isProPurchased
        }
    }

    suspend fun updateIsConnectedAppInfoDoNotShow(isConnectedAppInfoDoNotShow: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CONNECTED_APP_INFO_DO_NOT_SHOW] = isConnectedAppInfoDoNotShow
        }
    }
}
