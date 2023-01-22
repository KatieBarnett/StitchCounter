package dev.veryniche.stitchcounter.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Confirmation
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.firebase.analytics.FirebaseAnalytics
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.presentation.theme.Dimen
import dev.veryniche.stitchcounter.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.util.Analytics
import dev.veryniche.stitchcounter.util.TrackedScreen
import dev.veryniche.stitchcounter.util.emailOnPhone
import dev.veryniche.stitchcounter.util.openUrlOnPhone
import dev.veryniche.stitchcounter.util.trackScreenView
import kotlinx.coroutines.launch

@Composable
fun AboutScreen(listState: ScalingLazyListState,
    enableAnalytics: Boolean = true) {
    if (enableAnalytics) {
        TrackedScreen {
            trackScreenView(name = Analytics.Screen.About)
        }
    }
    val context = LocalContext.current
    val coroutineContext = rememberCoroutineScope()
    var showLinkOpenedConfirmation by remember { mutableStateOf(false) }
    var showEmailOpenedConfirmation by remember { mutableStateOf(false) }
    ScalingLazyColumn(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimen.spacingDouble),
        state = listState) {
            item {
                ListHeader() {
                    Text(
                        color = MaterialTheme.colors.primaryVariant,
                        text = stringResource(id = R.string.about_title)
                    )
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
//                coroutineContext.launch {
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
                    coroutineContext.launch {
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
                    coroutineContext.launch {
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
                    coroutineContext.launch {
                        openUrlOnPhone(context, companyLink)
                        showLinkOpenedConfirmation = true
                    }
                }
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

@Preview
@Composable
fun AboutScreenPreview() {
    StitchCounterTheme {
        AboutScreen(enableAnalytics = false, listState = rememberScalingLazyListState())
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun AboutScreenRoundPreview() {
    StitchCounterTheme {
        AboutScreen(enableAnalytics = false, listState = rememberScalingLazyListState())
    }
}
