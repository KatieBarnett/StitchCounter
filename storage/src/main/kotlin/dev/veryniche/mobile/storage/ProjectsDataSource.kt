package dev.veryniche.mobile.storage

import androidx.datastore.core.DataStore
import dev.veryniche.mobile.data.models.Project
import dev.veryniche.mobile.storage.models.Projects
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProjectsDataSource @Inject constructor(
    private val projectsStore: DataStore<Projects>
) {

    companion object {
        internal const val PROTO_FILE_NAME = "projects.pb"
    }
    
    val projectsFlow = projectsStore.data
        .map { it.projectList.sortedByDescending { it.lastModified } }

    suspend fun saveProject(project: Project) {
        projectsStore.updateData { data ->
            val nextId = (data.projectList.maxByOrNull { it.id }?.id ?: -1) + 1
            val updatedProject = if (project.id == null) {
                project.copy(id = nextId)
            } else {
                project
            }
            val currentIndex = data.projectList.indexOfFirst { it.id == updatedProject.id }
            if (currentIndex != -1) {
                data.toBuilder().setProject(currentIndex, updatedProject.toSavedProject()).build()
            } else {
                data.toBuilder().addProject(updatedProject.toSavedProject()).build()
            }
        }
    }

    suspend fun deleteProject(id: Int) {
        projectsStore.updateData { currentProjects ->
            val currentIndex = currentProjects.projectList.indexOfFirst { it.id == id }
            if (currentIndex != -1) {
                currentProjects.toBuilder().removeProject(currentIndex).build()
            } else {
                // Do nothing
                currentProjects.toBuilder().build()
            }
        }
    }

    suspend fun clearProjects() {
        projectsStore.updateData { currentProjects ->
            currentProjects.toBuilder().clearProject().build()
        }
    }
}
