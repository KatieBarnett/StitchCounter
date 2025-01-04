package dev.veryniche.stitchcounter.mobile

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import dev.veryniche.stitchcounter.storage.ProjectsRepository
import dev.veryniche.stitchcounter.storage.ThemeMode
import dev.veryniche.stitchcounter.storage.UserPreferencesRepository
import dev.veryniche.stitchcounter.storage.datasync.Event
import dev.veryniche.stitchcounter.storage.datasync.toDeleteEvent
import dev.veryniche.stitchcounter.storage.datasync.toUpdateAllEvent
import dev.veryniche.stitchcounter.storage.datasync.toUpdateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = MainViewModel.MainViewModelFactory::class)
class MainViewModel @AssistedInject constructor(
    @Assisted val purchaseManager: PurchaseManager,
    private val savedProjectsRepository: ProjectsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    @AssistedFactory
    interface MainViewModelFactory {
        fun create(purchaseManager: PurchaseManager): MainViewModel
    }

    val purchaseStatus = purchaseManager.purchases.combine(
        purchaseManager.subscriptions
    ) { purchases, activeSubscriptions ->
        PurchaseStatus(isBundleSubscribed = activeSubscriptions.contains(Products.bundle))
    }

    val availableSubscriptions: Flow<List<Subscription>> = purchaseManager.availableSubscriptions
        .combine(purchaseManager.subscriptions) { availableProducts, activeSubscriptions ->
            availableProducts.map {
                it.copy(purchased = activeSubscriptions.contains(it.productId))
            }
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
        viewModelScope.launch {
            purchaseManager.connectToBilling()
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
        } else {
            val updatedList = project.counters.plus(counter)
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

    private val reviewManager = ReviewManager(userPreferencesRepository)

    fun requestReviewIfAble(activity: Activity) {
        viewModelScope.launch {
            reviewManager.requestReviewIfAble(activity, this)
        }
    }
}
