package dev.veryniche.stitchcounter.wear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.AppHelperResultCode.APP_HELPER_RESULT_SUCCESS
import com.google.android.horologist.data.activityConfig
import com.google.android.horologist.datalayer.watch.WearDataLayerAppHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.data.models.ScreenOnState
import dev.veryniche.stitchcounter.storage.ProjectsRepository
import dev.veryniche.stitchcounter.storage.UserPreferencesRepository
import dev.veryniche.stitchcounter.storage.datasync.Event
import dev.veryniche.stitchcounter.storage.datasync.toDeleteEvent
import dev.veryniche.stitchcounter.storage.datasync.toUpdateAllEvent
import dev.veryniche.stitchcounter.storage.datasync.toUpdateEvent
import dev.veryniche.stitchcounter.wear.presentation.whatsnew.whatsNewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalHorologistApi::class)
@HiltViewModel(assistedFactory = MainViewModel.MainViewModelFactory::class)
class MainViewModel
@OptIn(ExperimentalHorologistApi::class)
@AssistedInject
constructor(
    @Assisted val appHelper: WearDataLayerAppHelper,
    private val savedProjectsRepository: ProjectsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    @AssistedFactory
    interface MainViewModelFactory {
        @OptIn(ExperimentalHorologistApi::class)
        fun create(appHelper: WearDataLayerAppHelper): MainViewModel
    }

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    val whatsNewToShow = userPreferencesFlow.map {
        it.whatsNewLastSeen
    }.map { lastSeenId ->
        whatsNewData.filter { it.id > lastSeenId }.sortedBy { it.id }
    }

    val isProPurchased = userPreferencesFlow.map {
        it.isProPurchased
    }

    val isConnectedAppInfoDoNotShow = userPreferencesFlow.map {
        it.isConectedAppInfoDoNotShow
    }

    val phoneState: Flow<PhoneState> = appHelper.connectedAndInstalledNodes.map { connectedNodes ->
        PhoneState(
            phoneConnected = connectedNodes.isNotEmpty(),
            appInstalledOnPhoneList = connectedNodes.map { it.id },
        )
    }

    private val eventsFromMobile = MutableStateFlow<Event?>(null)
    val eventsToMobile = MutableStateFlow<Event?>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            eventsFromMobile.collect { value ->
                Timber.d("Data from phone: $value")
            }
        }
        viewModelScope.launch {
            if (appHelper.isAvailable()) {
                phoneState.collectLatest {
                    if (it.appInstalledOnPhoneList.isNotEmpty()) {
                        Timber.d("Syncing all projects")
                        syncAllProjects()
                    }
                }
            } else {
                Timber.e("API not available")
            }
        }
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

    suspend fun saveProjectName(projectId: Int?, projectName: String) {
        val updatedProject = savedProjectsRepository.saveProjectName(
            Project(
                id = projectId,
                name = projectName
            ),
            isMobile = false
        )
        eventsToMobile.emit(updatedProject.toUpdateEvent())
    }

    suspend fun saveProject(project: Project) {
        val updatedProject = savedProjectsRepository.saveProject(project, isMobile = false)
        eventsToMobile.emit(updatedProject.toUpdateEvent())
    }

    suspend fun syncAllProjects() {
        val projects = projects.first()
        eventsToMobile.emit(projects.toUpdateAllEvent())
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
        eventsToMobile.emit(projectId.toDeleteEvent())
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

    fun updateIsConnectedAppInfoDoNotShow(isConnectedAppInfoDoNotShow: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateIsConnectedAppInfoDoNotShow(isConnectedAppInfoDoNotShow)
        }
    }

    suspend fun openAppOnPhoneForPurchase(state: PhoneState) {
        val installedNodeId = state.appInstalledOnPhoneList.firstOrNull()
        if (installedNodeId != null) {
            val config = activityConfig {
                classFullName = "dev.veryniche.stitchcounter.mobile.PurchaseProActivity"
            }
            val result = appHelper.startRemoteActivity(installedNodeId, config)
            if (result != APP_HELPER_RESULT_SUCCESS) {
                Timber.e("Error opening PurchaseProActivity on phone, error code: $result")
                appHelper.installOnNode(installedNodeId)
            }
        } else {
            appHelper.connectedNodes().forEach {
                appHelper.installOnNode(it.id)
            }
        }
    }

    suspend fun openAppOnPhone(state: PhoneState) {
        val installedNodeId = state.appInstalledOnPhoneList.firstOrNull()
        if (installedNodeId != null) {
            val result = appHelper.startRemoteOwnApp(installedNodeId)
            if (result != APP_HELPER_RESULT_SUCCESS) {
                Timber.e("Error opening PurchaseProActivity on phone, error code: $result")
                appHelper.installOnNode(installedNodeId)
            }
        } else {
            appHelper.connectedNodes().forEach {
                val result = appHelper.installOnNode(it.id)
                if (result != APP_HELPER_RESULT_SUCCESS) {
                    Timber.e("Error installing app on phone, error code: $result")
                }
            }
        }
    }
}

data class PhoneState(
    val appInstalledOnPhoneList: List<String> = listOf(),
    val phoneConnected: Boolean = false,
)
