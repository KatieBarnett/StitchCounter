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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.core.Analytics
import dev.veryniche.stitchcounter.core.Analytics.Action.InstallPhoneApp
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.core.trackScreenView
import dev.veryniche.stitchcounter.wear.presentation.ListTitle
import dev.veryniche.stitchcounter.wear.presentation.theme.Dimen
import dev.veryniche.stitchcounter.wear.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.wear.previews.PreviewScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun PhoneAppInfoScreen(
    listState: ScalingLazyListState,
    onClose: (Boolean) -> Unit,
    onInstall: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    TrackedScreen {
        trackScreenView(name = Analytics.Screen.PhoneAppInfo, isMobile = false)
    }
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = rememberActiveFocusRequester()
    var doNotShowAgain by remember { mutableStateOf(false) }

    ScalingLazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimen.spacingDouble),
        state = listState,
        anchorType = ScalingLazyListAnchorType.ItemStart,
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
                    stringResource(R.string.phone_app_info_title),
                    modifier = Modifier.padding(top = Dimen.spacingQuad)
                )
            }
        }
        item {
            Text(text = stringResource(R.string.phone_app_info_message))
        }
        item {
            Chip(
                onClick = {
                    trackEvent(InstallPhoneApp, isMobile = false)
                    onInstall.invoke(doNotShowAgain)
                },
                colors = ChipDefaults.primaryChipColors(),
                modifier = modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                label = {
                    Text(
                        text = stringResource(R.string.phone_app_info_install),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        }
        item {
            Chip(
                onClick = {
                    onClose.invoke(doNotShowAgain)
                },
                colors = ChipDefaults.primaryChipColors(),
                modifier = modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                label = {
                    Text(
                        text = stringResource(R.string.phone_app_info_close),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        }
        item {
            ToggleChip(
                colors = ToggleChipDefaults.toggleChipColors(
                    uncheckedToggleControlColor = ToggleChipDefaults.SwitchUncheckedIconColor
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                label = {
                    Text(
                        text = stringResource(R.string.phone_app_info_do_not_show_again),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                checked = doNotShowAgain,
                onCheckedChange = { newValue ->
                    doNotShowAgain = newValue
                },
                toggleControl = {
                    Switch(
                        checked = doNotShowAgain,
                        enabled = true,
                    )
                },
            )
        }
    }
}

@PreviewScreen
@Composable
fun PhoneAppInfoDialogPreview() {
    StitchCounterTheme {
        PhoneAppInfoScreen(
            listState = rememberScalingLazyListState(),
            onClose = {},
            onInstall = {},
        )
    }
}
