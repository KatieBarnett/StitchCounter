package dev.veryniche.stitchcounter.mobile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.storage.ProjectsRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedProjectsRepository: ProjectsRepository
) : ViewModel() {
    
    val projects = savedProjectsRepository.getProjects()

    fun getProject(id: Int) = savedProjectsRepository.getProject(id)

    suspend fun saveProject(projectName: String) {
        saveProject(null, projectName)
    }

    suspend fun saveProject(projectId: Int?, projectName: String) {
        savedProjectsRepository.saveProject(
            Project(
                id = projectId,
                name = projectName
            )
        )
    }

    suspend fun saveProjectName(projectId: Int?, projectName: String) {
        savedProjectsRepository.saveProjectName(
            Project(
                id = projectId,
                name = projectName
            )
        )
    }

    suspend fun saveProject(project: Project) {
        savedProjectsRepository.saveProject(project)
    }

    suspend fun saveCounter(projectId: Int, counterId: Int, counterName: String, counterMax: Int) {
        saveCounter(projectId, Counter(
            id = counterId,
            name = counterName,
            maxCount = counterMax
        ))
    }

    suspend fun saveCounter(projectId: Int, counter: Counter) {
        savedProjectsRepository.saveCounter(projectId, counter)
    }
    
    suspend fun updateCounter(project: Project, counter: Counter) {
        val counterIndex = project.counters.indexOfFirst { it.id == counter.id }
        if (counterIndex != -1) {
            val updatedList = project.counters.toMutableList()
            updatedList[counterIndex] = counter
            saveProject(project.copy(counters = updatedList))
        }
    }

    suspend fun resetProject(project: Project) {
        val updatedList = project.counters.map { 
            it.copy(currentCount = 0)
        }
        saveProject(project.copy(counters = updatedList))
    }
    
    suspend fun resetCounter(project: Project, counter: Counter) {
        updateCounter(project, counter.copy(currentCount = 0))
    }

    suspend fun deleteProject(projectId: Int) {
        savedProjectsRepository.deleteProject(projectId)
    }

    suspend fun deleteCounter(projectId: Int, counterId: Int) {
        getProject(projectId).firstOrNull()?.let { project ->
            val counterIndex = project.counters.indexOfFirst { it.id == counterId }
            if (counterIndex != -1) {
                val updatedList = project.counters.toMutableList()
                updatedList.removeAt(counterIndex)
                saveProject(project.copy(counters = updatedList))
            }
        }
    }
}