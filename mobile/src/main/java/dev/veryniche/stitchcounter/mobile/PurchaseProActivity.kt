package dev.veryniche.stitchcounter.mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.MobileAds
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.data.WearDataLayerRegistry
import com.google.android.horologist.datalayer.phone.PhoneDataLayerAppHelper
import dagger.hilt.android.AndroidEntryPoint
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseAction
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseManager
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.screens.PurchaseProScreen
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.storage.ThemeMode
import timber.log.Timber

@AndroidEntryPoint
class PurchaseProActivity : ComponentActivity() {

    @OptIn(ExperimentalHorologistApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this) { initializationStatus ->
            Timber.d("AdMob init: ${initializationStatus.adapterStatusMap}")
        }
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val purchaseManager = remember { PurchaseManager(this, coroutineScope) }
            val wearDataLayerRegistry = WearDataLayerRegistry.fromContext(
                application = application,
                coroutineScope = coroutineScope,
            )
            val appHelper = PhoneDataLayerAppHelper(this, wearDataLayerRegistry)
            val viewModel =
                hiltViewModel<MainViewModel, MainViewModel.MainViewModelFactory> { factory ->
                    factory.create(purchaseManager, appHelper)
                }
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle(ThemeMode.Auto)
            val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
            val availableSubscriptions by viewModel.availableSubscriptions.collectAsStateWithLifecycle(listOf())
            var showPurchaseErrorMessage by rememberSaveable { mutableStateOf<Int?>(null) }
            val purchaseStatus by viewModel.purchaseStatus.collectAsStateWithLifecycle(PurchaseStatus())
            StitchCounterTheme(themeMode) {
                PurchaseProScreen(
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier,
                    onNavigateBack = {
                        startActivity(Intent(this@PurchaseProActivity, MainActivity::class.java))
                    },
                    purchaseStatus = purchaseStatus,
                    onPurchaseClick = { action ->
                        if (action is PurchaseAction.Subscribe) {
                            viewModel.purchaseSubscription(
                                productId = action.productId,
                                offerToken = action.offerToken,
                                onError = { message ->
                                    showPurchaseErrorMessage = message
                                }
                            )
                        }
                    },
                    availableSubscriptions = availableSubscriptions,
                )
            }

            showPurchaseErrorMessage?.let { message ->
                AlertDialog(
                    onDismissRequest = { showPurchaseErrorMessage = null },
                    title = { Text(stringResource(R.string.app_name)) },
                    text = { Text(stringResource(message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            showPurchaseErrorMessage = null
                        }) {
                            Text(stringResource(R.string.purchase_error_dismiss))
                        }
                    }
                )
            }
        }
    }
}
