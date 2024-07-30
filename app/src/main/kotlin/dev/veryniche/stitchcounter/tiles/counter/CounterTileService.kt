package dev.veryniche.stitchcounter.tiles.counter

import CounterTileRenderer
import androidx.lifecycle.lifecycleScope
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import dev.veryniche.stitchcounter.data.models.Counter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@ExperimentalHorologistApi
class CounterTileService : SuspendingTileService() {

//    private lateinit var repo: MessagingRepo
    private lateinit var renderer: CounterTileRenderer
    private lateinit var tileStateFlow: StateFlow<CounterTileState?>

    override fun onCreate() {
        super.onCreate()
//        repo = MessagingRepo(this)
        renderer = CounterTileRenderer(this)
        tileStateFlow = flowOf(
            CounterTileState(
                counter = Counter(
                    id = 3,
                    name = "pattern",
                    currentCount = 40000,
                    maxCount = 50000,
                ),
                projectName = "Test Project"
            )
        ).stateIn(
            lifecycleScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
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
        // Since we know there's only 2 very small avatars, we'll fetch them
        // as part of this resource request.
//        val avatars = imageLoader.fetchAvatarsFromNetwork(
//            context = this@MessagingTileService,
//            requestParams = requestParams,
//            tileState = latestTileState()
//        )
        // then pass the bitmaps to the renderer to transform them to ImageResources

        // TODO change this
        return renderer.produceRequestedResources(Counter(
            id = 3,
            name = "pattern",
            currentCount = 40000,
            maxCount = 50000,
        ), requestParams)
    }
}
