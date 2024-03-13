package dev.veryniche.stitchcounter.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme
import androidx.navigation.compose.rememberNavController
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StitchCounterTheme {
                StitchCounterMobileApp()
            }
        }
    }
}

@Composable
fun StitchCounterMobileApp(modifier: Modifier = Modifier) {
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
            modifier = Modifier.fillMaxSize()
        )
    }
}