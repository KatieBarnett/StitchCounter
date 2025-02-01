package dev.veryniche.stitchcounter.wear.presentation

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.ResponsiveDialogContent
import dev.veryniche.stitchcounter.R.string
import dev.veryniche.stitchcounter.wear.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.wear.previews.PreviewScreen

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun PurchaseDialog(
    message: Int,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    ResponsiveDialogContent(
        title = {
            Text(
                text = stringResource(message),
                textAlign = TextAlign.Center,
            )
        },
        onOk = onConfirm,
        onCancel = onCancel,
        okButtonContentDescription = stringResource(string.purchase_limit_button_purchase),
        cancelButtonContentDescription = stringResource(string.purchase_limit_button_cancel),
        showPositionIndicator = true,
        modifier = Modifier.background(MaterialTheme.colors.background),
    )
}

@Composable
@PreviewScreen
fun PurchaseDialogPreview() {
    StitchCounterTheme {
        PurchaseDialog(
            message = string.purchase_limit_projects,
            onCancel = {},
            onConfirm = {},
        )
    }
}
