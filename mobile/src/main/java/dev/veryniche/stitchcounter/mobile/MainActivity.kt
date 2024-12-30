package dev.veryniche.stitchcounter.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.AndroidEntryPoint
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.mobile.update.AppUpdateHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    lateinit var appUpdateHelper: AppUpdateHelper

    private val dataClient by lazy { Wearable.getDataClient(this) }
    val viewModel: MainViewModel by viewModels<MainViewModel>()

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        // handle callback
        if (result.data == null) {
            return@registerForActivityResult
        }

        if (result.resultCode != RESULT_OK) {
            Timber.e("Update flow failed! Result code: " + result.resultCode)
            // If the update is canceled or fails,
            // you can request to start the update again.
        } else {
            Timber.d("In app update succeeded")
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            Timber.d("Syncing all projects")
            viewModel.syncAllProjects()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this) { initializationStatus ->
            Timber.d("AdMob init: ${initializationStatus.adapterStatusMap}")
        }
        setContent {
            val dataSyncState by viewModel.eventsToWatch.collectAsStateWithLifecycle()
            LaunchedEffect(dataSyncState) {
                dataSyncState?.let {
                    try {
                        val request = PutDataMapRequest.create(it.path).apply {
                            dataMap.putString(it.key, it.data)
                        }
                            .asPutDataRequest()
                            .setUrgent()
                        val result = dataClient.putDataItem(request).await()
                        Timber.d("DataItem $it synced: $result")
                    } catch (cancellationException: CancellationException) {
                        throw cancellationException
                    } catch (exception: Exception) {
                        Timber.d("Syncing DataItem failed: $exception")
                    }
                }
            }
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            StitchCounterTheme {
                val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                appUpdateHelper = AppUpdateHelper(this, updateLauncher, snackbarHostState, coroutineScope)
                appUpdateHelper.checkForUpdates()
                StitchCounterMobileApp(viewModel, snackbarHostState, windowSizeClass, Modifier)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::appUpdateHelper.isInitialized) {
            appUpdateHelper.checkUpdateStatus()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    @Composable
    fun StitchCounterMobileApp(
        viewModel: MainViewModel,
        snackbarHostState: SnackbarHostState,
        windowSizeClass: WindowSizeClass,
        modifier: Modifier = Modifier,
    ) {
        val navController = rememberNavController()
        MobileNavHost(
            navController = navController,
            viewModel = viewModel,
            purchaseStatus = PurchaseStatus(
                isAdRemovalPurchased = false,
                isSyncPurchased = false,
                isBundlePurchased = false
            ),
            onPurchaseClick = {},
            snackbarHostState = snackbarHostState,
            windowSizeClass = windowSizeClass,
            modifier = modifier.fillMaxSize()
        )
    }
}
