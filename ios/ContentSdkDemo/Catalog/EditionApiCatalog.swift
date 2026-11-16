import UIKit
import ContentSdk

/*
 =================================================================================================
  Content SDK - COMMERCIAL edition API catalog (iOS)
 =================================================================================================

  Calls only the commercial edition has.

  | API                                          | When to call                           | Section |
  |----------------------------------------------|----------------------------------------|---------|
  | showAdConsentIfRequired(viewController:)     | before start; skip if you run UMP      | Step 3  |
  | setAdEventListener(listener:)                | ad events                              | C1      |
  | setUnlockView(view:)                         | your own unlock UI on locked episodes  | C2      |
  | unlockWithRewardedAd(controller:onComplete:) | from your unlock UI                    | C2      |
  | getRewardedAdProgress(controller:)           | "2/5 watched today" label              | C2      |
 =================================================================================================
 */
enum EditionApiCatalog {

    // MARK: - C1. [Callbacks · Ads] Ad events (one listener per process; setting again replaces; nil stops)

    /// Every event has `ad`: format (rewarded / interstitial), scene, adUnitId.
    static func listenToAdEvents() {
        IosContentSdk.shared.setAdEventListener(listener: AdEventHandler())
    }

    static func stopAdEvents() {
        IosContentSdk.shared.setAdEventListener(listener: nil)
    }

    // MARK: - C2. [Ads] Unlock UI. A locked episode always shows an unlock UI: the SDK one by default, or yours.

    /// Your own unlock view, for SDK player pages and players from `createPlayer()`.
    /// Pass nil to show the SDK unlock layer again: `IosContentSdk.shared.setUnlockView(view: nil)`.
    static func useOwnUnlockView() {
        IosContentSdk.shared.setUnlockView(view: UnlockRegion())
    }
}

/// Ad listener. Kotlin sealed classes become Swift subclasses: check them with `as?`.
private final class AdEventHandler: NSObject, AdEventListener {
    func onAdEvent(event: AdEvent) {
        if event is AdEvent.Impression {
            print("ad shown: \(event.ad.format)")
        } else if let e = event as? AdEvent.RewardEarned {
            print("reward: \(e.rewardAmount) \(e.rewardType)")
        } else if let e = event as? AdEvent.Paid {
            // Value of one impression; not guaranteed for every impression.
            print("paid: \(e.valueMicros) micros \(e.currencyCode) (\(e.precision))")
        } else if event is AdEvent.Dismissed {
            print("ad closed")
        } else if let e = event as? AdEvent.Error {
            print("ad error at \(e.stage): \(e.error.code)")
        }
        // Also: LoadStarted, Loaded, Clicked.
    }
}

/// Unlock view for C2: a dimmed page with one "watch an ad" button.
private final class UnlockRegion: NSObject, DramaPlayerRegionView {
    func createView(controller: ContentPlayerController) -> UIView {
        let container = UIView()
        container.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        let button = UIButton(type: .system, primaryAction: UIAction(title: Self.label(controller)) { _ in
            Self.unlock(controller)
        })
        button.translatesAutoresizingMaskIntoConstraints = false
        container.addSubview(button)
        NSLayoutConstraint.activate([
            button.centerXAnchor.constraint(equalTo: container.centerXAnchor),
            button.centerYAnchor.constraint(equalTo: container.centerYAnchor),
        ])
        return container
    }

    /// "Watch an ad to unlock (2/5 today)"; the counter is nil until the limit is known.
    private static func label(_ controller: ContentPlayerController) -> String {
        guard let progress = IosContentSdk.shared.getRewardedAdProgress(controller: controller) else {
            return "Watch an ad to unlock"
        }
        return "Watch an ad to unlock (\(progress.watchedCount)/\(progress.dailyLimit) today)"
    }

    /// Plays a rewarded ad and unlocks the current episode, with the same rules as the SDK layer.
    private static func unlock(_ controller: ContentPlayerController) {
        IosContentSdk.shared.unlockWithRewardedAd(controller: controller) { result in
            switch result {
            case .unlocked: print("unlocked; playback continues")
            case .notCompleted: print("ad closed before the reward")
            case .dailyLimitReached: print("today's limit for this drama is used up")
            case .noAdAvailable: print("no ad available, try later")
            case .movedToFirstLockedEpisode: print("moved to the first locked episode; tap again")
            case .checking: print("lock still being checked; try again shortly")
            case .inProgress: print("another unlock is running")
            case .notLocked: print("episode is not locked")
            default: print("unlock failed; see SDK logs")
            }
        }
    }
}
