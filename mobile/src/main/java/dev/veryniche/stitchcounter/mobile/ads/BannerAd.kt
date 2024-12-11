package dev.veryniche.stitchcounter.mobile.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.core.trackAdClick
import timber.log.Timber

enum class BannerAdLocation(val adId: String) {
    Test(adId = "ca-app-pub-3940256099942544/6300978111"),
    MainScreen(adId = "ca-app-pub-4584531662076255/2206758646"),
    ProjectScreen(adId = "ca-app-pub-4584531662076255/5850490185"),
    CounterScreen(adId = "ca-app-pub-4584531662076255/4881589373"),
    AboutScreen(adId = "ca-app-pub-4584531662076255/5619497845"),
}

@Composable
fun BannerAd(location: BannerAdLocation, modifier: Modifier = Modifier) {
    val adRequest = AdRequest.Builder().build()
    val context = LocalContext.current
    Box(modifier = modifier.fillMaxWidth().height(Dimen.mobileBannerAdSize)) {
        if (adRequest.isTestDevice(context)) {
            BannerAd(adId = BannerAdLocation.Test.adId, screen = location.name)
        } else {
            // Replace with your actual ad unit ID.
            BannerAd(adId = location.adId, screen = location.name)
        }
    }
}

@Composable
private fun BannerAd(adId: String, screen: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            // on below line specifying ad view.
            AdView(context).apply {
                // on below line specifying ad size
                // adSize = AdSize.BANNER
                // on below line specifying ad unit id
                // currently added a test ad unit id.
                setAdSize(AdSize.BANNER)
                adUnitId = adId
                // calling load ad to load our ad.
                loadAd(AdRequest.Builder().build())
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Timber.d("Banner ad loaded")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        Timber.d("Banner ad clicked")
                        trackAdClick(screen, isMobile = true)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        Timber.d("Banner ad failed to load: ${error.message}")
                    }
                }
            }
        }
    )
}
