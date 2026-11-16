import ContentSdk
import SwiftUI

/// SwiftUI version of the custom UNLOCK screen (see Unlock/UnlockFlow.swift).
///
/// Connected to the SDK in two lines: the controller gives the `UnlockFlow`, the flow gives the
/// `UnlockUi`; the button calls `flow.unlock()`, which is `unlockWithRewardedAd`.
struct SwiftUIUnlockLayer: View {
    @ObservedObject private var flow: UnlockFlow
    @ObservedObject private var feed: PlayerFeed

    init(controller: ContentPlayerController) {
        flow = UnlockFlow(controller: controller)
        feed = PlayerFeed.of(controller)
    }

    var body: some View {
        UnlockLayerContent(ui: flow.ui(), onUnlock: flow.unlock)
    }
}

/// The unlock screen as pure UI: no SDK types, so it can be drawn from fixed data.
struct UnlockLayerContent: View {
    let ui: UnlockUi
    let onUnlock: () -> Void

    var body: some View {
        ZStack {
            // The dim layer takes no touches, so a swipe on the page still changes the episode.
            Color.black.opacity(0.62).ignoresSafeArea().allowsHitTesting(false)
            VStack(spacing: 14) {
                Image(systemName: "lock.fill")
                    .font(.system(size: 28))
                    .foregroundStyle(.white)
                    .frame(width: 64, height: 64)
                    .background(Color(argb: RegionColors.glass), in: Circle())
                    .overlay(Circle().stroke(.white.opacity(0.25), lineWidth: 1))
                Text(ui.episodeNumber > 0 ? "Episode \(ui.episodeNumber) is locked" : "This episode is locked")
                    .font(.system(size: 20, weight: .bold))
                    .foregroundStyle(.white)
                Text("Watch one short ad to unlock it and keep watching.")
                    .font(.system(size: 14))
                    .multilineTextAlignment(.center)
                    .foregroundStyle(Color(argb: RegionColors.textSecondary))
                unlockButton
                if let watched = ui.watchedToday, let limit = ui.dailyLimit {
                    Text("\(watched) / \(limit) ad unlocks used today")
                        .font(.system(size: 12))
                        .foregroundStyle(Color(argb: RegionColors.textSecondary))
                }
                if let message = ui.message {
                    Text(message)
                        .font(.system(size: 13))
                        .multilineTextAlignment(.center)
                        .foregroundStyle(DemoColors.warning)
                }
                Text("CUSTOM UNLOCK")
                    .font(.system(size: 9, weight: .bold))
                    .kerning(0.8)
                    .foregroundStyle(Color(argb: RegionColors.accent))
            }
            .padding(.horizontal, 40)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    /// The only touch target: an accent capsule. While the ad runs it shows a spinner and ignores taps.
    private var unlockButton: some View {
        let enabled = !ui.busy && !ui.limitReached
        return Button(action: onUnlock) {
            ZStack {
                if ui.busy {
                    ProgressView().tint(.white)
                } else {
                    Text("Watch ad to unlock").font(.system(size: 16, weight: .semibold))
                }
            }
            .foregroundStyle(.white)
            .frame(maxWidth: .infinity)
            .frame(height: 52)
            .background(DemoColors.accentGradient.opacity(enabled ? 1 : 0.45), in: Capsule())
        }
        .disabled(!enabled)
    }
}
