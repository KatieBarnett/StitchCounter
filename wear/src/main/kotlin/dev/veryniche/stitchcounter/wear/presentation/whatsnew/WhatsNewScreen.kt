package dev.veryniche.stitchcounter.wear.presentation.whatsnew

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.presentation.ListTitle
import dev.veryniche.stitchcounter.presentation.theme.Dimen
import dev.veryniche.stitchcounter.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.previews.PreviewScreen
import dev.veryniche.stitchcounter.util.Analytics
import dev.veryniche.stitchcounter.util.TrackedScreen
import dev.veryniche.stitchcounter.util.trackScreenView
import kotlinx.coroutines.launch

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun WhatsNewScreen(
    listState: ScalingLazyListState,
    data: List<WhatsNewData>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    TrackedScreen {
        trackScreenView(name = Analytics.Screen.WhatsNew)
    }
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = rememberActiveFocusRequester()

    ScalingLazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimen.spacingDouble),
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(Dimen.spacingDouble)
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    listState.scrollBy(it.verticalScrollPixels)
                    listState.animateScrollBy(0f)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
    ) {
        item {
            ListHeader(Modifier) {
                ListTitle(
                    stringResource(R.string.whats_new_title),
                    modifier = Modifier.padding(top = Dimen.spacingQuad)
                )
            }
        }
        data.forEach {
            it.text.forEach {
                item {
                    Text(text = it.text)
                }
            }
        }
        item {
            Chip(
                onClick = onClose,
                colors = ChipDefaults.primaryChipColors(),
                modifier = modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                label = {
                    Text(
                        text = stringResource(R.string.whats_new_close),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        }
    }
}

@PreviewScreen
@Composable
fun WhatsNewDialogPreview() {
    StitchCounterTheme {
        WhatsNewScreen(
            listState = rememberScalingLazyListState(),
            data = whatsNewData,
            onClose = {}
        )
    }
}
