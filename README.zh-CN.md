# Content SDK Demo：商业版

[English](README.md) | 中文 · [纯净版 demo](https://github.com/dramacontentdev-creator/ContentSDK-Pure)

本仓库不包含 SDK。按版本和平台向 SDK 提供方获取 SDK 包，再按下面「快速开始」放到指定位置。

SDK 展示激励视频解锁和剧集间插屏。这里使用商业版 SDK 包。

## 快速开始：Android

1. 从 `ContentSdk-Android-YOUR_SDK_VERSION-release` 中把 `ContentSdk-YOUR_SDK_VERSION.aar` 复制到 `android/app/libs/`。
2. 把同一个包里的 `content-sdk-dependencies.gradle` 复制到 `android/app/`。
3. 用 Android Studio 打开 `android/`，运行 `app`。

## 快速开始：iOS

1. 把 `ContentSdk-iOS-YOUR_SDK_VERSION-release` 里的全部内容复制到 `ios/ContentSdk/`。
2. 在 `ios/` 目录执行 `pod install`。
3. 打开 `ios/ContentSdkDemo.xcworkspace` 运行。

在真机上运行时，新建 `ios/Signing.local.xcconfig`，写入 `DEVELOPMENT_TEAM = YOUR_TEAM_ID`（参见 `ios/Signing.xcconfig`）。这个文件不会提交到 git；如果在 Xcode 的 Signing 页里选 team，它会被写进工程文件。

## 接入你自己的应用

代码中每个步骤都标有 `Content SDK - integration Step N`，按顺序抄过去。Android 和 iOS 的步骤分别编号，请按列从上往下看（iOS 一列没有依赖这一步）：

| 步骤 | Android | iOS |
|---|---|---|
| 1 | `settings.gradle.kts`：依赖仓库，包括广告依赖仓库 | `Podfile`：`pod 'ContentSdk'` |
| 2 | `app/build.gradle.kts`：AAR 和依赖 | `ContentSdkDemoApp.swift`：初始化 |
| 3 | `DemoApplication.kt`：初始化 | `DemoEdition.swift`：广告同意 + 启动 |
| 4 | `DemoEdition.kt`：广告同意 + 启动 | `SdkIntegration.swift`：打开 SDK 页面 |
| 5 | `MainActivity.kt`：打开 SDK 页面 | |
| A（仅商业版） | `AndroidManifest.xml`：AdMob Application ID | `Info.plist`：AdMob Application ID 和 SKAdNetworkItems |

A 步要和依赖步骤一起做：没有 AdMob Application ID，应用启动时就会停止运行。

然后换成自己的值：

- App ID / App Secret：在 [https://open.lucky-chick.com](https://open.lucky-chick.com) 获取，填到 `DemoEdition.kt` / `DemoEdition.swift` 的 `SdkConfig` 里。demo 里的值只能用于 demo。
- AdMob Application ID（`AndroidManifest.xml` / `Info.plist`）。demo 用的是 Google 测试 ID。
- iOS：把 SDK 包里的 `SKAdNetworkItems.plist` 合并到自己的 `Info.plist`。

其他调用都是可选的，用 SDK 默认值即可：

- `customization/`：用自己的界面替换 SDK 播放页的一部分。Android 先看 `CustomRegions.kt`，里面是 SDK 调用和详细说明；`compose/` 和 `view/` 是同一套简介区、控制区和选集列表的 Compose 与 Android View 两种写法；`PlayerFeed.kt` 把控制器状态转成它们要画的数据。iOS 对应 `CustomRegions.swift`、`SwiftUI/`、`UIKit/`、`PlayerFeed.swift`；iOS 上页面导航由应用自己管理，所以自定义简介区通过 `PlayerPageCloser` 关闭播放页。在演示首页选择样式（SDK 默认、Compose / SwiftUI、View / UIKit），再打开播放页，按你的界面技术栈选一个复制即可。商业版里样式还会替换锁定剧集的解锁页（`customization/unlock/`）：界面是你的，广告和奖励由 SDK 的 `unlockWithRewardedAd` 完成。
- `lab/`（Android）/ `Lab/`（iOS）：播放器实验室。SDK 播放页铺满屏幕，demo 在上面叠加自己的控制面板：播放 / 暂停、拖动进度、倍速、清晰度、选集；播放选项；页面样式；`autoHide`、`setRegionVisible`、`setControlsHeld`；以及每次调用和播放事件的日志。它的 SDK 调用都在 `LabActions.kt` / `LabActions.swift`。从演示首页进入。
- `catalog/SdkApiCatalog.kt` / `SdkApiCatalog.swift`：两个版本都有的接口和可直接复制的示例（播放控制、播放选项、播放记录、播放页区域、自建播放器、收入回传等）。
- `catalog/EditionApiCatalog.kt` / `EditionApiCatalog.swift`：商业版独有的接口：广告同意、广告事件，以及用 `unlockWithRewardedAd`、`getRewardedAdProgress` 自定义解锁界面。
- `ui/`（Android 和 iOS）：只是演示界面。

## 接口索引

节号指 `SdkApiCatalog`（Android 为 `catalog/SdkApiCatalog.kt`，iOS 为 `Catalog/SdkApiCatalog.swift`），两个平台的节号一致。

| 接口 | 位置 | 用途 |
|---|---|---|
| `initialize` / `start` | 上面的接入步骤 | 必需的初始化 |
| `setLoggingEnabled` | §1 | SDK 日志 |
| `setStrategy` | §2 | 资源策略 |
| `setColorPalette` / `resetColorPalette`、`setSubtitleStyle` / `resetSubtitleStyle` | §3 | 主题颜色和字幕 |
| Android：`openDramaPlayerPage`、`createDramaHomeFragment`、`createDramaPlayerFragment`、`DramaHomeScreen`、`DramaPlayerScreen`；iOS：`dramaHome` 各种写法、`dramaPlayer` | §4 | 展示 SDK 页面的其他方式 |
| `login` / `setLanguage` | §5 | 用户或应用语言变化 |
| `setPlayerEventListener` | §6 | 播放事件 |
| `getDramaList` | §7 | 自己的短剧列表 |
| `setPlayerControllerListener`；控制器的 `play`、`pause`、`seekTo`、`setPlaybackSpeed`、`setQuality`、`selectEpisode`、`setListener` | §8.1 - 8.2；可运行：实验室 | 用自己的界面控制 SDK 播放页 |
| `setContentListener` / `setProgressListener` | §8.3；可运行：`customization/PlayerFeed` | 给自己的控件提供内容和进度 |
| `setControlsHeld` | §8.4；可运行：实验室 | 自己的弹窗盖住播放页时让页面控件保持显示 |
| `setAutoPlay` / `setAutoAdvance` / `setResumeFromHistory` | §9；可运行：实验室设置 | 播放选项 |
| `getPlaybackHistory` / `removePlaybackHistory` / `clearPlaybackHistory` | §10 | 继续观看 |
| `setVideoRegion` / `setInfoRegion` / `setControlsRegion`、`DramaPlayerRegion` | §11；可运行：`customization/` | 替换播放页的部分区域 |
| `DramaPlayerRegion(autoHide)`、`setRegionVisible`、`state.controlsVisible` | §11；可运行：实验室设置 | 自己控制区域的显示和隐藏 |
| `createPlayer`；Android `ContentPlayerView` / `createPlayerView`，iOS `playerViewController` | §12 | 在自己的界面里放播放器 |
| `reportAdRevenue` / `reportPurchaseRevenue` | §13 | 上报应用自己获得的收入 |
| `showAdConsentIfRequired` | 接入步骤 4（Android）/ 3（iOS）；Android 协程写法：`EditionApiCatalog` C1 | 广告同意 |
| `setAdEventListener` | `EditionApiCatalog` C2（Android）/ C1（iOS） | 广告事件 |
| `setUnlockView`、`setUnlockContent`（Android） | `EditionApiCatalog` C3（Android）/ C2（iOS）；可运行：`customization/` 的样式 | 自己的解锁界面 |
| `unlockWithRewardedAd` / `getRewardedAdProgress` | `EditionApiCatalog` C3 / C2；可运行：`customization/unlock/UnlockFlow` | 自己的解锁界面背后使用 SDK 的广告和奖励 |

Android 源码结构（`app/src/main/kotlin/com/lc/content/sdk/demo/`）：

```
DemoApplication.kt   第 3 步：初始化
DemoEdition.kt       凭证、第 4 步：启动（两个版本不同）
MainActivity.kt      第 5 步：打开 SDK 页面
catalog/             每个接口及可复制的示例
customization/       替换播放页的一部分（Compose 和 View）
lab/                 播放器实验室：在 SDK 播放页上叠加你自己的控制面板
ui/                  demo 自己的页面，集成时不需要
```

iOS 源码结构（`ios/ContentSdkDemo/`）：

```
ContentSdkDemoApp.swift   第 2 步：初始化
DemoEdition.swift         凭证、第 3 步：启动（两个版本不同）
SdkIntegration.swift      第 4 步：打开 SDK 页面
Catalog/                  每个接口及可复制的示例
Customization/            替换播放页的一部分（SwiftUI 和 UIKit）
Lab/                      播放器实验室：在 SDK 播放页上叠加你自己的控制面板
UI/                       demo 自己的页面，集成时不需要
```

iOS 使用 SF Symbols，没有需要复制的图标资源。

`app/src/main/res/drawable/ic_*.xml` 里的图标是 Material 图标（Apache License 2.0）。

Android 上商业版独有的接口是扩展函数：`AndroidContentSdk.setUnlockView(...)` 需要 `import com.lc.content.sdk.setUnlockView`。

应用 id：Android `com.lc.content.sdk.demo.commercial`，iOS `com.lc.content.sdk.demo.ios.commercial`。四个 demo（商业版和纯净版、Android 和 iOS）共用同一套 demo 凭证，只能用于 demo；在你自己的 app 里请使用你自己的 App ID / App Secret。

## Demo 环境

| Android | |
|---|---|
| Android Gradle Plugin / Gradle | 9.3.0 / 9.5.0 |
| Kotlin | 2.3.10 |
| compileSdk / targetSdk / minSdk | 37 / 37 / 24 |
| Gradle JDK | 25（本机没有时 Gradle 会自动下载） |

| iOS | |
|---|---|
| Xcode | 26.6 |
| CocoaPods | 1.17.0 |
| 部署版本 | iOS 16.0，iPhone |
| 运行设备 | 真机，或 Apple 芯片 Mac 上的模拟器 |

## 日志

Debug 构建通过 `setLoggingEnabled` 打开 SDK 调试日志，可以看到初始化和页面加载状态。正式包保持关闭。

- Android：在 Logcat 中按 `ContentSdk` 筛选。
- iOS：在 Xcode 控制台中按 `ContentSdk` 筛选。
