/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.katiebarnett.stitchcounter.storage

import androidx.datastore.core.DataStore
import dev.katiebarnett.stitchcounter.core.models.SavedGame
import dev.katiebarnett.stitchcounter.core.models.mapToGameType
import dev.katiebarnett.stitchcounter.storage.models.Game
import dev.katiebarnett.stitchcounter.storage.models.Projects
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProjectsDataSource @Inject constructor(
    private val ProjectsStore: DataStore<Projects>
) {
    
    companion object {
        internal const val PROTO_FILE_NAME = "saved_games.pb"
    }

    val ProjectsFlow = ProjectsStore.data
        .map { 
            it.gamesList.map {
                SavedGame(
                    position = it.position,
                    gameType = it.gameType.mapToGameType(),
                    seed = it.seed.toLong(),
                    lastModified = it.lastModified.toLong(),
                    stackSize = it.stackSize
                ) 
            }.sortedByDescending { it.lastModified }
        }
    
    suspend fun saveGame(game: SavedGame) {
        ProjectsStore.updateData { currentProjects ->
            val currentIndex = currentProjects.gamesList.indexOfFirst { it.seed.toLong() == game.seed }
            if (currentIndex != -1) {
                currentProjects.toBuilder().setGames(currentIndex, game.toGame()).build()
            } else {
                currentProjects.toBuilder().addGames(game.toGame()).build()
            }
        }
    }

    suspend fun deleteGame(seed: Long) {
        ProjectsStore.updateData { currentProjects ->
            val currentIndex = currentProjects.gamesList.indexOfFirst { it.seed.toLong() == seed }
            if (currentIndex != -1) {
                currentProjects.toBuilder().removeGames(currentIndex).build()
            } else {
                // Do nothing
                currentProjects.toBuilder().build()
            }
        }
    }

    suspend fun clearProjects() {
        ProjectsStore.updateData { currentProjects ->
            currentProjects.toBuilder().clearGames().build()
        }
    }


    fun SavedGame.toGame(): Game {
        val gameBuilder = Game.newBuilder()
        gameBuilder.gameType = gameType?.name
        gameBuilder.seed = seed.toDouble()
        gameBuilder.position = position
        gameBuilder.stackSize = stackSize
        gameBuilder.lastModified = lastModified.toDouble()
        return gameBuilder.build()
    }
//
//    suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) =
//        userPreferences.setList(
//            listGetter = { it.followedTopicIds },
//            listModifier = { followedTopicIds.toList() },
//            clear = { it.clear() },
//            addAll = { dslList, editedList -> dslList.addAll(editedList) }
//        )
//
//    suspend fun toggleFollowedTopicId(followedTopicId: String, followed: Boolean) =
//        userPreferences.editList(
//            add = followed,
//            value = followedTopicId,
//            listGetter = { it.followedTopicIds },
//            clear = { it.clear() },
//            addAll = { dslList, editedList -> dslList.addAll(editedList) }
//        )
//
//    suspend fun setFollowedAuthorIds(followedAuthorIds: Set<String>) =
//        userPreferences.setList(
//            listGetter = { it.followedAuthorIds },
//            listModifier = { followedAuthorIds.toList() },
//            clear = { it.clear() },
//            addAll = { dslList, editedList -> dslList.addAll(editedList) }
//        )
//
//    suspend fun toggleFollowedAuthorId(followedAuthorId: String, followed: Boolean) =
//        userPreferences.editList(
//            add = followed,
//            value = followedAuthorId,
//            listGetter = { it.followedAuthorIds },
//            clear = { it.clear() },
//            addAll = { dslList, editedList -> dslList.addAll(editedList) }
//        )
//
//    suspend fun toggleNewsResourceBookmark(newsResourceId: String, bookmarked: Boolean) =
//        userPreferences.editList(
//            add = bookmarked,
//            value = newsResourceId,
//            listGetter = { it.bookmarkedNewsResourceIds },
//            clear = { it.clear() },
//            addAll = { dslList, editedList -> dslList.addAll(editedList) }
//        )
//
//    suspend fun getChangeListVersions() = userPreferences.data
//        .map {
//            ChangeListVersions(
//                topicVersion = it.topicChangeListVersion,
//                authorVersion = it.authorChangeListVersion,
//                episodeVersion = it.episodeChangeListVersion,
//                newsResourceVersion = it.newsResourceChangeListVersion,
//            )
//        }
//        .firstOrNull() ?: ChangeListVersions()
//
//    /**
//     * Update the [ChangeListVersions] using [update].
//     */
//    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
//        try {
//            userPreferences.updateData { currentPreferences ->
//                val updatedChangeListVersions = update(
//                    ChangeListVersions(
//                        topicVersion = currentPreferences.topicChangeListVersion,
//                        authorVersion = currentPreferences.authorChangeListVersion,
//                        episodeVersion = currentPreferences.episodeChangeListVersion,
//                        newsResourceVersion = currentPreferences.newsResourceChangeListVersion
//                    )
//                )
//
//                currentPreferences.copy {
//                    topicChangeListVersion = updatedChangeListVersions.topicVersion
//                    authorChangeListVersion = updatedChangeListVersions.authorVersion
//                    episodeChangeListVersion = updatedChangeListVersions.episodeVersion
//                    newsResourceChangeListVersion = updatedChangeListVersions.newsResourceVersion
//                }
//            }
//        } catch (ioException: IOException) {
//            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
//        }
//    }
//
//    /**
//     * Adds or removes [value] from the [DslList] provided by [listGetter]
//     */
//    private suspend fun <T : DslProxy> DataStore<UserPreferences>.editList(
//        add: Boolean,
//        value: String,
//        listGetter: (UserPreferencesKt.Dsl) -> DslList<String, T>,
//        clear: UserPreferencesKt.Dsl.(DslList<String, T>) -> Unit,
//        addAll: UserPreferencesKt.Dsl.(DslList<String, T>, Iterable<String>) -> Unit
//    ) {
//        setList(
//            listGetter = listGetter,
//            listModifier = { currentList ->
//                if (add) currentList + value
//                else currentList - value
//            },
//            clear = clear,
//            addAll = addAll
//        )
//    }
//
//    /**
//     * Sets the value provided by [listModifier] into the [DslList] read by [listGetter]
//     */
//    private suspend fun <T : DslProxy> DataStore<UserPreferences>.setList(
//        listGetter: (UserPreferencesKt.Dsl) -> DslList<String, T>,
//        listModifier: (DslList<String, T>) -> List<String>,
//        clear: UserPreferencesKt.Dsl.(DslList<String, T>) -> Unit,
//        addAll: UserPreferencesKt.Dsl.(DslList<String, T>, List<String>) -> Unit
//    ) {
//        try {
//            updateData {
//                it.copy {
//                    val dslList = listGetter(this)
//                    val newList = listModifier(dslList)
//                    clear(dslList)
//                    addAll(dslList, newList)
//                }
//            }
//        } catch (ioException: IOException) {
//            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
//        }
//    }
}
