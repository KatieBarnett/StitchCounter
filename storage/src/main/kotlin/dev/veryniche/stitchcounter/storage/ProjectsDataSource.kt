package dev.veryniche.stitchcounter.storage

import androidx.datastore.core.DataStore
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.storage.models.Projects
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class ProjectsDataSource @Inject constructor(
    private val projectsStore: DataStore<Projects>
) {

    companion object {
        internal const val PROTO_FILE_NAME = "projects.pb"
        internal const val MOBILE_ID_MIN = 10000
    }

    val projectsFlow = projectsStore.data
        .map { it.projectList.sortedByDescending { it.lastModified } }

    suspend fun getNextId(isMobile: Boolean): Int {
        val currentIds = projectsStore.data.first().projectList.map { it.id }
        val maxValue = currentIds.maxOrNull() ?: 0
        return when {
            isMobile -> {
                if (maxValue >= MOBILE_ID_MIN) {
                    maxValue + 1
                } else {
                    MOBILE_ID_MIN
                }
            }
            else -> {
                if (maxValue < MOBILE_ID_MIN) {
                    maxValue + 1
                } else {
                    currentIds.filter { it < MOBILE_ID_MIN }.maxOrNull()?.plus(1) ?: 0
                }
            }
        }
    }

    suspend fun saveProject(project: Project) {
        if (project.id == null) {
            Timber.e("Error - project id is null")
        }
        projectsStore.updateData { data ->
            val currentIndex = data.projectList.indexOfFirst { it.id == project.id }
            if (currentIndex != -1) {
                data.toBuilder().setProject(currentIndex, project.toSavedProject()).build()
            } else {
                data.toBuilder().addProject(project.toSavedProject()).build()
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
