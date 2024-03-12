package dev.veryniche.stitchcounter.wear.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.compose.material.rememberPickerState
import dev.veryniche.mobile.core.theme.Dimen
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.previews.PreviewScreen
import dev.veryniche.stitchcounter.wear.util.Analytics
import dev.veryniche.stitchcounter.wear.util.TrackedScreen
import dev.veryniche.stitchcounter.wear.util.trackScreenView

@Composable
fun EditCounterMaxDialog(showDialog: Boolean, initialValue: Int = 0, onDismissRequest: () -> Unit, onDone: (maxCount: Int) -> Unit) {

    TrackedScreen {
        trackScreenView(name = Analytics.Screen.EditCounterMax)
    }

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
                modifier = Modifier.weight(1f),
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
                    contentDescription = stringResource(id = R.string.edit_counter_max)
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

