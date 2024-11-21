package dev.veryniche.stitchcounter.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.mobile.update.AppUpdateHelper
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    lateinit var appUpdateHelper: AppUpdateHelper

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

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this) { initializationStatus ->
            Timber.d("AdMob init: ${initializationStatus.adapterStatusMap}")
        }
        setContent {
            StitchCounterTheme {
                val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                appUpdateHelper = AppUpdateHelper(this, updateLauncher, snackbarHostState, coroutineScope)
                appUpdateHelper.checkForUpdates()
                StitchCounterMobileApp(snackbarHostState)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::appUpdateHelper.isInitialized) {
            appUpdateHelper.checkUpdateStatus()
        }
    }
}

@Composable
fun StitchCounterMobileApp(snackbarHostState: SnackbarHostState, modifier: Modifier = Modifier) {
    StitchCounterTheme {
        val viewModel: MainViewModel = hiltViewModel()
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
            modifier = modifier.fillMaxSize()
        )
    }
}
