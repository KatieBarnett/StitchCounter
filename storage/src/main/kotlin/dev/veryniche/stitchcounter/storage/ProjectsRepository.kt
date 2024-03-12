package dev.veryniche.stitchcounter.storage

import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.take
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectsRepository @Inject constructor(
    private val projectsDataSource: ProjectsDataSource
) {

    fun getProjects(): Flow<List<Project>> {
        return projectsDataSource.projectsFlow.map { it.map { it.fromSavedProject() } }
    }

    fun getProject(id: Int): Flow<Project?> {
        return getProjects().map { it.firstOrNull { it.id == id } }
    }

    suspend fun saveProjectName(updatedProject: Project) {
        updatedProject.id?.let {
            getProject(it).firstOrNull()?.let { project ->
                projectsDataSource.saveProject(
                    project.copy(name = updatedProject.name, lastModified = System.currentTimeMillis())
                )
            } ?: projectsDataSource.saveProject(
                updatedProject.copy(name = updatedProject.name, lastModified = System.currentTimeMillis())
            )
        } ?: projectsDataSource.saveProject(
            updatedProject.copy(name = updatedProject.name, lastModified = System.currentTimeMillis())
        )
    }

    suspend fun saveProject(updatedProject: Project) {
        updatedProject.id?.let {
            getProject(it).firstOrNull()?.let { project ->
                projectsDataSource.saveProject(
                    updatedProject.copy(lastModified = System.currentTimeMillis())
                )
            } ?: projectsDataSource.saveProject(
                updatedProject.copy(lastModified = System.currentTimeMillis())
            )
        } ?: projectsDataSource.saveProject(
            updatedProject.copy(lastModified = System.currentTimeMillis())
        )
    }

    suspend fun saveCounter(projectId: Int, counter: Counter) {
        getProject(projectId).firstOrNull()?.let { project ->
            val counters = project.counters.toMutableList()
            val index = counters.indexOfFirst { it.id == counter.id }
            if (index != -1) {
                counters[index] = counters[index].copy(name = counter.name, maxCount = counter.maxCount)
            } else {
                counters.add(counter)
            }
            projectsDataSource.saveProject(project.copy(counters = counters, lastModified = System.currentTimeMillis()))
        }
    }

    suspend fun deleteProject(projectId: Int) {
        projectsDataSource.deleteProject(projectId)
    }
    
    suspend fun deleteAllProjects() {
        projectsDataSource.clearProjects()
    }
}
