package com.lc.content.sdk.demo.customization.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.lc.content.sdk.api.player.ContentPlayerController
import com.lc.content.sdk.demo.R
import com.lc.content.sdk.demo.customization.PlayerFeed
import com.lc.content.sdk.demo.customization.RegionColors
import com.lc.content.sdk.demo.customization.unlock.UnlockFlow
import com.lc.content.sdk.demo.customization.unlock.UnlockUi

/**
 * Android View version of the custom UNLOCK screen (see customization/unlock/UnlockFlow.kt). It looks
 * and behaves like the Compose version in compose/UnlockLayer.kt.
 *
 * The SDK gives this view the whole page. Only the button has a click listener, so a swipe on the
 * rest of the page still moves to another episode.
 */
internal class UnlockLayerView(context: Context, private val onUnlock: () -> Unit) : FrameLayout(context) {

    private val title = label(context, "", sp = 20f, bold = true)
    private val subtitle = label(context, "Watch one short ad to unlock it and keep watching.", sp = 14f, color = RegionColors.TEXT_SECONDARY)
    private val buttonText = label(context, "Watch ad to unlock", sp = 16f, bold = true).apply { setShadowLayer(0f, 0f, 0f, 0) }
    private val spinner = ProgressBar(context).apply { isIndeterminate = true }
    private val button = FrameLayout(context)
    private val progress = label(context, "", sp = 12f, color = RegionColors.TEXT_SECONDARY)
    private val message = label(context, "", sp = 13f, color = WARNING)

    init {
        setBackgroundColor(DIM)

        val badge = iconButton(context, R.drawable.ic_lock, 64, 30, RegionColors.GLASS, "Locked")
        listOf(title, subtitle, progress, message).forEach { it.gravity = Gravity.CENTER }

        button.apply {
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(RegionColors.ACCENT, RegionColors.ACCENT_END),
            ).apply { cornerRadius = 1000f }
            addView(buttonText, LayoutParams(WRAP, WRAP, Gravity.CENTER))
            addView(spinner, LayoutParams(context.dp(22), context.dp(22), Gravity.CENTER))
            setOnClickListener { onUnlock() }
        }

        val tag = label(context, "CUSTOM UNLOCK", sp = 9f, bold = true, color = RegionColors.ACCENT).apply { letterSpacing = 0.08f }
        val column = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(context.dp(40), 0, context.dp(40), 0)
            addView(badge, LinearLayout.LayoutParams(context.dp(64), context.dp(64)))
            addSpaced(title, 14)
            addSpaced(subtitle, 14)
            addView(button, LinearLayout.LayoutParams(MATCH, context.dp(52)).apply { topMargin = context.dp(14) })
            addSpaced(progress, 14)
            addSpaced(message, 14)
            addSpaced(tag, 14)
        }
        addView(column, LayoutParams(MATCH, WRAP, Gravity.CENTER))
    }

    /** Renders [ui]. Safe to call as often as the SDK reports changes. */
    fun bind(ui: UnlockUi) {
        title.text = if (ui.episodeNumber > 0) "Episode ${ui.episodeNumber} is locked" else "This episode is locked"
        val enabled = !ui.busy && !ui.limitReached
        button.isEnabled = enabled
        button.alpha = if (enabled) 1f else 0.45f
        buttonText.visibility = if (ui.busy) GONE else VISIBLE
        spinner.visibility = if (ui.busy) VISIBLE else GONE
        val used = if (ui.watchedToday != null && ui.dailyLimit != null) "${ui.watchedToday} / ${ui.dailyLimit} ad unlocks used today" else ""
        progress.text = used
        progress.visibility = if (used.isEmpty()) GONE else VISIBLE
        message.text = ui.message.orEmpty()
        message.visibility = if (ui.message == null) GONE else VISIBLE
    }

    private fun LinearLayout.addSpaced(view: View, topDp: Int) {
        addView(view, LinearLayout.LayoutParams(WRAP, WRAP).apply { topMargin = context.dp(topDp) })
    }

    private companion object {
        const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
        const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT
        const val DIM = 0x9E000000.toInt()
        const val WARNING = 0xFFFBBF24.toInt()
    }
}

/**
 * Creates the unlock view and connects it to the SDK: the controller gives the [UnlockFlow], and the
 * view renders the flow's [UnlockUi] on every change of the player or of the unlock.
 */
internal fun createUnlockLayerView(context: Context, controller: ContentPlayerController): View {
    val feed = PlayerFeed.of(controller)
    val flow = UnlockFlow(controller)
    val view = UnlockLayerView(context) { flow.unlock() }
    val render = { view.bind(flow.ui()) }
    flow.onChange = render
    observeWhileAttached(view, feed, render)
    return view
}
