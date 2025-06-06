package dev.veryniche.stitchcounter.mobile.review

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory
import dev.veryniche.stitchcounter.core.trackReviewRequested
import dev.veryniche.stitchcounter.storage.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

class ReviewManager(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    companion object {
        const val DAYS_SINCE_LAST_REVIEW_FIRST = 2
        const val DAYS_SINCE_LAST_REVIEW = 60
    }

    @OptIn(FlowPreview::class)
    private val reviewFlow = userPreferencesRepository.userPreferencesFlow.map {
        it.lastReviewDate to it.hasBeenAskedForReview
    }.debounce(300.milliseconds).distinctUntilChanged()

    suspend fun requestReviewIfAble(activity: Activity, coroutineScope: CoroutineScope) {
        reviewFlow.collectLatest { reviewPair ->
            val lastReviewDate = reviewPair.first
            val hasBeenAskedForReview = reviewPair.second
            val currentTimestamp = System.currentTimeMillis()
            val daysSinceLastReview = (currentTimestamp - lastReviewDate) / (1000 * 60 * 60 * 24)
            if (lastReviewDate == -1L) {
                userPreferencesRepository.updateLastReviewDate()
            } else if ((!hasBeenAskedForReview && daysSinceLastReview >= DAYS_SINCE_LAST_REVIEW_FIRST) ||
                daysSinceLastReview >= DAYS_SINCE_LAST_REVIEW
            ) {
                val manager = ReviewManagerFactory.create(activity)
                val request = manager.requestReviewFlow()
                trackReviewRequested()
                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // We got the ReviewInfo object
                        val reviewInfo = task.result
                        val flow = manager.launchReviewFlow(activity, reviewInfo)
                        flow.addOnCompleteListener { _ ->
                            // The flow has finished. The API does not indicate whether the user
                            // reviewed or not, or even whether the review dialog was shown. Thus, no
                            // matter the result, we continue our app flow.
                            coroutineScope.launch {
                                userPreferencesRepository.updateHasBeenAskedForReviewAndUpdateDate(true)
                            }
                        }
                    } else {
                        Timber.e(task.exception, "Error loading review flow.")
                    }
                }
            }
        }
    }
}
