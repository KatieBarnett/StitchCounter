package dev.veryniche.stitchcounter.core

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
import dev.veryniche.stitchcounter.core.Analytics.Action
import dev.veryniche.stitchcounter.core.Analytics.Action.ReviewRequested
import timber.log.Timber

private const val ANALYTICS_LOG_TAG = "Analytics"

object Analytics {
    object Screen {
        const val About = "About"
        const val Settings = "Settings"
        const val Counter = "Counter"
        const val EditCounter = "Edit Counter"
        const val EditCounterMax = "Edit Counter Max"
        const val EditProject = "Edit Project"
        const val Project = "Project"
        const val ProjectList = "Project List"
        const val SelectCounterForTile = "Select Counter For Tile"
        const val SelectProjectForTile = "Select Project For Tile"
        const val WhatsNew = "Whats New"
    }

    object Action {
        const val AboutEmail = "About email"
        const val AboutRemoveAdsVersion = "About remove ads version"
        const val AboutSyncVersion = "About sync version"
        const val AdClick = "AdClick"
        const val AddCounter = "Add Counter"
        const val AddProject = "Add Project"
        const val DeleteCounter = "Delete Counter"
        const val DeleteCounterCounterScreen = "Delete Counter Counter Screen"
        const val DeleteCounterProjectScreen = "Delete Counter Project Screen"
        const val DeleteProject = "Delete Project"
        const val DeleteProjectConfirm = "Delete Project Confirm"
        const val EditProject = "Project Confirm"
        const val EditProjectConfim = "Edit Project Confirm Save"
        const val EditProjectSave = "Edit Project Save"
        const val EditCounterSave = "Edit Counter Save"
        const val EditCounterConfim = "Edit Counter Confirm Save"
        const val ResetCounter = "Reset Counter"
        const val ResetProject = "Reset Project"
        const val ReviewRequested = "Review Requested"
        const val ManageSubscription = "Manage subscription"
        const val PurchasePro = "Purchase Pro Bundle"
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

fun trackScreenView(name: String, isMobile: Boolean) {
    val name = if (isMobile) {
        "Mobile $name"
    } else {
        "Wear $name"
    }
    Timber.d("Track screen: $name")
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, name.replace(" ", "_"))
    }
}

fun trackEvent(name: String, isMobile: Boolean) {
    val name = if (isMobile) {
        "Mobile $name"
    } else {
        "Wear $name"
    }
    Timber.d("Track action: $name")
    Firebase.analytics.logEvent(name.replace(" ", "_")) {
    }
}

fun trackAdClick(screen: String, isMobile: Boolean) {
    val screen = if (isMobile) {
        "Mobile $screen"
    } else {
        "Wear $screen"
    }
    Timber.d("Track ad click: ${Action.AdClick}")
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, screen.replace(" ", "_"))
    }
}

fun trackProjectScreenView(counterCount: Int, isMobile: Boolean) {
    val screen = if (isMobile) {
        "Mobile ${Analytics.Screen.Project}"
    } else {
        "Wear ${Analytics.Screen.Project}"
    }
    Timber.d("Track screen: ${Analytics.Screen.Project}")
    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, screen.replace(" ", "_"))
        param(FirebaseAnalytics.Param.ITEMS, counterCount.toDouble())
    }
}

fun trackReviewRequested() {
    Timber.d("Track action: Review requested")
    Firebase.analytics.logEvent(ReviewRequested.replace(" ", "_")) {}
}
