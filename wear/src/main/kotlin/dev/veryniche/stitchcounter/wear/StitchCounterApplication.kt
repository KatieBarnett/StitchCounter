package dev.veryniche.stitchcounter.wear

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import dev.veryniche.stitchcounter.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class StitchCounterApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            super.log(priority, tag, message, t)

            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            if (priority == Log.ERROR) {
                Firebase.crashlytics.recordException(t ?: RuntimeException(message))
            } else if (priority == Log.WARN) {
                Firebase.crashlytics.log(message)
            } else if (priority == Log.INFO) {
                Firebase.crashlytics.log(message)
            }
        }
    }
}
