package dev.katiebarnett.stitchcounter.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.stitchcounter.MainViewModel
import dev.katiebarnett.stitchcounter.presentation.theme.StitchCounterTheme

/**
 * Simple "Hello, World" app meant as a starting point for a new project using Compose for Wear OS.
 *
 * Displays only a centered [Text] composable, and the actual text varies based on the shape of the
 * device (round vs. square/rectangular).
 *
 * If you plan to have multiple screens, use the Wear version of Compose Navigation. You can carry
 * over your knowledge from mobile and it supports the swipe-to-dismiss gesture (Wear OS's
 * back action). For more information, go here:
 * https://developer.android.com/reference/kotlin/androidx/wear/compose/navigation/package-summary
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StitchCounterWearApp()
        }
    }
}

@Composable
fun StitchCounterWearApp(modifier: Modifier = Modifier) {
    StitchCounterTheme {
        val listState = rememberScalingLazyListState()
        val viewModel: MainViewModel = hiltViewModel()
        val navController = rememberSwipeDismissableNavController()
        Scaffold(
            timeText = {
                if (!listState.isScrollInProgress) {
                    TimeText()
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
            NavHost(navController = navController, viewModel = viewModel, listState = listState)
        }
    }
}
