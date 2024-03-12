package dev.veryniche.stitchcounter.presentation

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.OpenInBrowser
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Confirmation
import dev.veryniche.stitchcounter.BuildConfig
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.previews.PreviewScreen
import dev.veryniche.stitchcounter.util.Analytics
import dev.veryniche.stitchcounter.util.TrackedScreen
import dev.veryniche.stitchcounter.util.openUrlOnPhone
import dev.veryniche.stitchcounter.util.trackScreenView
import kotlinx.coroutines.launch

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun AboutScreen(
    listState: ScalingLazyListState,
    modifier: Modifier = Modifier
) {
    TrackedScreen {
        trackScreenView(name = Analytics.Screen.About)
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showLinkOpenedConfirmation by remember { mutableStateOf(false) }
    var showEmailOpenedConfirmation by remember { mutableStateOf(false) }
    val focusRequester = rememberActiveFocusRequester()

    ScalingLazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimen.spacingDouble),
        state = listState,
        modifier = modifier
            .fillMaxSize()
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
                    ListTitle(stringResource(R.string.about_title),
                        modifier = Modifier.padding(top = Dimen.spacingQuad))
                }
            }
            listOf(R.string.about_content, R.string.about_feedback).forEach {
                item {
                    Text(
                        text = stringResource(id = it)
                    )
                }
            }
            item {
                Text(
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primaryVariant,
                    text = stringResource(id = R.string.about_email)
                )
            }
        // TODO: Fix & make this work if possible
//        val email = stringResource(id = R.string.about_email)
//        val emailSubject = stringResource(id = R.string.about_email_subject)
//        Chip(colors = ChipDefaults.secondaryChipColors(),
//            modifier = Modifier.fillMaxWidth(),
//            label = {},
//            secondaryLabel = {
//                Text(email)
//            },
//            onClick = {
//                coroutineScope.launch {
//                    emailOnPhone(context, email, emailSubject)
//                    showEmailOpenedConfirmation = true
//                }
//            }
//        )

        item {
            Text(
                text = stringResource(id = R.string.about_privacy_policy)
            )
        }

        item {
            val privacyPolicyLink = stringResource(id = R.string.about_privacy_policy_link)
            Chip(colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.about_privacy_policy_link_text))
                },
                onClick = {
                    coroutineScope.launch {
                        openUrlOnPhone(context, privacyPolicyLink)
                        showLinkOpenedConfirmation = true
                    }
                }
            )
        }
        item {
            Text(
                text = stringResource(id = R.string.about_developer)
            )
        }
        item {
            val devLink = stringResource(id = R.string.about_developer_link)
            Chip(colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.about_developer_link_text))
                },
                secondaryLabel = {
                    Text(devLink)
                },
                onClick = {
                    coroutineScope.launch {
                        openUrlOnPhone(context, devLink)
                        showLinkOpenedConfirmation = true
                    }
                }
            )
        }
        item {
            val companyLink = stringResource(id = R.string.about_developer_company_link)
            Chip(colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.about_developer_company_link_text))
                },
                secondaryLabel = {
                    Text(companyLink)
                },
                onClick = {
                    coroutineScope.launch {
                        openUrlOnPhone(context, companyLink)
                        showLinkOpenedConfirmation = true
                    }
                }
            )
        }
        item {
            Text(
                text = stringResource(id = R.string.about_version, BuildConfig.VERSION_NAME),
                color = MaterialTheme.colors.primaryVariant,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth()
            )
        }
    }
    if (showLinkOpenedConfirmation) {
        LinkOpenedConfirmation {
            showLinkOpenedConfirmation = false
        }
    }
    if (showEmailOpenedConfirmation) {
        EmailOpenedConfirmation {
            showEmailOpenedConfirmation = false
        }
    }
}

@Composable
fun LinkOpenedConfirmation(onTimeout: () -> Unit) {
    Confirmation(
        onTimeout = onTimeout,
        icon = {
            Icon(
                imageVector = Icons.Filled.OpenInBrowser,
                contentDescription = stringResource(R.string.about_link_opened_confirmation),
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(Dimen.confirmationIconSize)
            )
        },
        content = {
            Text(stringResource(R.string.about_link_opened_confirmation),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    )
}

@Composable
fun EmailOpenedConfirmation(onTimeout: () -> Unit) {
    Confirmation(
        onTimeout = onTimeout,
        icon = {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = stringResource(R.string.about_email_opened_confirmation),
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(Dimen.confirmationIconSize)
            )
        },
        content = {
            Text(stringResource(R.string.about_email_opened_confirmation),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.75f))
        }
    )
}

@PreviewScreen
@Composable
fun AboutScreenPreview() {
    StitchCounterTheme {
        AboutScreen(listState = rememberScalingLazyListState())
    }
}