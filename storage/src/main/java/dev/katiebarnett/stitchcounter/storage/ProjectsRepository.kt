package dev.katiebarnett.stitchcounter.storage

import dev.katiebarnett.stitchcounter.data.models.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectsRepository @Inject constructor(
    private val projectsDataSource: ProjectsDataSource
) {

    fun getProjects(): Flow<List<Project>> {
        return projectsDataSource.projectsFlow.map { it.map { it.fromSavedProject() } }
    }

    fun getProject(id: Int): Flow<List<Project>> {
        return getProjects().map { it.filter { it.id == id } }
    }

    suspend fun saveProject(project: Project) {
        projectsDataSource.saveProject(project.copy(lastModified = System.currentTimeMillis()).toSavedProject())
    }

    suspend fun deleteProjectGame(id: Int) {
        projectsDataSource.deleteProject(id)
    }

    suspend fun deleteAllProjects() {
        projectsDataSource.clearProjects()
    }
}
