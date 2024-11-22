package dev.veryniche.stitchcounter.wear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.data.models.ScreenOnState
import dev.veryniche.stitchcounter.storage.ProjectsRepository
import dev.veryniche.stitchcounter.storage.UserPreferencesRepository
import dev.veryniche.stitchcounter.wear.presentation.whatsnew.whatsNewData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedProjectsRepository: ProjectsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    val whatsNewToShow = userPreferencesFlow.map {
        it.whatsNewLastSeen
    }.map { lastSeenId ->
        whatsNewData.filter { it.id > lastSeenId }.sortedBy { it.id }
    }

    val keepScreenOnState = userPreferencesFlow.map {
        it.keepScreenOn
    }

    private val _currentScreen = MutableStateFlow<Screens>(Screens.ProjectList)
    val currentScreen = _currentScreen.asStateFlow()

    val keepCurrentScreenOn = currentScreen.combine(keepScreenOnState) { currentScreen, keepScreenOnState ->
        currentScreen.showScreenAlwaysOn(keepScreenOnState)
    }

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

    fun updateCurrentScreen(screen: Screens) {
        viewModelScope.launch {
            _currentScreen.emit(screen)
        }
    }

    fun updateScreenOnState(screenOnState: ScreenOnState) {
        viewModelScope.launch {
            userPreferencesRepository.updateKeepScreenOn(screenOnState)
        }
    }

    fun updateTileState(projectId: Int, counterId: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateTileProjectId(projectId)
            userPreferencesRepository.updateTileCounterId(counterId)
        }
    }

    fun updateWhatsNewLastSeen(whatsNewLastSeen: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateWhatsNewLastSeen(whatsNewLastSeen)
        }
    }
}
