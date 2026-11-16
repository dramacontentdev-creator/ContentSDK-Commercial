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

## 常用 API 用法

下面每项可以展开，写了调用时机、注意事项和 Android / iOS 示例。完整示例见 `SdkApiCatalog`。

<details>
<summary><b>主题颜色 setColorPalette / resetColorPalette</b></summary>

- 随时可以调用，已打开的 SDK 页面也会立即更新；通常在 `start` 前设置一次。
- SDK 内置 `Colors.light`（默认）和 `Colors.dark` 两套配色。要跟随应用主题，就在应用切换深浅色时再调用一次。
- 自定义配色：从内置配色复制，只替换要改的字段。颜色用 `Colors.argb(0xAARRGGBB)` 创建（iOS `Colors.shared.argb(argb:)`）。
- 常用字段：页面底色 `background.background_1`，卡片背景 `background.background_2`，主要文字 `textIcon.default_1`，品牌色 `textIcon.brand` + `fills.brand_default` + `line.brand`（三处一起改）。

Android:

```kotlin
// Built-in palettes: Colors.light (default) / Colors.dark
AndroidContentSdk.setColorPalette(Colors.dark)

// Custom: start from a built-in palette and replace only what you need.
// The brand color lives in three groups; change them together.
val brand = Colors.argb(0xFFFF6A00)
val base = Colors.light
AndroidContentSdk.setColorPalette(
    base.copy(
        textIcon = base.textIcon.copy(brand = brand),
        fills = base.fills.copy(brand_default = brand),
        line = base.line.copy(brand = brand),
        background = base.background.copy(background_1 = Colors.argb(0xFFF7F7F7)),
    ),
)

// Back to the SDK default (light)
AndroidContentSdk.resetColorPalette()
```

iOS:

```swift
// Built-in palettes: Colors.shared.light (default) / Colors.shared.dark
IosContentSdk.shared.setColorPalette(colors: Colors.shared.dark)

// Custom: doCopy takes every parameter; pass the original value for the ones you keep.
let brand = Colors.shared.argb(argb: 0xFFFF6A00)
let base = Colors.shared.light
let t = base.textIcon, f = base.fills, l = base.line
let colors = base.doCopy(
    mask: base.mask,
    textIcon: t.doCopy(
        brand: brand,
        white_1: t.white_1, white_2: t.white_2, white_3: t.white_3, white_4: t.white_4, white_5: t.white_5,
        default_1: t.default_1, default_2: t.default_2, default_3: t.default_3, default_4: t.default_4
    ),
    background: base.background,
    fills: f.doCopy(brand_default: brand, white: f.white, defalt: f.defalt),
    line: l.doCopy(
        brand: brand,
        line_1: l.line_1, line_2: l.line_2, line_3: l.line_3, line_4: l.line_4, line_5: l.line_5
    ),
    error: base.error,
    player: base.player,
    feed: base.feed,
    placeholder: base.placeholder
)
IosContentSdk.shared.setColorPalette(colors: colors)

// Back to the SDK default (light)
IosContentSdk.shared.resetColorPalette()
```

</details>

<details>
<summary><b>字幕样式 setSubtitleStyle / resetSubtitleStyle</b></summary>

- 随时可以调用，屏幕上已显示的字幕也会更新。
- 只覆盖你设置的字段，其余保持字幕文件里的样式或 SDK 默认值。
- 字段：`fontScale`（0.5~2.0，基准 18sp）、`textColor`、`backgroundColor`、`outlineColor`、`bold`、`bottomMargin`（距底边比例 0~0.4）。

Android:

```kotlin
AndroidContentSdk.setSubtitleStyle(SubtitleStyle(fontScale = 1.2f, textColor = Colors.argb(0xFFFFFF00)))
// Back to the subtitle file styles and SDK defaults
AndroidContentSdk.resetSubtitleStyle()
```

iOS:

```swift
let style = SubtitleStyle(
    fontScale: 1.2,
    textColor: Colors.shared.argb(argb: 0xFFFFFF00),
    backgroundColor: Colors.shared.unspecified, // unspecified = keep the file / SDK default
    outlineColor: Colors.shared.unspecified,
    bold: nil,
    bottomMargin: nil
)
IosContentSdk.shared.setSubtitleStyle(style: style)
// Back to the subtitle file styles and SDK defaults
IosContentSdk.shared.resetSubtitleStyle()
```

</details>

<details>
<summary><b>资源策略 setStrategy</b></summary>

- 默认 `Adaptive`，按设备性能和剩余存储自动调整，一般不用调用。
- 要固定档位时在 `start` 前调用：`Standard`（主流设备）、`LowPerformance`（入门设备）、`HighPerformance`（高端设备）。

Android:

```kotlin
AndroidContentSdk.setStrategy(ContentSdkStrategy.Standard)
```

iOS:

```swift
IosContentSdk.shared.setStrategy(strategy: ContentSdkStrategy.standard)
```

</details>

<details>
<summary><b>用户和语言 login / setLanguage</b></summary>

- `initialize` 时已经传了用户 ID 就不用再调 `login`。之后用户登录、切换账号或退出（传空字符串）时调用，观看记录跟随用户。
- 应用内切换语言后调用 `setLanguage`，之后的内容请求使用新语言；回调里的 `effectiveLanguage` 是 SDK 实际使用的语言，不支持的语言按 `en-US` 处理。

Android:

```kotlin
// Login, account switch, or logout ("" = anonymous user)
AndroidContentSdk.login(userId) { error ->
    if (error != null) Log.w(TAG, "login failed: ${error.code} ${error.msg}")
}
// App language changed, e.g. "en-US", "zh-CN"
AndroidContentSdk.setLanguage("zh-CN") { effectiveLanguage, error -> }
```

iOS:

```swift
IosContentSdk.shared.login(userId: userId) { error in
    if let error { print("login failed: \(error.code) \(error.msg)") }
}
IosContentSdk.shared.setLanguage(language: "zh-CN") { effectiveLanguage, error in
    print("SDK language = \(effectiveLanguage ?? "-"), error = \(error?.code ?? "none")")
}
```

</details>

<details>
<summary><b>剧集列表 getDramaList</b></summary>

- 用自己的界面展示短剧时使用，`start` 成功后调用。`page` 从 1 开始，返回空列表表示没有更多数据。
- 可选筛选：`dramaIds`（只返回指定剧）、`recentDays`（最近 N 天上线）。
- 每个 `Drama` 有 `id`、`name`、`coverImageUrl`、`episodeSize`、`freeCount`、`description`、`tags`；点击后用 `drama.asPlayerParams()` 打开播放器。

Android:

```kotlin
lifecycleScope.launch {
    try {
        val dramas = AndroidContentSdk.getDramaList(page = 1, pageSize = 20)
        // Filtered: getDramaList(page = 1, pageSize = 20, dramaIds = listOf("YOUR_DRAMA_ID"), recentDays = 7)
        // On tap:
        // AndroidContentSdk.openDramaPlayerPage(context, drama.asPlayerParams())
    } catch (error: SdkException) {
        // Show an error or a retry entry
    }
}
```

iOS:

```swift
IosContentSdk.shared.getDramaList(page: 1, pageSize: 20) { dramas, error in
    if let error { print("getDramaList failed: \(error.code) \(error.msg)") }
    // On tap:
    // let player = IosContentSdk.shared.dramaPlayer(params: drama.asPlayerParams()) { /* close */ }
}
```

</details>

<details>
<summary><b>播放控制 setPlayerControllerListener</b></summary>

- 在应用启动时设置一次，SDK 播放页每次出现都会把它的控制器交给你，页面离开时为 `null` / `nil`。
- `pause()` 后保持暂停，切回前台或切集都不会自动继续，直到再次 `play()` 或用户点击播放。
- 清晰度只提供 `state.availableQualityHeights` 里有的档位；只有一项时不用提供切换。
- 状态监听不包含进度，需要进度条时用 `setProgressListener`（约 300ms 一次）。

Android:

```kotlin
// Once, e.g. in Application.onCreate
AndroidContentSdk.setPlayerControllerListener { controller ->
    playerController = controller            // null when the page leaves
    controller?.setListener { state ->
        playButton.isSelected = state.playWhenReady
    }
}

playerController?.pause()
playerController?.play()
playerController?.seekTo(30_000)
playerController?.setPlaybackSpeed(1.5f)
playerController?.selectEpisode(3)
// Quality: only offer what state.availableQualityHeights lists
playerController?.setQuality(PlayerQualitySelection(PlayerQualityMode.Manual, requestedHeight = 720))
```

iOS:

```swift
final class SdkPlayerPages: NSObject, PlayerControllerListener, ContentPlayerListener {
    private(set) var controller: ContentPlayerController?
    func onPlayerControllerChanged(controller: ContentPlayerController?) {
        self.controller = controller         // nil when the page leaves
        controller?.setListener(listener: self)
    }
    func onStateChanged(state: ContentPlayerState) { /* update your UI */ }
}
IosContentSdk.shared.setPlayerControllerListener(listener: SdkPlayerPages())

controller.pause()
controller.play()
controller.seekTo(positionMs: 30_000)
controller.setPlaybackSpeed(speed: 1.5)
controller.selectEpisode(episodeNumber: 3)
controller.setQuality(selection: PlayerQualitySelection(mode: .manual, requestedHeight: 720))
```

</details>

<details>
<summary><b>播放选项 setAutoPlay / setAutoAdvance / setResumeFromHistory</b></summary>

- `setAutoPlay`：剧集准备好后自动开始播放，默认 `true`。关闭后等用户点击或应用调用 `play()`。
- `setAutoAdvance`：一集播完自动切到下一集，默认 `true`。
- `setResumeFromHistory`：打开剧集且没有指定集数时，从播放记录的集数和位置继续，默认 `true`。
- 修改从每个播放器的下一集开始生效，不打断正在播放的内容。

Android:

```kotlin
AndroidContentSdk.setAutoPlay(true)
AndroidContentSdk.setAutoAdvance(true)
AndroidContentSdk.setResumeFromHistory(true)
```

iOS:

```swift
IosContentSdk.shared.setAutoPlay(enabled: true)
IosContentSdk.shared.setAutoAdvance(enabled: true)
IosContentSdk.shared.setResumeFromHistory(enabled: true)
```

</details>

<details>
<summary><b>播放记录 getPlaybackHistory</b></summary>

- 按当前登录用户在本机记录，最新的在前；最多保留 50 部，30 天没看的自动删除。登录前为空。
- 可用来做「继续观看」，点击后用 `Drama.PlayerParams(id = record.dramaId)` 打开即可从记录处继续。

Android:

```kotlin
lifecycleScope.launch {
    val records = AndroidContentSdk.getPlaybackHistory(limit = 20)
    // record.dramaId, dramaName, coverImageUrl, episodeNumber, positionMs, durationMs, updatedAtMs
}
AndroidContentSdk.removePlaybackHistory("YOUR_DRAMA_ID")
AndroidContentSdk.clearPlaybackHistory()
```

iOS:

```swift
IosContentSdk.shared.getPlaybackHistory(limit: 20) { records in
    for record in records { print("\(record.dramaName) #\(record.episodeNumber) at \(record.positionMs) ms") }
}
IosContentSdk.shared.removePlaybackHistory(dramaId: "YOUR_DRAMA_ID")
IosContentSdk.shared.clearPlaybackHistory()
```

</details>

<details>
<summary><b>播放事件 setPlayerEventListener</b></summary>

- 只保留一个监听器，再次设置会替换；回调在主线程按顺序执行，监听器抛出的异常不影响播放。
- 每个事件带 `resource`（当前剧和集，还没加载时为空），多数事件还带 `progress`。
- 其他事件：`PlaybackStateChanged`、`PlaybackProgressUpdated`、`PositionDiscontinuity`、`PlaybackSpeedChanged`、`QualityChanged`、`QualityResolved`、`ControlDispatched`。

Android:

```kotlin
AndroidContentSdk.setPlayerEventListener { event ->
    when (event) {
        is PlayerEvent.FirstFrameRendered -> { /* event.startupDurationMs */ }
        is PlayerEvent.PlaybackEnded -> { }
        is PlayerEvent.PlaybackError -> { /* event.error.code, event.recoverable */ }
        else -> Unit
    }
}
AndroidContentSdk.setPlayerEventListener(null) // stop, e.g. on logout
```

iOS:

```swift
final class PlayerEventHandler: NSObject, PlayerEventListener {
    func onPlayerEvent(event: PlayerEvent) {
        if let e = event as? PlayerEvent.FirstFrameRendered { print(e.startupDurationMs) }
        else if event is PlayerEvent.PlaybackEnded { }
        else if let e = event as? PlayerEvent.PlaybackError { print(e.error.code, e.recoverable) }
    }
}
IosContentSdk.shared.setPlayerEventListener(listener: PlayerEventHandler())
IosContentSdk.shared.setPlayerEventListener(listener: nil) // stop
```

</details>

<details>
<summary><b>播放页区域 setVideoRegion / setInfoRegion / setControlsRegion</b></summary>

- 播放页分三个区域：`video`（封面、播放按钮、错误提示）、`info`（返回按钮和简介）、`controls`（进度条、选集、设置）。每个区域可以用 SDK 版本、换成你的界面或不显示。
- 在应用启动时设置一次，对所有 SDK 播放页生效，已打开的页面也会更新。传 `DramaPlayerRegion()` 恢复 SDK 版本。
- 你的界面铺满整个播放页，每切一集重新创建；空白处不要设点击监听，否则滑动和点击传不到播放页。
- 默认跟随 SDK 控制栏一起显示和隐藏；传 `autoHide = false` 后由你调用 `controller.setRegionVisible` 控制。内容比 SDK 控件高时用 `bottomInsetDp` 让字幕上移。
- 可运行的完整写法见 `customization/`。

Android:

```kotlin
// Controls region: your own view (gets the page's controller)
AndroidContentSdk.setControlsRegion(
    DramaPlayerRegion(view = DramaPlayerRegionView { context, controller ->
        MyControls(context).apply { bind(controller) }
    }),
)
// Info region: hide the SDK intro and back button
AndroidContentSdk.setInfoRegion(DramaPlayerRegion(useDefault = false))
// Video region: back to the SDK version
AndroidContentSdk.setVideoRegion(DramaPlayerRegion())
// Compose: DramaPlayerRegion(content = { controller -> MyControls(controller) })
```

iOS:

```swift
final class MyControlsRegion: NSObject, DramaPlayerRegionView {
    func createView(controller: ContentPlayerController) -> UIView { MyControlsView(controller: controller) }
}
IosContentSdk.shared.setControlsRegion(region: DramaPlayerRegion(useDefault: true, view: MyControlsRegion()))
IosContentSdk.shared.setInfoRegion(region: DramaPlayerRegion(useDefault: false, view: nil))
IosContentSdk.shared.setVideoRegion(region: DramaPlayerRegion(useDefault: true, view: nil))
```

</details>

<details>
<summary><b>自建播放器 createPlayer</b></summary>

- 不打开 SDK 播放页，把播放器放进自己的界面（列表项、Tab、弹层都可以）。选集、授权、预加载和播放事件仍由 SDK 处理。
- `ContentPlayer` 也是控制器，可以调用上面所有播放控制方法。同一个播放器同一时间只能显示一个视图。
- 视图被移除、隐藏或页面进入后台时自动暂停，重新显示后恢复。不再使用时调用 `release()`。

Android:

```kotlin
val player = AndroidContentSdk.createPlayer()
player.open(Drama.PlayerParams(id = "YOUR_DRAMA_ID", episodeOrder = 1))
// View; ContentPlayerOverlays.None draws only video and subtitles
container.addView(AndroidContentSdk.createPlayerView(context, player, ContentPlayerOverlays.Default))
// Compose: AndroidContentSdk.ContentPlayerView(player, Modifier.fillMaxSize(), ContentPlayerOverlays.None)
player.release() // when your screen goes away
```

iOS:

```swift
let player = IosContentSdk.shared.createPlayer()
player.open(params: Drama.PlayerParams(id: "YOUR_DRAMA_ID", episodeOrder: 1, drama: nil))
let playerVC = IosContentSdk.shared.playerViewController(player: player, overlays: .default_)
parent.addChild(playerVC)
playerVC.view.frame = container.bounds
container.addSubview(playerVC.view)
playerVC.didMove(toParent: parent)
```

</details>

<details>
<summary><b>收入回传 reportAdRevenue / reportPurchaseRevenue</b></summary>

- 应用自己展示的广告：每次展示调用一次 `reportAdRevenue`，通常在广告平台的收益回调里。应用自己的内购或订阅：每次支付成功调用一次 `reportPurchaseRevenue`。
- SDK 自己展示的广告由 SDK 上报，不要重复上报。
- 所有字段必传，金额按美元。`valueUsd` 是这次展示的价值（不是 eCPM）。
- `dedupeId` 用于去重：广告传广告平台的 response id，内购传商店订单号（Google Play `orderId`、App Store `transactionId`）。不同的展示或支付必须用不同的 id。

Android:

```kotlin
val resource = controller.state.resource ?: return
AndroidContentSdk.reportAdRevenue(
    AdRevenueReport(
        aboutUnlock = true,
        resource = resource,
        valueUsd = adValue.valueMicros / 1_000_000.0,
        precision = AdRevenuePrecision.Precise,
        adNetworkName = adSourceName.ifBlank { "unknown" },
        adType = RevenueAdType.Rewarded,
        dedupeId = responseId,
    ),
)
AndroidContentSdk.reportPurchaseRevenue(
    PurchaseRevenueReport(aboutUnlock = true, resource = resource, revenueUsd = 4.99, dedupeId = orderId),
)
```

iOS:

```swift
guard let resource = controller.state.resource else { return }
IosContentSdk.shared.reportAdRevenue(report: AdRevenueReport(
    aboutUnlock: true,
    resource: resource,
    valueUsd: valueUsd,
    precision: .precise,
    adNetworkName: adSourceName.isEmpty ? "unknown" : adSourceName,
    adType: .rewarded,
    dedupeId: responseId
))
IosContentSdk.shared.reportPurchaseRevenue(report: PurchaseRevenueReport(
    aboutUnlock: true, resource: resource, revenueUsd: 4.99, dedupeId: transactionId
))
```

</details>

<details>
<summary><b>广告同意 showAdConsentIfRequired</b></summary>

- Google UMP 用户同意流程，可选。应用已经自己处理 UMP 时不用调用。
- 在 `start` 前、在可以展示弹窗的 `Activity` / `UIViewController` 中调用；按 `canRequestAds` 决定是否继续 `start`。
- Android 上是扩展函数，需要 `import com.lc.content.sdk.showAdConsentIfRequired`。

Android:

```kotlin
import com.lc.content.sdk.showAdConsentIfRequired

AndroidContentSdk.showAdConsentIfRequired(activity) { consent ->
    if (!consent.canRequestAds) return@showAdConsentIfRequired   // consent.errorMessage for details
    AndroidContentSdk.start { error -> /* open SDK pages when error == null */ }
}
```

iOS:

```swift
IosContentSdk.shared.showAdConsentIfRequired(viewController: viewController) { consent in
    guard consent.canRequestAds else { return }
    IosContentSdk.shared.start { error in /* open SDK pages when error == nil */ }
}
```

</details>

<details>
<summary><b>广告事件 setAdEventListener</b></summary>

- 只保留一个监听器，再次设置会替换，传 `null` / `nil` 停止。
- 每个事件带 `ad`：`format`（`Rewarded` / `Interstitial`）、`scene`、`adUnitId`。
- `Paid` 的 `valueMicros` 是百万分之一单位（`1500000` 表示 1.5），币种见 `currencyCode`。
- 其他事件：`LoadStarted`、`Loaded`、`Clicked`、`Dismissed`。

Android:

```kotlin
AndroidContentSdk.setAdEventListener { event ->
    when (event) {
        is AdEvent.Impression -> { /* event.ad.format, event.ad.scene */ }
        is AdEvent.RewardEarned -> { /* event.rewardType, event.rewardAmount */ }
        is AdEvent.Paid -> { /* event.valueMicros, event.currencyCode */ }
        is AdEvent.Error -> { /* event.stage, event.error.code */ }
        else -> Unit
    }
}
AndroidContentSdk.setAdEventListener(null)
```

iOS:

```swift
final class AdEventHandler: NSObject, AdEventListener {
    func onAdEvent(event: AdEvent) {
        if event is AdEvent.Impression { print(event.ad.format) }
        else if let e = event as? AdEvent.RewardEarned { print(e.rewardAmount, e.rewardType) }
        else if let e = event as? AdEvent.Paid { print(e.valueMicros, e.currencyCode) }
        else if let e = event as? AdEvent.Error { print(e.stage, e.error.code) }
    }
}
IosContentSdk.shared.setAdEventListener(listener: AdEventHandler())
IosContentSdk.shared.setAdEventListener(listener: nil)
```

</details>

<details>
<summary><b>自定义解锁界面 setUnlockView / unlockWithRewardedAd</b></summary>

- 锁定剧集总会显示解锁界面，默认是 SDK 的激励广告解锁层；用 `setUnlockView` 换成你的界面，对 SDK 播放页和 `createPlayer` 都生效。
- 在你的按钮里调用 `unlockWithRewardedAd`，结果：`Unlocked`（已解锁，自动继续播放）、`NotCompleted`、`DailyLimitReached`、`NoAdAvailable`、`MovedToFirstLockedEpisode`（已跳到第一个锁定集，再调一次）、`Checking`、`InProgress`、`NotLocked`、`Failed`。
- `getRewardedAdProgress` 返回本剧今日的 `watchedCount` / `dailyLimit`，可显示「今日已看 2/5」。
- Android 上这些是扩展函数，需要分别 `import`。

Android:

```kotlin
import com.lc.content.sdk.setUnlockView
import com.lc.content.sdk.unlockWithRewardedAd
import com.lc.content.sdk.getRewardedAdProgress

AndroidContentSdk.setUnlockView(
    DramaPlayerRegionView { context, controller ->
        MyUnlockView(context).apply {
            // "2/5 today": AndroidContentSdk.getRewardedAdProgress(controller)?.watchedCount / dailyLimit
            onUnlockClick = {
                AndroidContentSdk.unlockWithRewardedAd(controller) { result ->
                    // Unlocked: playback continues; NotCompleted; DailyLimitReached; NoAdAvailable; ...
                }
            }
        }
    },
)
// Compose: AndroidContentSdk.setUnlockContent { controller -> ... }
// Back to the SDK unlock layer: AndroidContentSdk.setUnlockView(null)
```

iOS:

```swift
final class UnlockRegion: NSObject, DramaPlayerRegionView {
    func createView(controller: ContentPlayerController) -> UIView {
        // "2/5 today": IosContentSdk.shared.getRewardedAdProgress(controller: controller)
        let button = UIButton(type: .system, primaryAction: UIAction(title: "Watch an ad to unlock") { _ in
            IosContentSdk.shared.unlockWithRewardedAd(controller: controller) { result in
                // .unlocked: playback continues; .notCompleted; .dailyLimitReached; .noAdAvailable; ...
            }
        })
        return button
    }
}
IosContentSdk.shared.setUnlockView(view: UnlockRegion())
```

</details>

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
