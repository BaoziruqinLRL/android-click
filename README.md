# 连点器 Android 项目

## 环境要求

- JDK 21（路径：`C:\Program Files\Java\jdk-21`）
- Android SDK（推荐 API 34+，当前机器已安装 API 36.1）
- Android Studio Ladybug 或更高版本（推荐）

## 安装 / 更新 Android SDK

### 方式一：Android Studio（推荐，国内网络友好）

1. 打开 **Android Studio → Settings → Languages & Frameworks → Android SDK**
2. **SDK Platforms** 勾选：`Android 14.0 (API 34)` 或更高
3. **SDK Tools** 勾选：`Android SDK Build-Tools`、`Android SDK Platform-Tools`、`Android SDK Command-line Tools`
4. 点击 **Apply** 下载

SDK 默认路径：`%LOCALAPPDATA%\Android\Sdk`

### 方式二：命令行脚本

```powershell
cd C:\Project\cursor\androidClick
.\scripts\install-android-sdk.ps1
```

> 脚本会从腾讯云镜像安装 cmdline-tools；其余组件若无法访问 Google 源，仍需通过 Android Studio 补装。

## 首次打开

1. 用 Android Studio 打开本项目根目录
2. 等待 Gradle Sync 完成（已配置阿里云 Maven 镜像）
3. 若提示 SDK 路径，在 `local.properties` 中设置（可参考 `local.properties.example`）：

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

4. 运行 `app` 模块到模拟器或真机

## 命令行构建

```bat
gradlew.bat assembleDebug
```

> 需已安装 Android SDK，且 `gradle.properties` 中 `org.gradle.java.home` 指向 JDK 21。

## 项目结构

详见 [plan.md](./plan.md) 与 [dev_plan.md](./dev_plan.md)。

当前进度：**P1 无障碍 + 单点连点已完成**，下一步为 P2（悬浮窗控制）。
