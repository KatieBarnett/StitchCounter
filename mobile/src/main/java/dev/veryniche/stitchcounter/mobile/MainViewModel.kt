package dev.veryniche.stitchcounter.mobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.AppHelperResultCode.APP_HELPER_RESULT_SUCCESS
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.data.models.ScreenOnState
import dev.veryniche.stitchcounter.mobile.purchase.Products
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseManager
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.purchase.Subscription
import dev.veryniche.stitchcounter.mobile.review.ReviewManager
import dev.veryniche.stitchcounter.mobile.whatsnew.whatsNewData
import dev.veryniche.stitchcounter.storage.ProjectsRepository
import dev.veryniche.stitchcounter.storage.ThemeMode
import dev.veryniche.stitchcounter.storage.UserPreferencesRepository
import dev.veryniche.stitchcounter.storage.datasync.DataLayerListenerService.Companion.KEY_PRO_PURCHASED
import dev.veryniche.stitchcounter.storage.datasync.DataLayerListenerService.Companion.PRO_PURCHASED_PATH
import dev.veryniche.stitchcounter.storage.datasync.Event
import dev.veryniche.stitchcounter.storage.datasync.toDeleteEvent
import dev.veryniche.stitchcounter.storage.datasync.toUpdateAllEvent
import dev.veryniche.stitchcounter.storage.datasync.toUpdateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalHorologistApi::class)
@HiltViewModel(assistedFactory = MainViewModel.MainViewModelFactory::class)
class MainViewModel
@OptIn(ExperimentalHorologistApi::class)
@AssistedInject
constructor(
    @Assisted val purchaseManager: PurchaseManager,
    @Assisted val appHelper: PhoneDataLayerAppHelper,
    private val savedProjectsRepository: ProjectsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    @AssistedFactory
    interface MainViewModelFactory {
        @OptIn(ExperimentalHorologistApi::class)
        fun create(purchaseManager: PurchaseManager, appHelper: PhoneDataLayerAppHelper): MainViewModel
    }

    val purchaseStatus = purchaseManager.subscriptions.map { activeSubscriptions ->
        val isProPurchased = activeSubscriptions?.let {
            val isProPurchased = it.contains(Products.bundle)
            eventsToWatch.emit(
                Event(
                    path = PRO_PURCHASED_PATH,
                    key = KEY_PRO_PURCHASED,
                    data = isProPurchased.toString()
                )
            )
            isProPurchased
        }
        PurchaseStatus(isBundleSubscribed = isProPurchased == true)
    }

    val availableSubscriptions: Flow<List<Subscription>> = purchaseManager.availableSubscriptions
        .combine(purchaseManager.subscriptions) { availableProducts, activeSubscriptions ->
            availableProducts?.map {
                it.copy(purchased = activeSubscriptions?.contains(it.productId))
            } ?: emptyList()
        }

    fun purchaseSubscription(productId: String, offerToken: String, onError: (message: Int) -> Unit) {
        viewModelScope.launch {
            purchaseManager.purchaseSubscription(
                productId = productId,
                offerToken = offerToken,
                onError = onError
            )
        }
    }

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    val keepScreenOnState = userPreferencesFlow.map {
        it.keepScreenOn
    }

    val themeMode = userPreferencesFlow.map {
        it.themeMode
    }

    val whatsNewToShow = userPreferencesFlow.map {
        it.whatsNewLastSeen
    }.map { lastSeenId ->
        whatsNewData.filter { it.id > lastSeenId }.sortedBy { it.id }
    }

    val projects = savedProjectsRepository.getProjects()

    fun getProject(id: Int) = savedProjectsRepository.getProject(id)

    private val eventsFromWatch = MutableStateFlow<Event?>(null)
    val eventsToWatch = MutableStateFlow<Event?>(null)

    private var isWatchConnected = MutableStateFlow<Boolean>(false)

    val watchState = appHelper.connectedAndInstalledNodes
        .combine(isWatchConnected) { connectedNodes, isWatchConnected ->
            WatchState(
                watchConnected = isWatchConnected,
                appInstalledOnWatchList = connectedNodes.map { it.id },
            )
        }

    val isUninstalledWatchAvailable = flowOf(false)

    val isConnectedAppInfoDoNotShow = userPreferencesFlow.map {
        it.isConnectedAppInfoDoNotShow
    }

    init {
        viewModelScope.launch {
            if (appHelper.isAvailable()) {
                isWatchConnected.emit(appHelper.connectedNodes().isNotEmpty())
            }
        }
        viewModelScope.launch {
            if (appHelper.isAvailable()) {
                watchState.map {
                    it.watchConnected && it.appInstalledOnWatchList.isEmpty()
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            eventsFromWatch.collect { value ->
                Timber.d("Data from watch: $value")
            }
        }
        viewModelScope.launch {
            purchaseManager.connectToBilling()
        }
        viewModelScope.launch {
            if (appHelper.isAvailable()) {
                watchState.collectLatest {
                    if (it.appInstalledOnWatchList.isNotEmpty()) {
                        Timber.d("Syncing all projects")
                        syncAllProjects()
                    }
                }
            } else {
                Timber.e("API not available")
            }
        }
    }

    suspend fun saveProject(project: Project): Int {
        val updatedProject = savedProjectsRepository.saveProject(project, isMobile = true)
        eventsToWatch.emit(updatedProject.toUpdateEvent())
        return updatedProject.id ?: -1
    }

    suspend fun syncAllProjects() {
        val projects = projects.first()
        eventsToWatch.emit(projects.toUpdateAllEvent())
    }

    suspend fun updateCounter(project: Project, counter: Counter) {
        val counterIndex = project.counters.indexOfFirst { it.id == counter.id }
        if (counterIndex != -1) {
            val updatedList = project.counters.toMutableList()
            updatedList[counterIndex] = counter
            saveProject(project.copy(counters = updatedList))
        } else {
            val updatedList = project.counters.plus(counter)
            saveProject(project.copy(counters = updatedList))
        }
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

    fun updateScreenOnState(screenOnState: ScreenOnState) {
        viewModelScope.launch {
            userPreferencesRepository.updateKeepScreenOn(screenOnState)
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.updateThemeMode(themeMode)
        }
    }

    fun updateIsConnectedAppInfoDoNotShow(isConnectedAppInfoDoNotShow: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateIsConnectedAppInfoDoNotShow(isConnectedAppInfoDoNotShow)
        }
    }

    fun updateWhatsNewLastSeen(whatsNewLastSeen: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateWhatsNewLastSeen(whatsNewLastSeen)
        }
    }

    private val reviewManager = ReviewManager(userPreferencesRepository)

    fun requestReviewIfAble(activity: Activity) {
        viewModelScope.launch {
            reviewManager.requestReviewIfAble(activity, this)
        }
    }

    fun installAppOnWatch(context: Context) {
        viewModelScope.launch {
            appHelper.connectedNodes().forEach {
                val result = appHelper.installOnNode(it.id)
                if (result != APP_HELPER_RESULT_SUCCESS) {
                    Timber.e("Error installing app on watch, error code: $result")
                }
            }
        }
        val intent = Intent(Intent.ACTION_VIEW)
            .setData(Uri.parse("https://play.google.com/store/apps/details?id=dev.veryniche.stitchcounter"))
        context.startActivity(intent)
    }
}

data class WatchState(
    val appInstalledOnWatchList: List<String> = listOf(),
    val watchConnected: Boolean = false,
)
