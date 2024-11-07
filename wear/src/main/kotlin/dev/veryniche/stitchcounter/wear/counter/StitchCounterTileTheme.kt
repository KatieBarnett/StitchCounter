package dev.veryniche.stitchcounter.wear.counter

import androidx.compose.ui.graphics.toArgb
import androidx.wear.compose.material.Colors
import dev.veryniche.stitchcounter.presentation.theme.stitchCounterColorPalette

object StitchCounterTileTheme {
    val colors = stitchCounterColorPalette.toTileColors()
}

private fun Colors.toTileColors() = androidx.wear.protolayout.material.Colors(
    /* primary = */
    primary.toArgb(),
    /* onPrimary = */
    onPrimary.toArgb(),
    /* surface = */
    surface.toArgb(),
    /* onSurface = */
    onSurface.toArgb()
)
