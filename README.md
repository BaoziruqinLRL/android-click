# 连点器 Android 项目

## 环境要求

- JDK 17+
- Android SDK（API 34）
- Android Studio Ladybug 或更高版本（推荐）

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

> 需已安装 Android SDK，且 `gradle.properties` 中 `org.gradle.java.home` 指向 JDK 17+。

## 项目结构

详见 [plan.md](./plan.md) 与 [dev_plan.md](./dev_plan.md)。

当前进度：**P0 项目初始化已完成**，下一步为 P1（无障碍服务 + 单点连点）。
