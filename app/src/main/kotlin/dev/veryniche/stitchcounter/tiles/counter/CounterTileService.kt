package dev.veryniche.stitchcounter.tiles.counter

import CounterTileRenderer
import CounterTileRenderer.Companion.ID_CLICKABLE_DECREMENT
import CounterTileRenderer.Companion.ID_CLICKABLE_INCREMENT
import CounterTileRenderer.Companion.ID_CLICKABLE_RESET
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
        var tileState = latestTileState()
        tileState.let { currentState ->
            if (currentState.project != null && currentState.counter != null) {
                when (requestParams.currentState.lastClickableId) {
                    ID_CLICKABLE_INCREMENT -> {
                        updateCounter(
                            currentState.project,
                            currentState.counter.copy(currentCount = currentState.counter.currentCount + 1)
                        )
                        tileState = latestTileState()
                    }
                    ID_CLICKABLE_DECREMENT -> {
                        updateCounter(
                            currentState.project,
                            currentState.counter.copy(currentCount = currentState.counter.currentCount - 1)
                        )
                        tileState = latestTileState()
                    }
                    ID_CLICKABLE_RESET -> {
                        resetCounter(
                            currentState.project,
                            currentState.counter
                        )
                        tileState = latestTileState()
                    }
                }
            }
        }

        return renderer.renderTimeline(tileState, requestParams)
    }

    private suspend fun latestTileState(): CounterTileState {
        return tileStateFlow.filterNotNull().first()
    }

    private suspend fun updateCounter(project: Project, counter: Counter) {
        val counterIndex = project.counters.indexOfFirst { it.id == counter.id }
        if (counterIndex != -1) {
            val updatedList = project.counters.toMutableList()
            updatedList[counterIndex] = counter
            saveProject(project.copy(counters = updatedList))
        }
    }

    private suspend fun resetCounter(project: Project, counter: Counter) {
        updateCounter(project, counter.copy(currentCount = 0))
    }

    private suspend fun saveProject(project: Project) {
        projectsRepository.saveProject(project)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): ResourceBuilders.Resources {
        return renderer.produceRequestedResources(
            1,
            requestParams
        )
    }
}
