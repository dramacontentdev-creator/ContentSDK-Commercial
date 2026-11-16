import Combine
import ContentSdk
import Foundation

/*
 Custom unlock screen (commercial edition): the SDK-facing part.

 ## Two ways to unlock, and what the SDK offers

 1. **Your UI, the SDK's ad and reward.** You draw the unlock screen; one call,
    `unlockWithRewardedAd(controller:)`, shows the rewarded ad, has the SDK grant the reward, and
    starts playback. This is what the demo does. The SDK keeps the rules: the daily limit, unlocking
    in episode order, and one unlock at a time.
 2. **Your own ad or payment.** The SDK has no call that takes a result from your side and unlocks
    the episode for it. The unlock is granted by the SDK after the SDK's own ad, so an
    ad or purchase of your app cannot unlock an SDK episode. Your app can still report its own
    revenue with `reportAdRevenue` / `reportPurchaseRevenue` (SdkApiCatalog.swift, section 13).

 ## What the SDK does around your screen

 - A locked episode ALWAYS shows an unlock screen: yours when you set one, the SDK's otherwise. It
   cannot be hidden, and it does not depend on the video / info / controls regions.
 - It is drawn over the whole page. Draw only real controls with a touch handler, so a swipe on the
   rest still moves to another episode.
 - It is created when the page needs it and removed when the episode unlocks; nothing to dismiss.
 - Set it once for the process, for example next to the regions (see EditionCustomization.swift).
 */

/// What the unlock screen draws, as plain data.
struct UnlockUi {
    let episodeNumber: Int
    /// Rewarded ads watched today for this drama, or nil until the SDK knows the limit.
    let watchedToday: Int?
    let dailyLimit: Int?
    /// true while the ad is being loaded, shown, or confirmed.
    let busy: Bool
    /// The result of the last try, when it did not unlock; nil otherwise.
    let message: String?

    /// true when today's limit for this drama is used up.
    var limitReached: Bool {
        guard let watchedToday, let dailyLimit else { return false }
        return watchedToday >= dailyLimit
    }
}

/// The state and the one action of a custom unlock screen: `unlock()` and what came of it.
/// One instance per screen; a UIKit view sets `onChange` to draw again, SwiftUI observes it.
final class UnlockFlow: ObservableObject {
    private let controller: ContentPlayerController

    @Published private(set) var busy = false
    @Published private(set) var message: String?

    /// Called after `busy` or `message` changed; for UIKit views, which do not observe.
    var onChange: (() -> Void)?

    init(controller: ContentPlayerController) {
        self.controller = controller
    }

    /// What the screen draws now. Also read the player state in SwiftUI, so the screen follows it.
    func ui() -> UnlockUi {
        let state = PlayerFeed.of(controller).state
        // The counter is read again each time the screen draws, so it follows an unlock that just ended.
        let progress = IosContentSdk.shared.getRewardedAdProgress(controller: controller)
        return UnlockUi(
            episodeNumber: Int(state.resource?.episodeNumber ?? 0),
            watchedToday: progress.map { Int($0.watchedCount) },
            dailyLimit: progress.map { Int($0.dailyLimit) },
            busy: busy,
            message: message
        )
    }

    /// Plays the rewarded ad and unlocks the episode; the page continues on its own when it worked.
    func unlock() {
        guard !busy else { return }
        busy = true
        message = nil
        onChange?()
        IosContentSdk.shared.unlockWithRewardedAd(controller: controller) { [weak self] result in
            guard let self else { return }
            busy = false
            message = Self.message(for: result)
            onChange?()
        }
    }

    /// What to tell the user about `result`, or nil when nothing needs saying (it worked).
    private static func message(for result: RewardedUnlockResult) -> String? {
        switch result {
        case .unlocked, .notLocked: return nil
        case .notCompleted: return "The ad was closed early. Watch it to the end to unlock."
        case .dailyLimitReached: return "Today's limit for this drama is used up. Come back tomorrow."
        case .noAdAvailable: return "No ad is available right now. Try again in a moment."
        case .movedToFirstLockedEpisode: return "Episodes unlock in order. Moved to the first locked one; tap again."
        case .checking: return "Checking this episode. Try again in a moment."
        case .inProgress: return "Another unlock is running."
        default: return "Unlock failed. Try again."
        }
    }
}
