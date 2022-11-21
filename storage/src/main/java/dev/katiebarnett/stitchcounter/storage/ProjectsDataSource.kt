package dev.katiebarnett.stitchcounter.storage

import androidx.datastore.core.DataStore
import dev.katiebarnett.stitchcounter.storage.models.Projects
import dev.katiebarnett.stitchcounter.storage.models.SavedProject
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProjectsDataSource @Inject constructor(
    private val projectsStore: DataStore<Projects>
) {

    companion object {
        internal const val PROTO_FILE_NAME = "projects.pb"
    }

    val projectsFlow = projectsStore.data
        .map { it ->
            it.projectList.sortedByDescending { it.lastModified }
        }

    suspend fun saveProject(project: SavedProject) {
        projectsStore.updateData { currentProjects ->
            val currentIndex = currentProjects.projectList.indexOfFirst { it.id == project.id }
            if (currentIndex != -1) {
                currentProjects.toBuilder().setProject(currentIndex, project).build()
            } else {
                currentProjects.toBuilder().addProject(project).build()
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
