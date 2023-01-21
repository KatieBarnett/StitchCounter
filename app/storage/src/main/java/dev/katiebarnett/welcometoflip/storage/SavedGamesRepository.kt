package dev.veryniche.stitchcounter.storage

import dev.veryniche.stitchcounter.core.models.GameType
import dev.veryniche.stitchcounter.core.models.SavedGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectsRepository @Inject constructor(
    private val ProjectsDataSource: ProjectsDataSource
) {

    fun getProjects(): Flow<List<SavedGame>> {
        return ProjectsDataSource.ProjectsFlow
    }
    
    suspend fun saveGame(gameType: GameType, seed: Long, position: Int, stackSize: Int) {
        ProjectsDataSource.saveGame(SavedGame(
            position = position,
            stackSize = stackSize,
            gameType = gameType,
            seed = seed,
            lastModified = System.currentTimeMillis()
        ))
    }
    
    suspend fun deleteSavedGame(seed: Long) {
        ProjectsDataSource.deleteGame(seed)
    }
    
    suspend fun deleteAllProjects() {
        ProjectsDataSource.clearProjects()
    }
}
