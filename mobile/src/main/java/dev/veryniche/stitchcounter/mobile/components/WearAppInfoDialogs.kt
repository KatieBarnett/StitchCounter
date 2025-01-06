package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.veryniche.stitchcounter.core.Analytics.Action.InstallWearApp
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.mobile.R
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun WearAppInfoDialog(
    onInstallWearAppClick: (Boolean) -> Unit,
    onDismiss: (Boolean) -> Unit
) {
    var doNotShowAgain by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = { onDismiss.invoke(doNotShowAgain) },
        title = { Text(stringResource(R.string.wear_app_info_title)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimen.spacingQuad)
            ) {
                InfoText(dev.veryniche.stitchcounter.core.R.string.about_content_mobile_get_wear)
                Button(content = {
                    Text(
                        text = stringResource(
                            dev.veryniche.stitchcounter.core.R.string.about_content_mobile_get_wear_cta
                        )
                    )
                }, onClick = {
                    trackEvent(InstallWearApp, isMobile = true)
                    onInstallWearAppClick.invoke(doNotShowAgain)
                })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.wear_app_info_do_not_show_again),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = doNotShowAgain,
                        onCheckedChange = { newValue ->
                            doNotShowAgain = newValue
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss.invoke(doNotShowAgain)
            }) {
                Text(stringResource(R.string.wear_app_info_button_cancel))
            }
        }
    )
}

@PreviewComponent
@Composable
fun WearAppInfoDialogPreview() {
    StitchCounterTheme {
        WearAppInfoDialog(
            onDismiss = {},
            onInstallWearAppClick = { },
        )
    }
}
