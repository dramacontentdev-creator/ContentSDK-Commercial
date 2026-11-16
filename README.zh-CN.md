# Content SDK Demo

[English](README.md) | 中文

演示 Content SDK 在 Android 和 iOS 上的接入：短剧首页、播放器、播放控制、播放页自定义、激励广告解锁和广告事件。代码里标好了接入步骤，可以直接复制到自己的应用。

本仓库不包含 SDK。向 SDK 提供方获取 SDK 包，按「快速开始」放到指定位置即可运行。

## 仓库结构

```text
android/                         Android 工程（用 Android Studio 打开这个目录）
  settings.gradle.kts            依赖仓库
  app/
    build.gradle.kts             引入 AAR 和依赖脚本
    libs/                        放 SDK 的 AAR（不提交）
    content-sdk-dependencies.gradle   SDK 包里的依赖脚本（不提交）
    src/main/kotlin/com/lc/content/sdk/demo/
      DemoApplication.kt         初始化
      DemoEdition.kt             App ID / App Secret、启动
      MainActivity.kt            打开 SDK 页面
      catalog/                   API 说明和可复制的示例
      customization/             播放页自定义（Compose 和 View 两种写法）
      lab/                       播放器实验室
      ui/                        demo 自己的界面，接入时不需要
ios/                             iOS 工程
  Podfile                        引入 SDK Pod
  ContentSdk/                    放 iOS SDK 包（不提交）
  ContentSdkDemo.xcworkspace     pod install 后生成，用它打开工程
  ContentSdkDemo/
    ContentSdkDemoApp.swift      初始化
    DemoEdition.swift            App ID / App Secret、启动
    SdkIntegration.swift         打开 SDK 页面
    Catalog/                     API 说明和可复制的示例
    Customization/               播放页自定义（SwiftUI 和 UIKit 两种写法）
    Lab/                         播放器实验室
    UI/                          demo 自己的界面，接入时不需要
```

## 快速开始

### Android

1. 从 `ContentSdk-Android-YOUR_SDK_VERSION-release` 中把 `ContentSdk-YOUR_SDK_VERSION.aar` 复制到 `android/app/libs/`。
2. 把同一个包里的 `content-sdk-dependencies.gradle` 复制到 `android/app/`。
3. 用 Android Studio 打开 `android/`，运行 `app`。

### iOS

1. 把 `ContentSdk-iOS-YOUR_SDK_VERSION-release` 里的全部内容复制到 `ios/ContentSdk/`。
2. 在 `ios/` 目录执行 `pod install`。
3. 打开 `ios/ContentSdkDemo.xcworkspace` 运行。

在真机上运行时，新建 `ios/Signing.local.xcconfig`，写入 `DEVELOPMENT_TEAM = YOUR_TEAM_ID`（参见 `ios/Signing.xcconfig`）。这个文件不会提交到 git；如果在 Xcode 的 Signing 页里选 team，它会被写进工程文件。

> AAR、依赖脚本和 iOS SDK 包必须来自同一个 SDK 包，版本一致。

## 接入到你的应用

代码中每个步骤都标有 `Content SDK - integration Step N`，按顺序复制。Android 和 iOS 分别编号：

| 步骤 | Android | iOS |
|---|---|---|
| 1 | `settings.gradle.kts`：依赖仓库，包括广告 SDK 仓库 | `Podfile`：`pod 'ContentSdk'` |
| 2 | `app/build.gradle.kts`：AAR 和依赖脚本 | `ContentSdkDemoApp.swift`：初始化 |
| 3 | `DemoApplication.kt`：初始化 | `DemoEdition.swift`：广告同意 + 启动 |
| 4 | `DemoEdition.kt`：广告同意 + 启动 | `SdkIntegration.swift`：打开 SDK 页面 |
| 5 | `MainActivity.kt`：打开 SDK 页面 | |
| A | `AndroidManifest.xml`：AdMob Application ID | `Info.plist`：AdMob Application ID 和 SKAdNetworkItems |

A 步要和依赖步骤一起做：没有 AdMob Application ID，应用启动时就会停止运行。

然后换成你自己的值：

- App ID / App Secret：在 [https://open.lucky-chick.com](https://open.lucky-chick.com) 获取，填到 `DemoEdition.kt` / `DemoEdition.swift` 的 `SdkConfig` 里。
- AdMob Application ID：`AndroidManifest.xml` / `Info.plist`，demo 用的是 Google 测试 ID。
- iOS：把 SDK 包里的 `SKAdNetworkItems.plist` 合并到自己的 `Info.plist`。

## 功能说明

运行后在 demo 首页可以打开以下功能：

- **SDK 页面**：短剧首页和播放器，由 SDK 提供完整界面。
- **播放页样式**：在首页选择 SDK 默认、Compose / SwiftUI 或 View / UIKit，再打开播放器，可以看到用自己的界面替换播放页的简介区、控制区和选集列表（`customization/`）。选了自定义样式后，锁定剧集的解锁页也换成 demo 自己的界面（`customization/unlock/`），广告和奖励仍由 SDK 的 `unlockWithRewardedAd` 完成。
- **播放器实验室**：SDK 播放页铺满屏幕，demo 在上面叠加自己的控制面板，演示播放 / 暂停、拖动进度、倍速、清晰度、选集、播放选项、区域显示控制，并记录每次调用和播放事件（`lab/` / `Lab/`，SDK 调用集中在 `LabActions.kt` / `LabActions.swift`）。
- **广告解锁**：遇到锁定剧集时，SDK 显示激励广告解锁层，看完广告后自动继续播放。

## API 说明

位置一列的 § 是 `SdkApiCatalog`（Android `catalog/SdkApiCatalog.kt`，iOS `Catalog/SdkApiCatalog.swift`）里的节号，两个平台一致。

| API | 位置 | 用途 |
|---|---|---|
| `initialize` / `start` | 接入步骤 | 初始化并启动 SDK，`start` 成功后才能展示 SDK 页面 |
| `setLoggingEnabled` | §1 | 打开 SDK 调试日志 |
| `setStrategy` | §2 | 资源策略（缓存和预加载档位） |
| `setColorPalette` / `resetColorPalette`、`setSubtitleStyle` / `resetSubtitleStyle` | §3 | 主题颜色、字幕样式 |
| Android：`openDramaHomePage`、`openDramaPlayerPage`、`createDramaHomeFragment`、`createDramaPlayerFragment`、`DramaHomeScreen`、`DramaPlayerScreen`；iOS：`dramaHome`、`dramaPlayer` | §4 | 用 Activity / Fragment / Compose 或 UIKit / SwiftUI 展示短剧首页和播放器 |
| `login` / `setLanguage` | §5 | 用户切换、应用语言切换 |
| `setPlayerEventListener` | §6 | 首帧、播放状态、进度、错误等播放事件 |
| `getDramaList` | §7 | 获取剧集列表，用自己的界面展示 |
| `setPlayerControllerListener`；控制器的 `play`、`pause`、`seekTo`、`setPlaybackSpeed`、`setQuality`、`selectEpisode`、`setListener` | §8.1 - 8.2，实验室 | 在自己的界面上控制 SDK 播放页 |
| `setContentListener` / `setProgressListener` | §8.3，`customization/PlayerFeed` | 给自己的控件提供剧集信息、选集列表和进度 |
| `setControlsHeld` | §8.4，实验室 | 自己的弹窗盖住播放页时让控件保持显示 |
| `setAutoPlay` / `setAutoAdvance` / `setResumeFromHistory` | §9，实验室设置 | 自动播放、自动下一集、从播放记录继续 |
| `getPlaybackHistory` / `removePlaybackHistory` / `clearPlaybackHistory` | §10 | 继续观看列表 |
| `setVideoRegion` / `setInfoRegion` / `setControlsRegion`、`DramaPlayerRegion` | §11，`customization/` | 用自己的界面替换播放页的画面区、简介区、控制区 |
| `DramaPlayerRegion(autoHide)`、`setRegionVisible`、`state.controlsVisible` | §11，实验室设置 | 自己控制区域的显示和隐藏 |
| `createPlayer`；Android `ContentPlayerView` / `createPlayerView`，iOS `playerViewController` | §12 | 不用 SDK 播放页，把播放器放进自己的界面 |
| `reportAdRevenue` / `reportPurchaseRevenue` | §13 | 回传应用自己的广告和内购收入 |
| `showAdConsentIfRequired` | Android 接入步骤 4 / iOS 步骤 3；Android 协程写法：`EditionApiCatalog` C1 | Google UMP 广告同意，在 `start` 前调用 |
| `setAdEventListener` | `EditionApiCatalog` C2（Android）/ C1（iOS） | 广告加载、展示、点击、奖励、收益、关闭等事件 |
| `setUnlockView`、`setUnlockContent`（Android） | `EditionApiCatalog` C3（Android）/ C2（iOS），`customization/` 的样式 | 用自己的界面替换解锁层 |
| `unlockWithRewardedAd` / `getRewardedAdProgress` | `EditionApiCatalog` C3 / C2，`customization/unlock/UnlockFlow` | 在自己的解锁界面里调用 SDK 的激励广告，读取今日观看次数 |

## API 使用指引

1. **先跑通必需步骤**：按「接入到你的应用」的步骤 1 到最后一步复制，`start` 成功后打开短剧首页。只做这一步就能上线基础功能。
2. **按需查 `SdkApiCatalog`**：每一节写了这个 API 什么时候调用、参数含义和一段可以直接复制的示例，节号见上表。其余 API 都是可选的，不调用时使用 SDK 默认值。
3. **广告和解锁看 `EditionApiCatalog`**：广告同意、广告事件和自定义解锁界面都在这里。Android 上这些接口是扩展函数，例如 `AndroidContentSdk.setUnlockView(...)` 需要 `import com.lc.content.sdk.setUnlockView`。
4. **要改播放页界面**：先看 `customization/CustomRegions.kt`（iOS `CustomRegions.swift`），里面是 SDK 调用和说明；再按你的界面技术栈复制 `compose/` 或 `view/`（iOS `SwiftUI/` 或 `UIKit/`）。`PlayerFeed` 把播放器状态转换成界面要画的数据。
5. **要验证播放控制**：在实验室里点一遍对应按钮，看日志里的调用和事件，再到 `LabActions` 找到对应的 SDK 调用。

调用规则：

- 所有 API 在主线程调用，回调也在主线程执行。
- `initialize` 每个进程只调用一次；其余 API 在 `start` 成功后使用。
- 每类监听器只保留一个，再次设置会替换之前的，传 `null` / `nil` 停止回调。

## 运行环境

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

## 其他

- demo 里的 App ID / App Secret 只能用于 demo，你的应用请使用自己的值。
- Android `app/src/main/res/drawable/ic_*.xml` 是 Material 图标（Apache License 2.0）；iOS 使用 SF Symbols，没有需要复制的图标资源。
