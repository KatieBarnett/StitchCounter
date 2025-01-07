package dev.veryniche.stitchcounter.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tiles.TileService
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.datalayer.watch.WearDataLayerAppHelper
import dagger.hilt.android.AndroidEntryPoint
import dev.veryniche.stitchcounter.data.models.ScreenOnState
import dev.veryniche.stitchcounter.tiles.counter.CounterTileService
import dev.veryniche.stitchcounter.wear.MainViewModel
import dev.veryniche.stitchcounter.wear.Screens
import dev.veryniche.stitchcounter.wear.presentation.MainActivity.Companion.EXTRA_JOURNEY_SELECT_COUNTER
import dev.veryniche.stitchcounter.wear.presentation.theme.StitchCounterTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        internal const val EXTRA_JOURNEY = "journey"
        internal const val EXTRA_JOURNEY_SELECT_COUNTER = "journey:select_counter"
    }

    lateinit var viewModel: MainViewModel

    private val dataClient by lazy { Wearable.getDataClient(this) }

    @OptIn(ExperimentalHorologistApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            val wearDataLayerRegistry = WearDataLayerRegistry.fromContext(
                application = application,
                coroutineScope = coroutineScope,
            )
            val appHelper = WearDataLayerAppHelper(
                context = context,
                registry = wearDataLayerRegistry,
                scope = coroutineScope,
            )

            viewModel = hiltViewModel<MainViewModel, MainViewModel.MainViewModelFactory> { factory ->
                factory.create(appHelper)
            }
            val dataSyncState by viewModel.eventsToMobile.collectAsStateWithLifecycle()

            LaunchedEffect(dataSyncState) {
                if (appHelper.isAvailable()) {
                    dataSyncState?.let {
                        try {
                            val request = PutDataMapRequest.create(it.path).apply {
                                dataMap.putString(it.key, it.data)
                            }
                                .asPutDataRequest()
                                .setUrgent()
                            val result = dataClient.putDataItem(request).await()
                            Timber.d("DataItem $it synced: $result")
                        } catch (cancellationException: CancellationException) {
                            throw cancellationException
                        } catch (exception: Exception) {
                            Timber.d("Syncing DataItem failed: $exception")
                        }
                    }
                }
            }
            StitchCounterWearApp(
                viewModel = viewModel,
                journey = intent.extras?.getString(EXTRA_JOURNEY),
                onTileStateUpdate = {
                    TileService.getUpdater(this)
                        .requestUpdate(CounterTileService::class.java)
                    finish()
                }
            )
        }
    }
}

@Composable
fun StitchCounterWearApp(
    viewModel: MainViewModel,
    journey: String?,
    onTileStateUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    StitchCounterTheme {
        val listState = rememberScalingLazyListState()
        val navController = rememberSwipeDismissableNavController()

        val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle(
            Screens.ProjectList,
        )
        val screenOnState by viewModel.keepScreenOnState.collectAsStateWithLifecycle(
            ScreenOnState(),
        )
        val keepCurrentScreenOn by viewModel.keepCurrentScreenOn.collectAsStateWithLifecycle(
            false,
        )

        val whatsNewToShow by viewModel.whatsNewToShow.collectAsStateWithLifecycle(
            listOf(),
        )

        val startDestination = when (journey) {
            EXTRA_JOURNEY_SELECT_COUNTER -> "select_project_for_tile"
            else -> if (whatsNewToShow.isNotEmpty()) {
                "whats_new"
            } else {
                "project_list"
            }
        }

        AmbientAware(isAlwaysOnScreen = keepCurrentScreenOn) { ambientAwareUpdate ->
            Scaffold(
                timeText = {
                    if (!listState.isScrollInProgress && !listState.canScrollBackward &&
                        ambientAwareUpdate.ambientState is AmbientState.Interactive
                    ) {
                        val leadingTextStyle =
                            TimeTextDefaults.timeTextStyle(color = MaterialTheme.colors.primary)
                        currentScreen.pageContextDisplay?.let {
                            val displayText = stringResource(it)
                            TimeText(
                                startLinearContent = {
                                    Text(
                                        text = displayText,
                                        style = leadingTextStyle
                                    )
                                },
                                startCurvedContent = {
                                    curvedText(
                                        text = displayText,
                                        style = CurvedTextStyle(leadingTextStyle)
                                    )
                                },
                            )
                        }
                    }
                },
                vignette = {
                    Vignette(vignettePosition = VignettePosition.TopAndBottom)
                },
                positionIndicator = {
                    PositionIndicator(
                        scalingLazyListState = listState
                    )
                },
                modifier = modifier
            ) {
                NavHost(
                    navController = navController,
                    startDestination = if (whatsNewToShow.isNotEmpty()) {
                        startDestination
                    } else {
                        startDestination
                    },
                    viewModel = viewModel,
                    listState = listState,
                    screenOnState = screenOnState,
                    ambientAwareState = ambientAwareUpdate,
                    onScreenOnStateUpdate = { newState ->
                        viewModel.updateScreenOnState(newState)
                    },
                    onTileStateUpdate = onTileStateUpdate,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
