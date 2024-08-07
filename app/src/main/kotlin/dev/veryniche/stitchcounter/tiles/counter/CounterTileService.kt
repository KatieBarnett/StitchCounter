package dev.veryniche.stitchcounter.tiles.counter

import CounterTileRenderer
import androidx.lifecycle.lifecycleScope
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import dagger.hilt.android.AndroidEntryPoint
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.storage.ProjectsRepository
import dev.veryniche.stitchcounter.storage.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalHorologistApi
@AndroidEntryPoint
class CounterTileService @Inject constructor() : SuspendingTileService() {

    @Inject
    lateinit var projectsRepository: ProjectsRepository

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private lateinit var renderer: CounterTileRenderer
    private lateinit var tileStateFlow: StateFlow<CounterTileState?>
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        renderer = CounterTileRenderer(this)
        scope.launch {
            tileStateFlow = combine(
                userPreferencesRepository.userPreferencesFlow,
                projectsRepository.getProjects()
            ) { userPreferences, projects ->
                val project = projects.find { it.id == userPreferences.tileProjectId }
                CounterTileState(
                    counter = project?.counters?.find { it.id == userPreferences.tileCounterId },
                    project = project
                )
            }.stateIn(
                lifecycleScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
        }
    }

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        val tileState = latestTileState()
        return renderer.renderTimeline(tileState, requestParams)
    }

    /**
     * Reads the latest state from the flow, and updates the data if there isn't any.
     */
    private suspend fun latestTileState(): CounterTileState {
        var tileState = tileStateFlow.filterNotNull().first()

        // see `refreshData()` docs for more information
//        if (tileState.contacts.isEmpty()) {
//            refreshData()
//            tileState = tileStateFlow.filterNotNull().first()
//        }
        return tileState
    }

    suspend fun updateCounter(project: Project, counter: Counter) {
        val counterIndex = project.counters.indexOfFirst { it.id == counter.id }
        if (counterIndex != -1) {
            val updatedList = project.counters.toMutableList()
            updatedList[counterIndex] = counter
            saveProject(project.copy(counters = updatedList))
        }
    }

    suspend fun resetCounter(project: Project, counter: Counter) {
        updateCounter(project, counter.copy(currentCount = 0))
    }

    suspend fun saveProject(project: Project) {
        projectsRepository.saveProject(project)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    /**
     * If our data source (the repository) is empty/has stale data, this is where we could perform
     * an update. For this sample, we're updating the repository with fake data
     * ([MessagingRepo.knownContacts]).
     *
     * In a more complete example, tiles, complications and the main app would
     * share a common data source so it's less likely that an initial data refresh triggered by the
     * tile would be necessary.
     */
    private suspend fun refreshData() {
//        repo.updateContacts(MessagingRepo.knownContacts)
    }

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): ResourceBuilders.Resources {
        // TODO change this
        return renderer.produceRequestedResources(
            Counter(
                id = 3,
                name = "pattern",
                currentCount = 40000,
                maxCount = 50000,
            ),
            requestParams
        )
    }
}
