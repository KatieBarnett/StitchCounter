package dev.veryniche.stitchcounter.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import dev.veryniche.stitchcounter.core.theme.BlackCoral
import dev.veryniche.stitchcounter.core.theme.Charcoal
import dev.veryniche.stitchcounter.core.theme.LavenderBlue
import dev.veryniche.stitchcounter.core.theme.Pink
import dev.veryniche.stitchcounter.core.theme.RubineRed

private val stitchCounterColorPalette: Colors = Colors(
    primary = RubineRed,
    primaryVariant = Pink,
    secondary = BlackCoral,
    secondaryVariant = Charcoal,
    error = RubineRed,
    onPrimary = Color.White,
    onSecondary = LavenderBlue,
    onError = Color.White,
    background = Color.Black
)

@Composable
fun StitchCounterTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = stitchCounterColorPalette,
        // For shapes, we generally recommend using the default Material Wear shapes which are
        // optimized for round and non-round devices.
        content = content
    )
}
