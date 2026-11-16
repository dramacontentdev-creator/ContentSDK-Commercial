package com.lc.content.sdk.demo.customization

import com.lc.content.sdk.AndroidContentSdk
import com.lc.content.sdk.DramaPlayerRegionView
import com.lc.content.sdk.demo.customization.compose.ComposeUnlockLayer
import com.lc.content.sdk.demo.customization.view.createUnlockLayerView
import com.lc.content.sdk.setUnlockContent
import com.lc.content.sdk.setUnlockView

// =================================================================================================
//  COMMERCIAL edition. This file and its pure twin are the only files of customization/ that differ
//  between the editions, together with the unlock/ folder, the compose/UnlockLayer.kt and the
//  view/UnlockLayer.kt that only exist here.
// =================================================================================================

/**
 * What the commercial edition adds to the player page styles: the unlock screen of a locked episode.
 *
 * A locked episode always shows an unlock screen, so the style changes it as well: the SDK's own, or
 * yours drawn with Compose or with Views. Yours calls `unlockWithRewardedAd`; see
 * unlock/UnlockFlow.kt for what that does and for the other way to unlock, which the SDK does not offer.
 */
internal object EditionCustomization {

    /** Installs the unlock screen of [style]. Pages that are open apply it at once. */
    fun apply(style: PlayerPageStyle) {
        when (style) {
            // Passing null brings the SDK unlock layer back.
            PlayerPageStyle.SdkDefault -> AndroidContentSdk.setUnlockView(null)

            PlayerPageStyle.Compose ->
                AndroidContentSdk.setUnlockContent { controller -> ComposeUnlockLayer(controller) }

            PlayerPageStyle.View ->
                AndroidContentSdk.setUnlockView(
                    DramaPlayerRegionView { context, controller -> createUnlockLayerView(context, controller) },
                )
        }
    }
}
