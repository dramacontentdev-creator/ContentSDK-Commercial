import ContentSdk
import UIKit

/// UIKit version of the custom UNLOCK screen (see Unlock/UnlockFlow.swift). It looks and behaves like
/// the SwiftUI version in SwiftUI/SwiftUIUnlockLayer.swift.
///
/// The SDK gives this view the whole page. Only the button takes touches, so a swipe on the rest of
/// the page still moves to another episode.
final class UIKitUnlockLayerView: AttachObservingView {

    private let title = makeLabel(size: 20, weight: .bold)
    private let subtitle = makeLabel("Watch one short ad to unlock it and keep watching.", size: 14, argb: RegionColors.textSecondary)
    private let progress = makeLabel(size: 12, argb: RegionColors.textSecondary)
    private let message = makeLabel(size: 13, argb: 0xFFFBBF24)
    private let button = UIButton(type: .custom)
    private let buttonTitle = makeLabel("Watch ad to unlock", size: 16, weight: .semibold)
    private let spinner = UIActivityIndicatorView(style: .medium)
    private let gradient = CAGradientLayer()

    init(onUnlock: @escaping () -> Void) {
        super.init(frame: .zero)
        backgroundColor = .clear

        let dim = UIView()
        dim.backgroundColor = UIColor.black.withAlphaComponent(0.62)
        dim.isUserInteractionEnabled = false
        dim.translatesAutoresizingMaskIntoConstraints = false
        addSubview(dim)

        let badge = UIImageView(image: UIImage(systemName: "lock.fill", withConfiguration: UIImage.SymbolConfiguration(pointSize: 28)))
        badge.tintColor = .white
        badge.contentMode = .center
        badge.backgroundColor = UIColor(argb: RegionColors.glass)
        badge.layer.cornerRadius = 32
        badge.layer.borderWidth = 1
        badge.layer.borderColor = UIColor.white.withAlphaComponent(0.25).cgColor
        badge.translatesAutoresizingMaskIntoConstraints = false

        [title, subtitle, progress, message].forEach {
            $0.textAlignment = .center
            $0.numberOfLines = 0
        }
        spinner.color = .white
        spinner.hidesWhenStopped = true

        gradient.colors = [UIColor(argb: RegionColors.accent).cgColor, UIColor(argb: RegionColors.accentEnd).cgColor]
        gradient.startPoint = CGPoint(x: 0, y: 0.5)
        gradient.endPoint = CGPoint(x: 1, y: 0.5)
        button.layer.insertSublayer(gradient, at: 0)
        button.layer.masksToBounds = true
        button.addAction(UIAction { _ in onUnlock() }, for: .touchUpInside)
        buttonTitle.isUserInteractionEnabled = false
        buttonTitle.translatesAutoresizingMaskIntoConstraints = false
        spinner.translatesAutoresizingMaskIntoConstraints = false
        button.addSubview(buttonTitle)
        button.addSubview(spinner)
        NSLayoutConstraint.activate([
            buttonTitle.centerXAnchor.constraint(equalTo: button.centerXAnchor),
            buttonTitle.centerYAnchor.constraint(equalTo: button.centerYAnchor),
            spinner.centerXAnchor.constraint(equalTo: button.centerXAnchor),
            spinner.centerYAnchor.constraint(equalTo: button.centerYAnchor),
            button.heightAnchor.constraint(equalToConstant: 52),
        ])

        let tag = makeLabel("CUSTOM UNLOCK", size: 9, weight: .bold, argb: RegionColors.accent)
        let column = UIStackView(arrangedSubviews: [badge, title, subtitle, button, progress, message, tag])
        column.axis = .vertical
        column.alignment = .fill
        column.spacing = 14
        column.setCustomSpacing(14, after: badge)
        badge.widthAnchor.constraint(equalToConstant: 64).isActive = true
        badge.heightAnchor.constraint(equalToConstant: 64).isActive = true
        column.alignment = .center
        button.widthAnchor.constraint(equalTo: column.widthAnchor).isActive = true
        column.translatesAutoresizingMaskIntoConstraints = false
        addSubview(column)

        NSLayoutConstraint.activate([
            dim.leadingAnchor.constraint(equalTo: leadingAnchor),
            dim.trailingAnchor.constraint(equalTo: trailingAnchor),
            dim.topAnchor.constraint(equalTo: topAnchor),
            dim.bottomAnchor.constraint(equalTo: bottomAnchor),
            column.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 40),
            column.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -40),
            column.centerYAnchor.constraint(equalTo: centerYAnchor),
        ])
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) { fatalError("init(coder:) is not used") }

    override func layoutSubviews() {
        super.layoutSubviews()
        gradient.frame = button.bounds
        button.layer.cornerRadius = button.bounds.height / 2
    }

    /// Renders `ui`. Safe to call as often as the SDK reports changes.
    func bind(_ ui: UnlockUi) {
        title.text = ui.episodeNumber > 0 ? "Episode \(ui.episodeNumber) is locked" : "This episode is locked"
        let enabled = !ui.busy && !ui.limitReached
        button.isEnabled = enabled
        button.alpha = enabled ? 1 : 0.45
        buttonTitle.isHidden = ui.busy
        if ui.busy { spinner.startAnimating() } else { spinner.stopAnimating() }
        if let watched = ui.watchedToday, let limit = ui.dailyLimit {
            progress.text = "\(watched) / \(limit) ad unlocks used today"
            progress.isHidden = false
        } else {
            progress.isHidden = true
        }
        message.text = ui.message
        message.isHidden = ui.message == nil
    }
}

/// Creates the unlock view and connects it to the SDK: the controller gives the `UnlockFlow`, and
/// the view renders the flow's `UnlockUi` on every change of the player or of the unlock.
func createUnlockLayerView(controller: ContentPlayerController) -> UIView {
    let feed = PlayerFeed.of(controller)
    let flow = UnlockFlow(controller: controller)
    // The view keeps the flow (through onUnlock); the flow's callback holds both weakly, or they would
    // keep each other alive after the episode is gone.
    let view = UIKitUnlockLayerView(onUnlock: flow.unlock)
    flow.onChange = { [weak view, weak flow] in
        if let view, let flow { view.bind(flow.ui()) }
    }
    observeWhileAttached(view, feed: feed) { view in view.bind(flow.ui()) }
    return view
}
