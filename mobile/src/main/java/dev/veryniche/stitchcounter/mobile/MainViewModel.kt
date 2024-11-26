package dev.veryniche.stitchcounter.mobile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.storage.ProjectsRepository
import dev.veryniche.stitchcounter.storage.datasync.Event
import dev.veryniche.stitchcounter.storage.datasync.toDeleteEvent
import dev.veryniche.stitchcounter.storage.datasync.toUpdateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedProjectsRepository: ProjectsRepository
) : ViewModel() {

    val projects = savedProjectsRepository.getProjects()

    fun getProject(id: Int) = savedProjectsRepository.getProject(id)

    private val eventsFromWatch = MutableStateFlow<Event?>(null)
    val eventsToWatch = MutableStateFlow<Event?>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            eventsFromWatch.collect { value ->
                Timber.d("Data from watch: $value")
            }
        }
    }

    suspend fun saveProject(project: Project) {
        val updatedProject = savedProjectsRepository.saveProject(project, isMobile = true)
        eventsToWatch.emit(updatedProject.toUpdateEvent())
    }

    suspend fun saveCounter(projectId: Int, counterId: Int, counterName: String, counterMax: Int) {
        saveCounter(
            projectId,
            Counter(
                id = counterId,
                name = counterName,
                maxCount = counterMax
            )
        )
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
        eventsToWatch.emit(projectId.toDeleteEvent())
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
