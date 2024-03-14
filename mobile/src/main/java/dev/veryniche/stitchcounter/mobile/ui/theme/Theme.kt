package dev.veryniche.stitchcounter.mobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import dev.veryniche.stitchcounter.core.theme.BlackCoral
import dev.veryniche.stitchcounter.core.theme.LavenderBlue
import dev.veryniche.stitchcounter.core.theme.Pink
import dev.veryniche.stitchcounter.core.theme.RubineRed
import dev.veryniche.stitchcounter.mobile.BuildConfig

private val DarkColorScheme = darkColorScheme(
    primary = RubineRed,
    onPrimary = Color.White,
    primaryContainer = BlackCoral,
    onPrimaryContainer = Color.White,
    tertiary = Pink,
    secondary = BlackCoral,
//    secondaryVariant = Charcoal,
    error = RubineRed,
    onSecondary = LavenderBlue,
    onError = Color.White,
    background = Color.Black
)

// TODO
private val LightColorScheme = lightColorScheme(
    primary = RubineRed,
    onPrimary = Color.White,
    primaryContainer = RubineRed,
    onPrimaryContainer = Color.White,
    tertiary = Pink,
    secondary = BlackCoral,
//    secondaryVariant = Charcoal,
    error = RubineRed,
    onSecondary = LavenderBlue,
    onError = Color.Red,
    background = Color.White
)

@Composable
fun StitchCounterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = !BuildConfig.DEBUG, //true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}