package dev.veryniche.stitchcounter.wear.presentation

import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Output
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import dev.veryniche.stitchcounter.R.string
import dev.veryniche.stitchcounter.wear.previews.PreviewScreen

@Composable
fun PurchaseDialog(
    message: Int,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    scrollState: ScalingLazyListState,
) {
    Alert(
        scrollState = scrollState,
        title = {
            Text(
                text = stringResource(message),
                textAlign = TextAlign.Center,
            )
        },
        negativeButton = {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Filled.Close,
                    contentDescription = stringResource(string.purchase_limit_button_cancel)
                )
            }
        },
        positiveButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Icon(
                    imageVector = Filled.Output,
                    contentDescription = stringResource(string.purchase_limit_button_purchase)
                )
            }
        },
        modifier = Modifier
    )
}

@Composable
@PreviewScreen
fun PurchaseDialogPreview() {
    PurchaseDialog(
        message = string.purchase_limit_projects,
        onCancel = {},
        onConfirm = {},
        scrollState = rememberScalingLazyListState()
    )
}
