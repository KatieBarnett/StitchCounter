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
import timber.log.Timber

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
    }

    object Action {
        const val AddProject = "Reset Counter"
        const val ResetCounter = "Reset Counter"
        const val ResetProject = "Reset Project"
        const val DeleteCounter = "Delete Counter"
        const val DeleteProject = "Delete Project"
    }
}
