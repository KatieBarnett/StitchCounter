package dev.veryniche.stitchcounter.mobile

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
import dev.veryniche.stitchcounter.core.AnalyticsConstants
import timber.log.Timber

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
    Timber.d("Track screen: $name")
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, name ?: "Unknown")
    }
}

fun trackEvent(name: String) {
    Timber.d("Track action: $name")
    Firebase.analytics.logEvent(name) {
    }
}

fun trackAdClick(screen: String) {
    Timber.d("Track ad click: ${AnalyticsConstants.Action.AdClick}")
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, screen)
    }
}

fun trackProjectScreenView(counterCount: Int) {
    Timber.d("Track screen: ${AnalyticsConstants.Screen.Project}")
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, AnalyticsConstants.Screen.Project)
        param(FirebaseAnalytics.Param.ITEMS, counterCount.toDouble())
    }
}
