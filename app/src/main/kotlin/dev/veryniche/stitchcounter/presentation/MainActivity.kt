package dev.veryniche.stitchcounter.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import dagger.hilt.android.AndroidEntryPoint
import dev.veryniche.stitchcounter.MainViewModel
import dev.veryniche.stitchcounter.Screens
import dev.veryniche.stitchcounter.data.models.ScreenOnState
import dev.veryniche.stitchcounter.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.presentation.whatsnew.WhatsNewDialog
import dev.veryniche.stitchcounter.tiles.counter.CounterTileService

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        internal const val EXTRA_JOURNEY = "journey"
        internal const val EXTRA_JOURNEY_SELECT_COUNTER = "journey:select_counter"
    }

    @OptIn(ExperimentalHorologistApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        val startDestination = when (intent.extras?.getString(EXTRA_JOURNEY)) {
            EXTRA_JOURNEY_SELECT_COUNTER -> "select_project_for_tile"
            else -> "project_list"
        }

        setContent {
            StitchCounterWearApp(
                startDestination = startDestination,
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
    startDestination: String,
    onTileStateUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    StitchCounterTheme {
        val listState = rememberScalingLazyListState()
        val viewModel: MainViewModel = hiltViewModel()
        val navController = rememberSwipeDismissableNavController()

        val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle(
            Screens.ProjectList,
            lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
        )
        val screenOnState by viewModel.keepScreenOnState.collectAsStateWithLifecycle(
            ScreenOnState(),
            lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
        )
        val keepCurrentScreenOn by viewModel.keepCurrentScreenOn.collectAsStateWithLifecycle(
            false,
            lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
        )

        val whatsNewToShow by viewModel.whatsNewToShow.collectAsStateWithLifecycle(
            listOf(),
            lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
        )

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
                    startDestination = startDestination,
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

                if (whatsNewToShow.isNotEmpty()) {
                    WhatsNewDialog(whatsNewToShow, {}, Modifier)
                }
            }
        }
    }
}
