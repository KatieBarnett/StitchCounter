package dev.veryniche.stitchcounter.util

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

private const val ANALYTICS_LOG_TAG = "Analytics"

object Analytics {
    object Screen {
        const val ProjectList = "Project List"
        const val Project = "Project"
        const val EditProject = "Edit Project"
        const val Counter = "Counter"
        const val EditCounter = "Edit Counter"
        const val EditCounterMax = "Edit Counter Max"
        const val About = "About"
        const val WhatsNew = "Whats New"
        const val SelectProjectForTile = "Select Project For Tile"
        const val SelectCounterForTile = "Select Counter For Tile"
    }

    object Action {
        const val AddProject = "Add Project"
        const val ResetCounter = "Reset Counter"
        const val ResetProject = "Reset Project"
        const val DeleteCounter = "Delete Counter"
        const val DeleteProject = "Delete Project"
    }
}

@Composable
fun TrackedScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onStart: () -> Unit, // Send the 'started' analytics event
) {
    if (!LocalInspectionMode.current) {
        // Safely update the current lambdas when a new one is provided
        val currentOnStart by rememberUpdatedState(onStart)

        // If `lifecycleOwner` changes, dispose and reset the effect
        DisposableEffect(lifecycleOwner) {
            // Create an observer that triggers our remembered callbacks
            // for sending analytics events
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    currentOnStart()
                }
            }

            // Add the observer to the lifecycle
            lifecycleOwner.lifecycle.addObserver(observer)

            // When the effect leaves the Composition, remove the observer
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }
}

fun trackScreenView(name: String) {
    Log.d(ANALYTICS_LOG_TAG, "Track screen: $name")
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, name.replace(" ", "_"))
    }
}

fun trackEvent(name: String) {
    Log.d(ANALYTICS_LOG_TAG, "Track action: $name")
    Firebase.analytics.logEvent(name.replace(" ", "_")) {
    }
}

fun trackProjectScreenView(counterCount: Int) {
    Log.d(ANALYTICS_LOG_TAG, "Track screen: ${Analytics.Screen.Project}")
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, Analytics.Screen.Project)
        param(FirebaseAnalytics.Param.ITEMS, counterCount.toDouble())
    }
}
