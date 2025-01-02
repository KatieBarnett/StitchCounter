package dev.veryniche.stitchcounter.mobile.review

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory
import dev.veryniche.stitchcounter.core.trackReviewRequested
import dev.veryniche.stitchcounter.storage.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class ReviewManager(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    companion object {
        const val DAYS_SINCE_LAST_REVIEW = 30
    }

    private val lastReviewDateFlow = userPreferencesRepository.userPreferencesFlow.map { it.lastReviewDate }

    suspend fun requestReviewIfAble(activity: Activity, coroutineScope: CoroutineScope) {
        lastReviewDateFlow.collectLatest { lastReviewDate ->
            val currentTimestamp = System.currentTimeMillis()
            val daysSinceLastReview = (currentTimestamp - lastReviewDate) / (1000 * 60 * 60 * 24)
            if (lastReviewDate == -1L || daysSinceLastReview >= DAYS_SINCE_LAST_REVIEW) {
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
                                userPreferencesRepository.updateLastReviewDate()
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
