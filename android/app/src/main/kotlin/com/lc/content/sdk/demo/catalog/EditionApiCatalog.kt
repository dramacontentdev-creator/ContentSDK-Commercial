package com.lc.content.sdk.demo.catalog

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lc.content.sdk.AndroidContentSdk
import com.lc.content.sdk.DramaPlayerRegionView
import com.lc.content.sdk.RewardedUnlockResult
import com.lc.content.sdk.api.SdkException
import com.lc.content.sdk.api.event.AdEvent
import com.lc.content.sdk.api.player.ContentPlayerController
import com.lc.content.sdk.getRewardedAdProgress
import com.lc.content.sdk.setAdEventListener
import com.lc.content.sdk.setUnlockContent
import com.lc.content.sdk.setUnlockView
import com.lc.content.sdk.showAdConsentIfRequired
import com.lc.content.sdk.unlockWithRewardedAd

/*
 * =================================================================================================
 *  Content SDK - COMMERCIAL edition API catalog (Android)
 * =================================================================================================
 *
 *  This is the Ads block of SdkApiCatalog.kt: calls only the commercial edition has (C2 is a
 *  callback). They are extension functions of `AndroidContentSdk`, so Kotlin needs an import per
 *  call, e.g. `import com.lc.content.sdk.setUnlockView`.
 *  Java: `AndroidContentSdkAds.setUnlockView(AndroidContentSdk.INSTANCE, view)`.
 *
 *  | API                                        | When to call                            | Section |
 *  |--------------------------------------------|-----------------------------------------|---------|
 *  | showAdConsentIfRequired(activity)          | before start; skip if you run UMP       | Step 4  |
 *  | start / consent (suspend versions)         | coroutine version of Step 4             | C1      |
 *  | setAdEventListener(listener)               | ad events                               | C2      |
 *  | setUnlockView / setUnlockContent           | your own unlock UI on locked episodes   | C3      |
 *  | unlockWithRewardedAd(controller)           | from your unlock UI                     | C3      |
 *  | getRewardedAdProgress(controller)          | "2/5 watched today" label               | C3      |
 * =================================================================================================
 */
@Suppress("unused")
object EditionApiCatalog {

    private const val TAG = "ContentSdkDemo"

    // ---------------------------------------------------------------------------------------------
    // C1. [Setup] Step 4 with coroutines
    // ---------------------------------------------------------------------------------------------

    /**
     * Same as `startContentSdk` (DemoEdition.kt in the demo root), with suspend functions.
     * e.g. `lifecycleScope.launch { EditionApiCatalog.startWithCoroutines(this@MainActivity) }`.
     *
     * @return true when SDK pages can be opened.
     */
    suspend fun startWithCoroutines(activity: Activity): Boolean {
        val consent = AndroidContentSdk.showAdConsentIfRequired(activity)
        if (!consent.canRequestAds) {
            Log.w(TAG, "ads not allowed: ${consent.errorMessage}")
            return false
        }
        return try {
            AndroidContentSdk.start()
            true
        } catch (error: SdkException) {
            Log.w(TAG, "start failed: ${error.code} ${error.msg}")
            false
        }
    }

    // ---------------------------------------------------------------------------------------------
    // C2. [Callbacks · Ads] Ad events (one listener per process; setting again replaces; null stops)
    // ---------------------------------------------------------------------------------------------

    /** Every event has `ad`: format (Rewarded / Interstitial), scene, adUnitId. */
    fun listenToAdEvents() {
        AndroidContentSdk.setAdEventListener { event ->
            when (event) {
                is AdEvent.Impression -> Log.d(TAG, "ad shown: ${event.ad.format}")
                is AdEvent.RewardEarned -> Log.d(TAG, "reward: ${event.rewardAmount} ${event.rewardType}")
                // Value of one impression; not guaranteed for every impression.
                is AdEvent.Paid -> Log.d(TAG, "paid: ${event.valueMicros} micros ${event.currencyCode} (${event.precision})")
                is AdEvent.Dismissed -> Log.d(TAG, "ad closed")
                is AdEvent.Error -> Log.w(TAG, "ad error at ${event.stage}: ${event.error.code}")
                // Also: LoadStarted, Loaded, Clicked.
                else -> Unit
            }
        }
    }

    fun stopAdEvents() {
        AndroidContentSdk.setAdEventListener(null)
    }

    // ---------------------------------------------------------------------------------------------
    // C3. [Ads] Unlock UI. A locked episode always shows an unlock UI: the SDK one by default, or yours.
    // ---------------------------------------------------------------------------------------------

    /**
     * Your own unlock view, for SDK player pages and players from `createPlayer`.
     * A runnable, styled version (Compose and View) is in customization/unlock/: pick a style on the
     * demo home screen. The SDK offers one way to unlock, `unlockWithRewardedAd` below; there is no
     * call that unlocks an episode for an ad or purchase of your own.
     * Pass null to show the SDK unlock layer again: `AndroidContentSdk.setUnlockView(null)`.
     */
    fun useOwnUnlockView() {
        AndroidContentSdk.setUnlockView(
            DramaPlayerRegionView { context, controller ->
                FrameLayout(context).apply {
                    setBackgroundColor(Color.argb(160, 0, 0, 0))
                    addView(
                        Button(context).apply {
                            text = unlockLabel(controller)
                            setOnClickListener { unlock(controller) }
                        },
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER,
                        ),
                    )
                }
            },
        )
    }

    /** Compose version of [useOwnUnlockView]. */
    fun useOwnUnlockContent() {
        AndroidContentSdk.setUnlockContent { controller ->
            Box(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color(0xA0000000)), Alignment.Center) {
                androidx.compose.material3.Button(onClick = { unlock(controller) }) {
                    Text(unlockLabel(controller))
                }
            }
        }
    }

    /** "Watch an ad to unlock (2/5 today)"; the counter is null until the limit is known. */
    private fun unlockLabel(controller: ContentPlayerController): String {
        val progress = AndroidContentSdk.getRewardedAdProgress(controller)
        return if (progress == null) "Watch an ad to unlock"
        else "Watch an ad to unlock (${progress.watchedCount}/${progress.dailyLimit} today)"
    }

    /** Plays a rewarded ad and unlocks the current episode, with the same rules as the SDK layer. */
    private fun unlock(controller: ContentPlayerController) {
        AndroidContentSdk.unlockWithRewardedAd(controller) { result ->
            when (result) {
                RewardedUnlockResult.Unlocked -> Log.d(TAG, "unlocked; playback continues")
                RewardedUnlockResult.NotCompleted -> Log.d(TAG, "ad closed before the reward")
                RewardedUnlockResult.DailyLimitReached -> Log.d(TAG, "today's limit for this drama is used up")
                RewardedUnlockResult.NoAdAvailable -> Log.d(TAG, "no ad available, try later")
                RewardedUnlockResult.MovedToFirstLockedEpisode -> Log.d(TAG, "moved to the first locked episode; tap again")
                RewardedUnlockResult.Checking -> Log.d(TAG, "lock still being checked; try again shortly")
                RewardedUnlockResult.InProgress -> Log.d(TAG, "another unlock is running")
                RewardedUnlockResult.NotLocked -> Log.d(TAG, "episode is not locked")
                RewardedUnlockResult.Failed -> Log.w(TAG, "unlock failed; see SDK logs")
            }
        }
    }
}
