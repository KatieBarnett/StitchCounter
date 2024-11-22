package dev.veryniche.stitchcounter.wear.presentation

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.compose.material.rememberPickerState
import dev.veryniche.stitchcounter.R.string
import dev.veryniche.stitchcounter.core.Analytics
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.trackScreenView
import dev.veryniche.stitchcounter.wear.presentation.theme.Dimen
import dev.veryniche.stitchcounter.wear.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.wear.previews.PreviewScreen



import kotlinx.coroutines.launch

@Composable
fun EditCounterMaxDialog(
    showDialog: Boolean,
    initialValue: Int = 0,
    onDismissRequest: () -> Unit,
    onDone: (maxCount: Int) -> Unit
) {
    TrackedScreen {
        trackScreenView(name = Analytics.Screen.EditCounterMax, isMobile = false)
    }
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(Dimen.spacing)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            val items = (0..1000).toList()
            val initialIndex = items.indexOf(initialValue).run {
                if (this == -1) { 0 } else { this }
            }
            val selectedValue = rememberPickerState(items.size, initialIndex)
            val contentDescription by remember { derivedStateOf { "${selectedValue.selectedOption}" } }
            Picker(
                modifier = Modifier.weight(1f)
                    .onRotaryScrollEvent {
                        coroutineScope.launch {
                            selectedValue.scrollBy(it.verticalScrollPixels)
                            selectedValue.animateScrollBy(0f)
                        }
                        true
                    },
                state = selectedValue,
                contentDescription = contentDescription,
            ) {
                Text(items[it].toString(), style = MaterialTheme.typography.display1)
            }
            Button(
                onClick = { onDone.invoke(items[selectedValue.selectedOption]) },
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Icon(
                    imageVector = Filled.Done,
                    contentDescription = stringResource(id = string.edit_counter_max)
                )
            }
        }
    }
}

@PreviewScreen
@Composable
fun EditCounterMaxDialogPreview() {
    StitchCounterTheme {
        EditCounterMaxDialog(true, 67, {}, {})
    }
}
