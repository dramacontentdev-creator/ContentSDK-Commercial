# Content SDK Demo

English | [中文](README.zh-CN.md)

Shows how to integrate Content SDK on Android and iOS: drama home, player, playback control, player page customization, rewarded-ad unlock, and ad events. Integration steps are marked in the code so you can copy them into your app.

This repository does not include the SDK. Get the SDK package from your SDK provider and put the files where the quick start says, then run.

## Repository layout

```text
android/                         Android project (open this folder in Android Studio)
  settings.gradle.kts            Repositories
  app/
    build.gradle.kts             AAR and dependency script
    libs/                        SDK AAR goes here (not committed)
    content-sdk-dependencies.gradle   Dependency script from the SDK package (not committed)
    src/main/kotlin/com/lc/content/sdk/demo/
      DemoApplication.kt         Initialize
      DemoEdition.kt             App ID / App Secret, start
      MainActivity.kt            Open SDK pages
      catalog/                   API reference with examples to copy
      customization/             Player page customization (Compose and View)
      lab/                       Player lab
      ui/                        Demo UI only, not needed in your app
ios/                             iOS project
  Podfile                        SDK Pod
  ContentSdk/                    iOS SDK package goes here (not committed)
  ContentSdkDemo.xcworkspace     Created by pod install; open the project with it
  ContentSdkDemo/
    ContentSdkDemoApp.swift      Initialize
    DemoEdition.swift            App ID / App Secret, start
    SdkIntegration.swift         Open SDK pages
    Catalog/                     API reference with examples to copy
    Customization/               Player page customization (SwiftUI and UIKit)
    Lab/                         Player lab
    UI/                          Demo UI only, not needed in your app
```

## Quick start

### Android

1. From `ContentSdk-Android-YOUR_SDK_VERSION-release`, copy `ContentSdk-YOUR_SDK_VERSION.aar` into `android/app/libs/`.
2. Copy `content-sdk-dependencies.gradle` from the same package into `android/app/`.
3. Open `android/` in Android Studio and run `app`.

### iOS

1. Copy everything in `ContentSdk-iOS-YOUR_SDK_VERSION-release` into `ios/ContentSdk/`.
2. Run `pod install` in `ios/`.
3. Open `ios/ContentSdkDemo.xcworkspace` and run.

To run on a device, create `ios/Signing.local.xcconfig` with `DEVELOPMENT_TEAM = YOUR_TEAM_ID` (see `ios/Signing.xcconfig`). The file is git-ignored; setting the team in Xcode's Signing tab would write it into the project file instead.

> The AAR, the dependency script, and the iOS SDK package must come from the same SDK version.

## Integrate into your app

Each step is marked `Content SDK - integration Step N` in the code. Copy them in order. Android and iOS are numbered separately:

| Step | Android | iOS |
|---|---|---|
| 1 | `settings.gradle.kts`: repositories, including the ad SDK repositories | `Podfile`: `pod 'ContentSdk'` |
| 2 | `app/build.gradle.kts`: AAR and dependency script | `ContentSdkDemoApp.swift`: initialize |
| 3 | `DemoApplication.kt`: initialize | `DemoEdition.swift`: ad consent + start |
| 4 | `DemoEdition.kt`: ad consent + start | `SdkIntegration.swift`: open SDK pages |
| 5 | `MainActivity.kt`: open SDK pages | |
| A | `AndroidManifest.xml`: AdMob Application ID | `Info.plist`: AdMob Application ID and SKAdNetworkItems |

Do step A together with the dependency steps: without the AdMob Application ID the app stops at launch.

Then fill in your own values:

- App ID / App Secret: get them at [https://open.lucky-chick.com](https://open.lucky-chick.com) and put them in `SdkConfig` in `DemoEdition.kt` / `DemoEdition.swift`.
- AdMob Application ID: `AndroidManifest.xml` / `Info.plist`. The demo uses Google's test ID.
- iOS: merge `SKAdNetworkItems.plist` from the SDK package into your `Info.plist`.

## Features

From the demo home screen you can open:

- **SDK pages**: the drama home and the player, with the full UI from the SDK.
- **Player page styles**: pick SDK default, Compose / SwiftUI, or View / UIKit on the home screen, then open the player to see your own UI replace the info region, controls region, and episode list (`customization/`). With a custom style, the unlock screen of a locked episode is also the demo's own UI (`customization/unlock/`); the SDK's `unlockWithRewardedAd` still runs the ad and the reward.
- **Player lab**: the SDK player page fills the screen and the demo draws its own control panel over it: play / pause, seek, speed, quality, episodes, playback options, region visibility, and a log of every call and playback event (`lab/` / `Lab/`; the SDK calls are in `LabActions.kt` / `LabActions.swift`).
- **Ad unlock**: on a locked episode the SDK shows its rewarded-ad unlock layer and continues playback after the ad.

## API reference

The § numbers are sections of `SdkApiCatalog` (`catalog/SdkApiCatalog.kt` on Android, `Catalog/SdkApiCatalog.swift` on iOS). Both platforms use the same numbers.

| API | Where | Use |
|---|---|---|
| `initialize` / `start` | Integration steps | Initialize and start the SDK. Show SDK pages only after `start` succeeds |
| `setLoggingEnabled` | §1 | SDK diagnostic logs |
| `setStrategy` | §2 | Resource strategy (cache and preload level) |
| `setColorPalette` / `resetColorPalette`, `setSubtitleStyle` / `resetSubtitleStyle` | §3 | Theme colors and subtitle style |
| Android: `openDramaHomePage`, `openDramaPlayerPage`, `createDramaHomeFragment`, `createDramaPlayerFragment`, `DramaHomeScreen`, `DramaPlayerScreen`; iOS: `dramaHome`, `dramaPlayer` | §4 | Show the drama home and player with Activity / Fragment / Compose or UIKit / SwiftUI |
| `login` / `setLanguage` | §5 | User or app language changes |
| `setPlayerEventListener` | §6 | Playback events: first frame, state, progress, errors |
| `getDramaList` | §7 | Fetch the drama list and show it in your own UI |
| `setPlayerControllerListener`; controller `play`, `pause`, `seekTo`, `setPlaybackSpeed`, `setQuality`, `selectEpisode`, `setListener` | §8.1 - 8.2, lab | Control the SDK player page from your own UI |
| `setContentListener` / `setProgressListener` | §8.3, `customization/PlayerFeed` | Drama info, episode list, and progress for your own controls |
| `setControlsHeld` | §8.4, lab | Keep the page controls visible while your own dialog covers the page |
| `setAutoPlay` / `setAutoAdvance` / `setResumeFromHistory` | §9, lab settings | Auto play, auto advance, resume from history |
| `getPlaybackHistory` / `removePlaybackHistory` / `clearPlaybackHistory` | §10 | Continue watching |
| `setVideoRegion` / `setInfoRegion` / `setControlsRegion`, `DramaPlayerRegion` | §11, `customization/` | Replace the video, info, or controls region of the player page |
| `DramaPlayerRegion(autoHide)`, `setRegionVisible`, `state.controlsVisible` | §11, lab settings | Decide yourself when a region shows or hides |
| `createPlayer`; Android `ContentPlayerView` / `createPlayerView`, iOS `playerViewController` | §12 | Put a player in your own UI instead of the SDK player page |
| `reportAdRevenue` / `reportPurchaseRevenue` | §13 | Report revenue from your app's own ads and purchases |
| `showAdConsentIfRequired` | Android step 4 / iOS step 3; Android coroutine version: `EditionApiCatalog` C1 | Google UMP ad consent, before `start` |
| `setAdEventListener` | `EditionApiCatalog` C2 (Android) / C1 (iOS) | Ad load, impression, click, reward, paid, and dismiss events |
| `setUnlockView`, `setUnlockContent` (Android) | `EditionApiCatalog` C3 (Android) / C2 (iOS), styles in `customization/` | Replace the unlock layer with your own UI |
| `unlockWithRewardedAd` / `getRewardedAdProgress` | `EditionApiCatalog` C3 / C2, `customization/unlock/UnlockFlow` | Run the SDK's rewarded ad from your unlock UI and read today's count |

## Using the APIs

1. **Get the required steps running first**: copy steps 1 to the last one from "Integrate into your app", then open the drama home after `start` succeeds. That alone covers the basic features.
2. **Look up `SdkApiCatalog` as needed**: each section says when to call the API, what the parameters mean, and has an example to copy. Section numbers are in the table above. Every other API is optional; without a call the SDK uses its defaults.
3. **Ads and unlock are in `EditionApiCatalog`**: ad consent, ad events, and your own unlock UI. On Android these APIs are extension functions; `AndroidContentSdk.setUnlockView(...)` needs `import com.lc.content.sdk.setUnlockView`.
4. **To change the player page UI**: start with `customization/CustomRegions.kt` (iOS `CustomRegions.swift`), which holds the SDK calls and explanations. Then copy `compose/` or `view/` (iOS `SwiftUI/` or `UIKit/`) for your UI toolkit. `PlayerFeed` turns the player state into what your UI draws.
5. **To check playback control**: tap through the matching buttons in the lab, read the calls and events in its log, then find the SDK call in `LabActions`.

Rules for every call:

- Call every API on the main thread; callbacks also run on the main thread.
- Call `initialize` once per process. Use the other APIs after `start` succeeds.
- Each kind of listener keeps one instance. Setting it again replaces the old one; pass `null` / `nil` to stop.

## Environment

| Android | |
|---|---|
| Android Gradle Plugin / Gradle | 9.3.0 / 9.5.0 |
| Kotlin | 2.3.10 |
| compileSdk / targetSdk / minSdk | 37 / 37 / 24 |
| Gradle JDK | 25 (downloaded by Gradle if missing) |

| iOS | |
|---|---|
| Xcode | 26.6 |
| CocoaPods | 1.17.0 |
| Deployment target | iOS 16.0, iPhone |
| Run on | Device, or an Apple-silicon Mac simulator |

## Logs

Debug builds turn on SDK diagnostic logging with `setLoggingEnabled`, so you can follow initialization and page loading. Keep it off in release builds.

- Android: filter Logcat by `ContentSdk`.
- iOS: filter the Xcode console by `ContentSdk`.

## Notes

- The App ID / App Secret in this demo are for the demo only. Use your own values in your app.
- The Android icons in `app/src/main/res/drawable/ic_*.xml` are Material icons (Apache License 2.0). iOS uses SF Symbols, so there are no icon assets to copy.
