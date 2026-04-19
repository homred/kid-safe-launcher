# Kid Safe Launcher 🛡️

A professional Android launcher designed for child safety. Replaces the system home screen to provide a controlled, kid-friendly environment with parental management features.

## Features

- **Home Screen Replacement** — Acts as the default launcher with app grid display
- **Adaptive Layout** — Supports watch (sw<320dp), phone, phablet, tablet, and TV screens
- **App Grid** — Lists all installed launchable apps with search functionality
- **Quick Settings Panel** — Pull-down gesture reveals WiFi, Bluetooth, Network, and Hardware toggles
- **Status Bar** — Displays clock and connection status on larger screens (tablet/TV)
- **WiFi Management** — Quick access to WiFi settings
- **Bluetooth Management** — Quick access to Bluetooth settings
- **Wired Network Settings** — Configure Ethernet and other connections
- **Hardware Info** — View device name, manufacturer, model, Android version, CPU, RAM, storage, screen, battery
- **App Management** — Uninstall user apps, view app details, search installed apps
- **Package Change Detection** — Automatically refreshes app list on install/uninstall

## Screen Size Support

| Screen Size | Width (dp) | Grid Columns | Status Bar |
|-------------|-----------|--------------|------------|
| Watch       | <320      | 2            | Hidden     |
| Phone       | 320-479   | 3            | Hidden     |
| Phablet     | 480-599   | 4            | Visible    |
| Tablet      | 600-719   | 5            | Visible    |
| TV          | ≥720      | 6            | Visible    |

## Building

### Prerequisites

- Android SDK with `build-tools;35.0.0` and `platforms;android-35`
- Java 11+ (JDK)
- `ANDROID_HOME` environment variable set

### Build APK

```bash
chmod +x build.sh
./build.sh          # Build debug APK
```

The APK will be output to `app/build/KidSafeLauncher-debug.apk`.

### CI Build & Publish APK

- GitHub Actions workflow: `.github/workflows/build-and-publish-apk.yml`
- On `main` branch push / Pull Request / manual trigger: builds APK and uploads CI artifact
- On tag push like `v1.0.0`: builds APK, uploads artifact, and publishes APK to GitHub Release

### Run Tests

```bash
chmod +x test.sh
./test.sh
```

Tests are pure JUnit 4 unit tests covering models, utilities, adapters, and receivers.

## Project Structure

```
app/src/main/
├── java/com/kidsafe/launcher/
│   ├── activities/
│   │   ├── LauncherActivity.java    # Main home screen
│   │   ├── SettingsActivity.java    # Device settings hub
│   │   ├── AppManageActivity.java   # App uninstall management
│   │   └── HardwareInfoActivity.java # Device hardware info
│   ├── adapters/
│   │   ├── AppGridAdapter.java      # Grid view adapter
│   │   ├── AppManageAdapter.java    # List view adapter
│   │   └── AppListManager.java      # Testable data logic
│   ├── models/
│   │   ├── AppInfo.java             # App data model
│   │   ├── DeviceInfo.java          # Device info model
│   │   └── ScreenSize.java          # Screen size enum
│   ├── receivers/
│   │   └── PackageChangeReceiver.java # App install/uninstall listener
│   └── utils/
│       ├── AppUtils.java            # App listing and filtering
│       ├── DeviceUtils.java         # Device hardware info
│       ├── NetworkUtils.java        # WiFi/BT/Network utilities
│       └── ScreenUtils.java         # Screen size detection
├── res/
│   ├── layout/                      # UI layouts
│   ├── values/                      # Strings, colors, dimensions, themes
│   ├── values-sw320dp/              # Watch dimensions
│   ├── values-sw600dp/              # Tablet dimensions
│   ├── values-sw720dp/              # TV dimensions
│   ├── anim/                        # Slide animations
│   └── drawable/                    # Icons and backgrounds
└── AndroidManifest.xml
```

## Permissions

- `ACCESS_WIFI_STATE` / `CHANGE_WIFI_STATE` — WiFi management
- `ACCESS_NETWORK_STATE` — Network status
- `BLUETOOTH` / `BLUETOOTH_ADMIN` / `BLUETOOTH_CONNECT` / `BLUETOOTH_SCAN` — Bluetooth management
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` — WiFi/BT scanning
- `REQUEST_DELETE_PACKAGES` — App uninstall
- `QUERY_ALL_PACKAGES` — List installed apps

## Tech Stack

- **Language**: Java 11
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35
- **UI**: Android native views (Material-inspired flat design)
- **Testing**: JUnit 4, 115+ unit tests, 100% pass rate
