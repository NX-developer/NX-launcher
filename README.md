# NX Launcher

NX Launcher is an open source Minecraft: Java Edition launcher for Android. The goal of the project is to run the desktop Java edition of Minecraft on phones and tablets with a clean, modern interface and a built in mod browser.

This repository currently contains **phase one: the user interface**. Game execution (Java runtime, renderers, account login and the actual launch flow) will be implemented in phase two. Every button that performs a launch or download action is present in the UI but is intentionally inactive for now.

## Status

| Area | State |
| --- | --- |
| Launcher UI | Done |
| Version browser UI | Done |
| Mod browser UI (Modrinth + CurseForge) | Done |
| Settings UI | Done |
| Game launch | Phase 2 |
| Real mod download | Phase 2 |
| Account login | Phase 2 |

## Features in this build

### Home
A single launcher hub with the selected version, a large play button, the active account, quick performance stats and a news area.

### Version browser
A list of Minecraft versions with a Release / Snapshot filter, release dates, the available mod loaders per version and an installed indicator.

### Mod browser
This is the main feature of phase one.

- Switch between **Modrinth** and **CurseForge** on a single page with one tap.
- Search mods by name or author.
- **Version filter**: pick a Minecraft version (for example `1.21`) and only mods that support that version are shown.
- **Loader filter**: pick Fabric, Forge, Quilt or NeoForge.
- **Pagination**: first page, previous page, current page indicator, next page and last page controls.
- Tapping a mod opens a detail page that shows its **requirements**: supported versions, supported loaders and dependencies (required or optional). There is no download button in this phase by design.

### Settings
- **Resolution scale** slider to lower the render resolution for more FPS on weaker devices.
- **Allocated memory** slider for the amount of RAM reserved for the game.
- **Renderer** selection.
- **Java runtime** selection.
- **Controls**: touch controls, gamepad support and gesture controls.
- **System**: keep screen on and high process priority.
- Custom JVM arguments entry.

## Where the features come from

NX Launcher takes inspiration from the existing mobile Java launcher ecosystem. The table below lists each idea and the launcher it was adapted from. These are interface and feature references only; no code from those projects is used here.

| NX Launcher feature | Inspired by |
| --- | --- |
| Resolution scale slider (lower resolution for more FPS) | PojavLauncher graphics settings |
| RAM allocation control | PojavLauncher Java settings |
| Custom JVM arguments | PojavLauncher Java settings |
| Renderer selection (Zink, GL4ES, Holy GL4ES, VirGL) | PojavLauncher renderers |
| Java runtime selection (Java 8 / 17 / 21) | PojavLauncher bundled OpenJDK runtimes |
| Touch controls, gamepad and gesture toggles | PojavLauncher control editor and FoldCraftLauncher |
| Release / Snapshot version filter and loader badges | PojavLauncher and Amethyst version manager |
| Modrinth and CurseForge in app browser with version and loader filters | Modrinth App and FoldCraftLauncher / Zalith Launcher mod download flow |
| Offline / local account chip | PojavLauncher local account login |
| Single launcher hub layout with a large play button | Modern launcher home screens such as Amethyst |

Referenced projects: PojavLauncher (LGPL-3.0), its successor Amethyst, FoldCraftLauncher and Zalith Launcher.

## Tech

- Language: Kotlin
- UI: Jetpack Compose with Material 3
- Architecture: single activity with Compose Navigation
- Minimum SDK: 21 (Android 5.0)
- Target SDK: 34
- License: AGPL-3.0

The mod, version and account data in this phase comes from local sample data in `app/src/main/java/com/nxlauncher/data/sample`. In phase two this layer will be replaced by the real Modrinth and CurseForge APIs.

## Project structure

```
NX-Launcher/
├── app/                         Android application module
│   └── src/main/java/com/nxlauncher/
│       ├── MainActivity.kt       Navigation host and bottom bar
│       ├── data/model/           Data classes and enums
│       ├── data/sample/          Sample data for the UI phase
│       ├── navigation/           Routes and bottom navigation items
│       └── ui/
│           ├── screens/          Home, Versions, Mods, ModDetail, Settings
│           ├── components/       Reusable composables
│           └── theme/            Colors, typography and theme
├── website/                     Landing page deployed to GitHub Pages
├── .github/workflows/           CI for the APK and the website
└── README.md
```

The `website/` folder is a separate static site. It is excluded from the Gradle build and from the APK workflow, so it never affects the build.

## Building

Requirements: JDK 17 and the Android SDK.

```bash
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/`.

### Continuous integration

- `Build APK` runs on every push that touches the app and produces a debug and a release APK as artifacts. On a failed build it uploads an `error.txt` artifact containing the extracted build errors.
- `Deploy Website` runs only when the `website/` folder changes and publishes the landing page to GitHub Pages.

## Roadmap

1. **Phase 1 (current):** complete launcher, version, mod browser and settings UI.
2. **Phase 2:** Java runtime integration, renderers, account login and real game launch.
3. **Phase 3:** live Modrinth and CurseForge APIs with real mod installation.
4. **Phase 4:** in game overlay, control editor and profile management.

## License

NX Launcher is licensed under the GNU Affero General Public License v3.0. See `LICENSE` for details.

This project is not affiliated with Mojang or Microsoft. Minecraft is a trademark of Mojang AB.
