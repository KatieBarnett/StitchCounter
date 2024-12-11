package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.veryniche.stitchcounter.core.Analytics.Action
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun SaveCounterEnterName(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.validation_error)) },
        text = { Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.validation_message_counter_name)) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) {
                Text(stringResource(R.string.confirm_changes_confirm))
            }
        },
    )
}

@Composable
fun SaveCounterConfirmation(counterName: String, onAccept: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(counterName) },
        text = { Text(stringResource(R.string.confirm_counter_save)) },
        confirmButton = {
            TextButton(onClick = {
                trackEvent(Action.EditCounterConfim, isMobile = true)
                onAccept.invoke()
            }) {
                Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.confirm_counter_save_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) {
                Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.confirm_counter_save_dismiss))
            }
        }
    )
}

@Composable
fun DeleteCounterConfirmation(counterName: String, onAccept: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(counterName) },
        text = { Text(stringResource(R.string.confirm_counter_delete)) },
        confirmButton = {
            TextButton(onClick = {
                trackEvent(Action.DeleteCounterCounterScreen, isMobile = true)
                onAccept.invoke()
            }) {
                Text(stringResource(R.string.confirm_changes_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) {
                Text(stringResource(R.string.confirm_changes_dismiss))
            }
        }
    )
}

@PreviewComponent
@Composable
fun SaveCounterEnterNamePreview() {
    StitchCounterTheme {
        SaveCounterEnterName({})
    }
}

@PreviewComponent
@Composable
fun SaveCounterConfirmationPreview() {
    StitchCounterTheme {
        SaveCounterConfirmation("Counter Name that is really long", {}, {})
    }
}

@PreviewComponent
@Composable
fun DeleteCounterConfirmationPreview() {
    StitchCounterTheme {
        DeleteCounterConfirmation("Counter Name that is really long", {}, {})
    }
}
