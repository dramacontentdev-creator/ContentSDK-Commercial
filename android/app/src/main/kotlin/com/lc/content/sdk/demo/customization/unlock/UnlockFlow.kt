package com.lc.content.sdk.demo.customization.unlock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lc.content.sdk.AndroidContentSdk
import com.lc.content.sdk.RewardedUnlockResult
import com.lc.content.sdk.api.player.ContentPlayerController
import com.lc.content.sdk.demo.customization.PlayerFeed
import com.lc.content.sdk.getRewardedAdProgress
import com.lc.content.sdk.unlockWithRewardedAd

/*
 * Custom unlock screen (commercial edition): the SDK-facing part.
 *
 * ## Two ways to unlock, and what the SDK offers
 *
 * 1. **Your UI, the SDK's ad and reward.** You draw the unlock screen; one call,
 *    `unlockWithRewardedAd(controller)`, shows the rewarded ad, has the SDK grant the reward, and
 *    starts playback. This is what the demo does. The SDK keeps the rules: the daily limit, unlocking
 *    in episode order, and one unlock at a time.
 * 2. **Your own ad or payment.** The SDK has no call that takes a result from your side and unlocks
 *    the episode for it. The unlock is granted by the SDK after the SDK's own ad, so an
 *    ad or purchase of your app cannot unlock an SDK episode. Your app can still report its own
 *    revenue with `reportAdRevenue` / `reportPurchaseRevenue` (SdkApiCatalog.kt, section 13).
 *
 * ## What the SDK does around your screen
 *
 * - A locked episode ALWAYS shows an unlock screen: yours when you set one, the SDK's otherwise. It
 *   cannot be hidden, and it does not depend on the video / info / controls regions.
 * - It is drawn over the whole page. Draw only real controls with a click handler, so a swipe on the
 *   rest still moves to another episode.
 * - It is created when the page needs it and removed when the episode unlocks; nothing to dismiss.
 * - Set it once for the process, for example next to the regions (see EditionCustomization.kt).
 */

/** What the unlock screen draws, as plain data. */
internal data class UnlockUi(
    val episodeNumber: Int,
    /** Rewarded ads watched today for this drama, or null until the SDK knows the limit. */
    val watchedToday: Int?,
    val dailyLimit: Int?,
    /** true while the ad is being loaded, shown, or confirmed. */
    val busy: Boolean,
    /** The result of the last try, when it did not unlock; null otherwise. */
    val message: String?,
) {
    /** true when today's limit for this drama is used up. */
    val limitReached: Boolean get() = watchedToday != null && dailyLimit != null && watchedToday >= dailyLimit
}

/**
 * The state and the one action of a custom unlock screen: [unlock] and what came of it.
 * One instance per screen; a View calls [onChange] to draw again, Compose reads the state.
 */
internal class UnlockFlow(private val controller: ContentPlayerController) {

    var busy by mutableStateOf(false)
        private set

    var message by mutableStateOf<String?>(null)
        private set

    /** Called after [busy] or [message] changed; for Views, which do not read snapshot state. */
    var onChange: (() -> Unit)? = null

    /** @return What the screen draws now. Reading it in Compose also follows the player state. */
    fun ui(): UnlockUi {
        val state = PlayerFeed.of(controller).state
        // The counter is read again each time the screen draws, so it follows an unlock that just ended.
        val progress = AndroidContentSdk.getRewardedAdProgress(controller)
        return UnlockUi(
            episodeNumber = state.resource?.episodeNumber ?: 0,
            watchedToday = progress?.watchedCount,
            dailyLimit = progress?.dailyLimit,
            busy = busy,
            message = message,
        )
    }

    /** Plays the rewarded ad and unlocks the episode; the page continues on its own when it worked. */
    fun unlock() {
        if (busy) return
        busy = true
        message = null
        onChange?.invoke()
        AndroidContentSdk.unlockWithRewardedAd(controller) { result ->
            busy = false
            message = result.toMessage()
            onChange?.invoke()
        }
    }
}

/** @return What to tell the user about [this], or null when nothing needs saying (it worked). */
private fun RewardedUnlockResult.toMessage(): String? = when (this) {
    RewardedUnlockResult.Unlocked, RewardedUnlockResult.NotLocked -> null
    RewardedUnlockResult.NotCompleted -> "The ad was closed early. Watch it to the end to unlock."
    RewardedUnlockResult.DailyLimitReached -> "Today's limit for this drama is used up. Come back tomorrow."
    RewardedUnlockResult.NoAdAvailable -> "No ad is available right now. Try again in a moment."
    RewardedUnlockResult.MovedToFirstLockedEpisode -> "Episodes unlock in order. Moved to the first locked one; tap again."
    RewardedUnlockResult.Checking -> "Checking this episode. Try again in a moment."
    RewardedUnlockResult.InProgress -> "Another unlock is running."
    RewardedUnlockResult.Failed -> "Unlock failed. Try again."
}
