package dev.veryniche.stitchcounter.mobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.veryniche.stitchcounter.core.theme.DarkColors
import dev.veryniche.stitchcounter.core.theme.LightColors
import dev.veryniche.stitchcounter.storage.ThemeMode

// private val DarkColorScheme = darkColorScheme(
//    primary = RubineRed,
//    onPrimary = Color.White,
//    primaryContainer = BlackCoral,
//    onPrimaryContainer = Color.White,
//    tertiary = Pink,
//    secondary = BlackCoral,
// //    secondaryVariant = Charcoal,
//    error = RubineRed,
//    onSecondary = LavenderBlue,
//    onError = Color.White,
//    background = Color.Black,
//    surfaceVariant = LavenderBlue,
//    onSurfaceVariant = Charcoal
// )
//
// // TODO
// private val LightColorScheme = lightColorScheme(
//    primary = RubineRed,
//    onPrimary = Color.White,
//    primaryContainer = RubineRed,
//    onPrimaryContainer = Color.White,
//    tertiary = Pink,
//    secondary = BlackCoral,
// //    secondaryVariant = Charcoal,
//    error = RubineRed,
//    onSecondary = LavenderBlue,
//    onError = Color.Red,
//    background = Color.White,
//    surfaceVariant = LavenderBlue,
//    onSurfaceVariant = Charcoal
// )

@Composable
fun StitchCounterTheme(
    themeMode: ThemeMode,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // !BuildConfig.DEBUG, //true,
    content: @Composable () -> Unit
) {
    StitchCounterTheme(
        darkTheme = when(themeMode) {
            ThemeMode.Auto -> isSystemInDarkTheme()
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
        },
        // Dynamic color is available on Android 12+
        dynamicColor = dynamicColor,
        content = content)
}
@Composable
fun StitchCounterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // !BuildConfig.DEBUG, //true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
