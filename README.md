# Kid Safe Launcher (V1)

儿童健康限制安卓桌面（V1），可作为默认 Launcher，提供应用展示、家长 PIN、应用白名单限制、系统设置入口和设备信息展示。

## 功能清单

- Launcher 基础
  - 声明 `HOME/DEFAULT`，可设置为默认桌面
  - 展示已安装可启动应用（图标 + 名称）
  - 应用名称搜索过滤
- 家长控制（V1）
  - 首次设置 4~6 位数字 PIN
  - 后续进入家长模式需 PIN 校验
  - **白名单限制策略**：白名单非空时，非白名单应用受限
  - 受限应用点击提示，不崩溃
- 应用管理
  - 应用详情页：打开应用、发起卸载
  - 系统应用/不可卸载应用友好提示
- 连接与系统入口
  - Wi‑Fi 设置
  - 蓝牙设置
  - 网络/以太网设置（不支持时提示）
- 设备信息
  - 品牌、型号、Android 版本、SDK、ABI、可用/总存储
- 屏幕适配
  - 小屏紧凑网格
  - 大屏更高列数网格，并显示顶部状态区域（时间、应用数量）
  - 应用内下拉快捷面板（Wi‑Fi/蓝牙/显示设置）
- 稳定性
  - `ActivityNotFoundException` / `SecurityException` 统一兜底提示
  - 首次启动引导（默认桌面设置与 PIN 建议）

## 权限与系统限制说明

- V1 采用普通应用权限能力，不依赖系统签名。
- “深度限制安装/卸载/系统开关控制”受 Android 系统权限与厂商策略限制。
- 若需更强管控能力，建议后续支持 Device Owner（企业设备管理）模式。

## 本地构建

```bash
./gradlew lint test assembleDebug
```

Debug APK：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Docker 构建与测试

> 推荐：`cimg/android`

### 1) cimg/android（推荐）

```bash
docker run --rm -it \
  -v "$PWD":/workspace \
  -w /workspace \
  cimg/android:2025.01 \
  bash -lc "chmod +x gradlew && ./gradlew lint test assembleDebug"
```

### 2) thyrlian/android-sdk（可选）

```bash
docker run --rm -it \
  -v "$PWD":/workspace \
  -w /workspace \
  thyrlian/android-sdk:latest \
  bash -lc "chmod +x gradlew && ./gradlew lint test assembleDebug"
```

### 3) inovex/gitlab-ci-android（可选）

```bash
docker run --rm -it \
  -v "$PWD":/workspace \
  -w /workspace \
  inovex/gitlab-ci-android:latest \
  bash -lc "chmod +x gradlew && ./gradlew lint test assembleDebug"
```

### 4) androidbuilder/android-builder（可选）

```bash
docker run --rm -it \
  -v "$PWD":/workspace \
  -w /workspace \
  androidbuilder/android-builder:latest \
  bash -lc "chmod +x gradlew && ./gradlew lint test assembleDebug"
```

## CI 说明

GitHub Actions 工作流：`.github/workflows/android-ci.yml`

包含：
- checkout
- JDK 17
- Gradle 缓存
- `./gradlew lint test assembleDebug`
- 上传 `app-debug.apk` 构建产物

## 安装并设置为默认桌面

1. 安装 Debug APK 到设备。
2. 按 Home 键，系统会提示选择默认桌面。
3. 选择 **儿童健康桌面** 并设为默认。
4. 在右上角进入家长模式，完成 PIN 设置与白名单配置。

## 已知限制与后续路线

- 以太网设置入口在部分设备不可用。
- 普通应用无法完全阻止系统级安装/卸载行为。
- 后续路线：
  - Device Owner 模式增强
  - 更细粒度时间配额与使用统计
  - 更完整的大屏状态信息与可视化
