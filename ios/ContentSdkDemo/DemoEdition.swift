import UIKit
import ContentSdk

// =================================================================================================
//  COMMERCIAL edition. Swift files that differ from the pure demo: this file,
//  Catalog/EditionApiCatalog.swift, Customization/EditionCustomization.swift, and the commercial-only
//  unlock UI (Customization/Unlock/, SwiftUI/SwiftUIUnlockLayer.swift, UIKit/UIKitUnlockLayer.swift).
//  Every other Swift file is identical in the commercial and pure demos.
// =================================================================================================

/// What the demo UI shows about this edition. Demo only.
enum DemoEdition {
    static let name = "Commercial"
    static let summary = "SDK ads included: rewarded unlock and between-episode interstitials."
    static let startingDetail = "Checking ad consent and booting the SDK."

    /// The SDK's `setAutoPlay` default in this edition: an episode starts as soon as it is ready.
    static let autoPlayDefault = true

    /// The integration checklist on the demo home screen; labels match the `Step` comments in code.
    static let integrationSteps: [IntegrationStep] = [
        IntegrationStep(label: "1", title: "Install the SDK Pod", file: "Podfile"),
        IntegrationStep(label: "A", title: "Set your AdMob ID and SKAdNetworkItems", file: "Info.plist"),
        IntegrationStep(label: "2", title: "Initialize the SDK", file: "ContentSdkDemoApp.swift"),
        IntegrationStep(label: "3", title: "Ad consent + start", file: "DemoEdition.swift"),
        IntegrationStep(label: "4", title: "Open the SDK page", file: "SdkIntegration.swift"),
    ]
}

/// Your SDK credentials for the COMMERCIAL edition.
///
/// The values below are the DEMO credentials. The commercial and the pure demo, on Android and on
/// iOS, all use this one set, and it is for the demos only. Replace it with YOUR App ID / App
/// Secret, which you get at https://open.lucky-chick.com.
/// Do not hard-code your production secret in a public repository.
enum SdkConfig {
    /// YOUR Content App ID.
    static let appId = "1d724208-378c-489a-8462-f9038fa1ffd8"

    /// YOUR Content App Secret.
    static let appSecret = "427908ce2f21dc75bab6202bd7a3390f29e27bfe0b3e686b4570af7e4ef972fd"

    /// YOUR logged-in user's id, so watch history and unlocks follow the user across devices.
    /// Pass "" if your app has no login; the SDK then creates an anonymous user.
    static let userId = ""
}

/// Content SDK - integration Step 3 (ad consent + start)
///
/// 1. `showAdConsentIfRequired` shows Google's privacy (UMP / GDPR) dialog when the user's region
///    needs it, otherwise it returns at once. It needs a view controller to present on.
///    Skip it if your app already runs UMP.
/// 2. `start` boots the SDK (network, player, ads). Call it once; do NOT show SDK pages before it
///    succeeds.
///
/// Both callbacks run on the main thread.
@MainActor
func startContentSdk(onResult: @escaping (DemoSdkStatus) -> Void) {
    guard let presenter = UIApplication.shared.topViewController else {
        onResult(.failed(code: "NoViewController", message: "No view controller to show the consent dialog on"))
        return
    }
    IosContentSdk.shared.showAdConsentIfRequired(viewController: presenter) { consent in
        guard consent.canRequestAds else {
            // The user did not allow ad requests. Decide what your app does here.
            onResult(.failed(code: "Consent", message: consent.errorMessage ?? "Ads not allowed"))
            return
        }
        IosContentSdk.shared.start { error in
            if let error {
                // Show a failure message or retry entry.
                print("Content SDK start failed: \(error.code) \(error.msg)")
                onResult(.failed(code: error.code, message: error.msg))
                return
            }
            onResult(.ready)
        }
    }
}
