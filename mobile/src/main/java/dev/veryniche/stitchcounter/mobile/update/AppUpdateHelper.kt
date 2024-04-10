package dev.veryniche.stitchcounter.mobile.update

import android.content.Context
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class AppUpdateHelper(
    private val context: Context,
    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>,
    private val snackbarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope
) {

    companion object {
        const val DAYS_FOR_FLEXIBLE_UPDATE = 2
        const val DAYS_FOR_FLEXIBLE_UPDATE_TO_IMMEDIATE = 14
    }

    val appUpdateManager = AppUpdateManagerFactory.create(context)

    fun checkForUpdates() {
        Timber.d("Checking for available updates")
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnFailureListener {
            // Likely because we're in debug mode
            Timber.d(it, "Failure getting app update status")
        }
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

            Timber.d(
                "App update info: $appUpdateInfo," +
                        " ${appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)}," +
                        " ${appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)}," +
                        " ${appUpdateInfo.availableVersionCode()}," +
                        " ${appUpdateInfo.updateAvailability()}," +
                        " ${appUpdateInfo.updatePriority()}," +
                        " ${appUpdateInfo.clientVersionStalenessDays()}"
            )

            // Check for flexible updates first
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                Timber.d(
                    "Flexible update available and staleness is ${appUpdateInfo.clientVersionStalenessDays()} " +
                            "days (limit $DAYS_FOR_FLEXIBLE_UPDATE)"
                )
                try {
                    appUpdateManager.registerListener(listener)
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )
                } catch (exception: IntentSender.SendIntentException) {
                    Timber.e("Error launching app update for ${AppUpdateType.FLEXIBLE} update")
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE_TO_IMMEDIATE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                Timber.d(
                    "Flexible update available and staleness is ${appUpdateInfo.clientVersionStalenessDays()} " +
                            "days (limit $DAYS_FOR_FLEXIBLE_UPDATE_TO_IMMEDIATE) - change it to an immediate"
                )
                try {
                    appUpdateManager.registerListener(listener)
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                } catch (exception: IntentSender.SendIntentException) {
                    Timber.e("Error launching app update for ${AppUpdateType.IMMEDIATE} update")
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                Timber.d("Immediate update available")
                try {
                    appUpdateManager.registerListener(listener)
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                } catch (exception: IntentSender.SendIntentException) {
                    Timber.e("Error launching app update for ${AppUpdateType.IMMEDIATE} update")
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                Timber.d(
                    "Flexible update available but staleness is ${appUpdateInfo.clientVersionStalenessDays()} " +
                            "days (limit $DAYS_FOR_FLEXIBLE_UPDATE). Do nothing."
                )
            } else {
                Timber.d("No update available")
            }
        }
    }

    fun handleDownloadComplete() {
        Timber.d("Download complete")
        appUpdateManager.unregisterListener(listener)
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "An update has just been downloaded.",
                actionLabel = "Restart",
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    Timber.d("App restarting after update")
                    appUpdateManager.completeUpdate()
                }
                SnackbarResult.Dismissed -> {
                    Timber.d("App restarting snackbar dismissed")
                }
            }
        }
    }

    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            val bytesDownloaded = state.bytesDownloaded()
            val totalBytesToDownload = state.totalBytesToDownload()
            Timber.d("Downloading: $bytesDownloaded/$totalBytesToDownload")
            // Show update progress bar.
        } else if (state.installStatus() == InstallStatus.DOWNLOADED) {
            handleDownloadComplete()
        }
    }

    fun checkUpdateStatus() {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    Timber.d("App update has been downloaded")
                    handleDownloadComplete()
                } else if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    Timber.d("App update already running (immediate), restarting")
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
            }
    }
}
