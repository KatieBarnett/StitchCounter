package dev.veryniche.stitchcounter.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
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
import dagger.hilt.android.AndroidEntryPoint
import dev.veryniche.stitchcounter.MainViewModel
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.presentation.theme.StitchCounterTheme

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
        
        val pageContextState by viewModel.pageContext.observeAsState(stringResource(id = R.string.app_name))
        
        Scaffold(
            timeText = {
                if (!listState.isScrollInProgress) {
                    val leadingTextStyle = TimeTextDefaults.timeTextStyle(color = MaterialTheme.colors.primary)
                    TimeText(
                        startLinearContent = {
                            Text(
                                text = pageContextState,
                                style = leadingTextStyle
                            )
                        },
                        startCurvedContent = {
                            curvedText(
                                text = pageContextState,
                                style = CurvedTextStyle(leadingTextStyle)
                            )
                        },
                    )
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
            NavHost(navController = navController, viewModel = viewModel, listState = listState, modifier = Modifier.fillMaxSize())
        }
    }
}
