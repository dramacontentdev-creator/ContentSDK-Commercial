import ContentSdk
import UIKit

// =================================================================================================
//  COMMERCIAL edition. This file and its pure twin are the only files of Customization/ that differ
//  between the editions, together with the Unlock folder, SwiftUI/SwiftUIUnlockLayer.swift and
//  UIKit/UIKitUnlockLayer.swift that only exist here.
// =================================================================================================

/// What the commercial edition adds to the player page styles: the unlock screen of a locked episode.
///
/// A locked episode always shows an unlock screen, so the style changes it as well: the SDK's own, or
/// yours drawn with SwiftUI or with UIKit. Yours calls `unlockWithRewardedAd`; see
/// Unlock/UnlockFlow.swift for what that does and for the other way to unlock, which the SDK does not offer.
enum EditionCustomization {

    /// Installs the unlock screen of `style`. Pages that are open apply it at once.
    static func apply(_ style: PlayerPageStyle) {
        switch style {
        // Passing nil brings the SDK unlock layer back.
        case .sdkDefault:
            IosContentSdk.shared.setUnlockView(view: nil)

        case .swiftUI:
            IosContentSdk.shared.setUnlockView(view: RegionViewFactory { SwiftUIHost.view(SwiftUIUnlockLayer(controller: $0)) })

        case .uiKit:
            IosContentSdk.shared.setUnlockView(view: RegionViewFactory { createUnlockLayerView(controller: $0) })
        }
    }
}
