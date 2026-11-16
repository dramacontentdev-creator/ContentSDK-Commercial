package com.lc.content.sdk.demo.customization.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lc.content.sdk.api.player.ContentPlayerController
import com.lc.content.sdk.demo.R
import com.lc.content.sdk.demo.customization.RegionColors
import com.lc.content.sdk.demo.customization.unlock.UnlockFlow
import com.lc.content.sdk.demo.customization.unlock.UnlockUi

/**
 * Compose version of the custom UNLOCK screen (see customization/unlock/UnlockFlow.kt).
 *
 * Connected to the SDK in two lines: the controller gives the [UnlockFlow], the flow gives the
 * [UnlockUi]; the button calls `flow.unlock()`, which is `unlockWithRewardedAd`.
 */
@Composable
internal fun ComposeUnlockLayer(controller: ContentPlayerController) {
    val flow = remember(controller) { UnlockFlow(controller) }
    UnlockLayerContent(ui = flow.ui(), onUnlock = flow::unlock)
}

/** The unlock screen as pure UI: no SDK types, so it can be drawn from fixed data. */
@Composable
internal fun UnlockLayerContent(ui: UnlockUi, onUnlock: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.62f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            LockBadge()
            Text(
                text = if (ui.episodeNumber > 0) "Episode ${ui.episodeNumber} is locked" else "This episode is locked",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Watch one short ad to unlock it and keep watching.",
                color = Color(RegionColors.TEXT_SECONDARY),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
            UnlockButton(ui, onUnlock)
            AdProgress(ui)
            ui.message?.let {
                Text(it, color = Color(0xFFFBBF24), fontSize = 13.sp, textAlign = TextAlign.Center)
            }
            Text(
                text = "CUSTOM UNLOCK",
                color = Color(RegionColors.ACCENT),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
            )
        }
    }
}

@Composable
private fun LockBadge() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color(RegionColors.GLASS))
            .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_lock),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier.size(30.dp),
        )
    }
}

/** The only touch target: an accent capsule. While the ad runs it shows a spinner and ignores taps. */
@Composable
private fun UnlockButton(ui: UnlockUi, onUnlock: () -> Unit) {
    val enabled = !ui.busy && !ui.limitReached
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(
                Brush.horizontalGradient(listOf(Color(RegionColors.ACCENT), Color(RegionColors.ACCENT_END))),
                alpha = if (enabled) 1f else 0.45f,
            )
            .clickable(enabled = enabled, onClick = onUnlock),
        contentAlignment = Alignment.Center,
    ) {
        if (ui.busy) {
            CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Text("Watch ad to unlock", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** `2 / 5 ad unlocks used today`; nothing until the SDK knows the limit. */
@Composable
private fun AdProgress(ui: UnlockUi) {
    val watched = ui.watchedToday ?: return
    val limit = ui.dailyLimit ?: return
    Text("$watched / $limit ad unlocks used today", color = Color(RegionColors.TEXT_SECONDARY), fontSize = 12.sp)
}
