package dev.veryniche.stitchcounter.storage

import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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

    fun getProject(id: Int): Flow<Project?> {
        return getProjects().map { it.firstOrNull { it.id == id } }
    }

    suspend fun saveProjectName(project: Project, isMobile: Boolean): Project {
        val updatedProject = project.id?.let {
            getProject(it).firstOrNull()?.copy(name = project.name, lastModified = System.currentTimeMillis())
        } ?: project.copy(
            id = projectsDataSource.getNextId(isMobile),
            lastModified = System.currentTimeMillis()
        )
        projectsDataSource.saveProject(updatedProject)
        return updatedProject
    }
    suspend fun saveProject(project: Project, isMobile: Boolean): Project {
        val updatedProject = project.id?.let {
            project.copy(lastModified = System.currentTimeMillis())
        } ?: project.copy(
            id = projectsDataSource.getNextId(isMobile),
            lastModified = System.currentTimeMillis()
        )
        projectsDataSource.saveProject(updatedProject)
        return updatedProject
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
            projectsDataSource.saveProject(
                project.copy(
                    counters = counters,
                    lastModified = System.currentTimeMillis()
                ),
            )
        }
    }

    suspend fun deleteProject(projectId: Int) {
        projectsDataSource.deleteProject(projectId)
    }

    suspend fun deleteAllProjects() {
        projectsDataSource.clearProjects()
    }
}
