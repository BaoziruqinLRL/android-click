# 安卓连点器 — 详细开发计划

> 本文档基于 [plan.md](./plan.md) 展开，将每个里程碑拆解为**可执行的小任务**，粒度到「单个文件 / 单个函数 / 单块 UI」级别。  
> 建议按任务编号顺序推进，完成一项勾选一项（`- [ ]` → `- [x]`）。

---

## 文档约定

| 符号 | 含义 |
|------|------|
| **预估** | 单人开发参考工时 |
| **产出** | 该任务结束时应存在的文件或行为 |
| **验收** | 可手动或自动验证的完成标准 |
| **依赖** | 必须先完成的任务编号 |

**任务编号规则：** `P{阶段}-{序号}`，如 `P1-03` 表示第一阶段第 3 项任务。

**总工期参考：** P0–P5 约 **22 个工作日**；P6 按需追加。

---

## 阶段总览

| 阶段 | 名称 | 工作日 | 核心交付 |
|------|------|--------|----------|
| P0 | 项目初始化 | 2 | 可编译运行的 Compose 骨架 |
| P1 | 无障碍 + 单点连点 | 4 | 单坐标稳定连点 100 次 |
| P2 | 悬浮窗控制 | 3 | 任意界面启停连点 |
| P3 | 编辑器 + 脚本存储 | 4 | 脚本 CRUD + 取点 |
| P4 | 多点 + 随机间隔 + 滑动 | 4 | 顺序/同时多点、滑动手势 |
| P5 | 测试与 Polish | 3 | 主流机型稳定可用 |
| P6 | 进阶功能（可选） | 按需 | 录制、导入导出等 |

---

# P0 — 项目初始化（第 1–2 天）

## P0-01 创建 Android 工程

**预估：** 1h  
**依赖：** 无

- [x] 使用 Android Studio 新建项目：`Empty Activity` + `Jetpack Compose`
- [x] 设置 `applicationId`：`com.example.androidclick`（可按实际修改）
- [x] 设置 `minSdk = 24`，`targetSdk = 34`，`compileSdk = 34`
- [x] 启用 Kotlin，`jvmTarget = 21`

**产出：** 根目录存在 `settings.gradle.kts`、`app/build.gradle.kts`

**验收：** `./gradlew assembleDebug` 成功，模拟器可启动空白 Activity

---

## P0-02 配置 Gradle 依赖与版本目录

**预估：** 1h  
**依赖：** P0-01

- [x] 创建/编辑 `gradle/libs.versions.toml`，锁定版本：
  - Kotlin 1.9+
  - Compose BOM 最新稳定版
  - Hilt 2.48+
  - Room 2.6+
  - Navigation Compose 2.7+
  - DataStore 1.0+
  - Lifecycle / Coroutines
- [x] 在 `app/build.gradle.kts` 引入上述依赖
- [x] 应用 `com.google.dagger.hilt.android` 插件
- [x] 应用 `ksp` 插件（Room、Hilt 注解处理）

**产出：** `libs.versions.toml` 依赖声明完整

**验收：** Sync 无报错；`./gradlew :app:dependencies` 可解析

---

## P0-03 搭建包结构

**预估：** 0.5h  
**依赖：** P0-01

- [x] 在 `app/src/main/java/.../` 下创建目录：

```
ui/home/
ui/editor/
ui/overlay/
ui/settings/
ui/theme/
service/
domain/model/
domain/usecase/
domain/repository/
data/local/
data/repository/
di/
util/
```

**产出：** 包目录与 `plan.md` 3.1 节一致

**验收：** 目录结构可在 IDE 中看到

---

## P0-04 配置 Hilt 与应用入口

**预估：** 1h  
**依赖：** P0-02, P0-03

- [x] 创建 `ClickerApplication.kt`，添加 `@HiltAndroidApp`
- [x] 在 `AndroidManifest.xml` 注册 `android:name=".ClickerApplication"`
- [x] 创建 `di/AppModule.kt`（空模块占位）
- [x] 将 `MainActivity` 标注 `@AndroidEntryPoint`

**产出：** `ClickerApplication.kt`、`AppModule.kt`

**验收：** 应用启动不崩溃；Logcat 无 Hilt 初始化错误

---

## P0-05 搭建 Compose 主题与导航骨架

**预估：** 2h  
**依赖：** P0-04

- [x] 创建 `ui/theme/Color.kt`、`Theme.kt`、`Type.kt`（Material 3）
- [x] 定义亮色 / 暗色配色（暗色完整实现可放到 P4，此处先留接口）
- [x] 创建 `ui/navigation/NavGraph.kt`，路由枚举：
  - `Home`
  - `Editor/{scriptId}`
  - `Settings`
- [x] 创建占位 Composable：`HomeScreen`、`EditorScreen`、`SettingsScreen`（仅显示标题 Text）
- [x] `MainActivity` 使用 `ClickerTheme { NavHost(...) }`

**产出：** 主题文件 + 三页占位 + 导航

**验收：** 可在 Home / Settings 间导航；切换系统深色模式时主题跟随（若已实现暗色）

---

## P0-06 定义领域模型（空壳）

**预估：** 1h  
**依赖：** P0-03

- [x] 创建 `domain/model/ClickMode.kt` 枚举：`SEQUENTIAL`、`SIMULTANEOUS`
- [x] 创建 `domain/model/ClickPoint.kt`（x, y, delayAfterMs）
- [x] 创建 `domain/model/ClickScript.kt`（与 plan.md 6 节字段一致，使用领域类型非 Entity）
- [x] 创建 `domain/model/ClickState.kt` 密封类：`Idle`、`Running`、`Paused`、`Stopped`
- [x] 创建 `domain/model/GestureType.kt` 枚举：`TAP`、`SWIPE`（滑动功能 P4 使用，先定义）

**产出：** `domain/model/` 下 5 个文件

**验收：** 模型可编译；字段与 `plan.md` 数据模型一致

---

## P0-07 配置 AndroidManifest 基础权限占位

**预估：** 0.5h  
**依赖：** P0-01

- [x] 在 `AndroidManifest.xml` 声明权限（先声明，服务后续注册）：
  - `FOREGROUND_SERVICE`
  - `FOREGROUND_SERVICE_SPECIAL_USE`
  - `POST_NOTIFICATIONS`
  - `SYSTEM_ALERT_WINDOW`
- [x] 添加 `tools:targetApi` 注释说明 Android 14 special use 用途

**产出：** Manifest 权限块

**验收：** Manifest merger 无 error

---

## P0-08 初始化 Git 与 .gitignore

**预估：** 0.5h  
**依赖：** P0-01

- [x] 执行 `git init`
- [x] 添加 `.gitignore`：`.idea/`、`local.properties`、`build/`、`.gradle/`、`*.apk` 等
- [x] 首次提交：`chore: init android project skeleton`

**产出：** Git 仓库

**验收：** `git status` 干净；无敏感文件入库

---

# P1 — 无障碍服务 + 单点连点（第 3–6 天）

## P1-01 创建无障碍服务配置文件

**预估：** 1h  
**依赖：** P0-07

- [x] 创建 `res/xml/accessibility_service_config.xml`：
  - `android:accessibilityEventTypes` 最小化（或 `typeAllMask` 若后续需要）
  - `android:canPerformGestures="true"`
  - `android:accessibilityFlags` 不含多余敏感 flag
  - `android:description` 指向 `@string/accessibility_service_description`
- [x] 在 `res/values/strings.xml` 添加服务描述文案（说明用途：辅助点击）

**产出：** `accessibility_service_config.xml`、strings

**验收：** XML 格式正确；描述对用户可读

---

## P1-02 实现 ClickAccessibilityService 骨架

**预估：** 2h  
**依赖：** P1-01

- [x] 创建 `service/ClickAccessibilityService.kt`，继承 `AccessibilityService`
- [x] 实现 `onServiceConnected()`：日志 + 设置单例/伴生对象引用 `instance`
- [x] 实现 `onUnbind()`：清空 `instance`，发送断开事件
- [x] 实现 `onAccessibilityEvent()`：空实现或仅 debug 日志
- [x] 实现 `onInterrupt()`：空实现
- [x] 在 Manifest 注册 service：
  - `android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"`
  - `intent-filter` + `meta-data` 指向 config xml

**产出：** `ClickAccessibilityService.kt`、Manifest service 块

**验收：** 系统设置 → 无障碍 中可看到并开启本服务

---

## P1-03 实现单次点击注入 GestureDispatcher

**预估：** 2h  
**依赖：** P1-02

- [x] 创建 `service/GestureDispatcher.kt`
- [x] 实现 `dispatchTap(x: Float, y: Float, durationMs: Long = 50, callback: (Boolean) -> Unit)`：
  - 构建 `Path` → `StrokeDescription` → `GestureDescription`
  - 调用 `dispatchGesture`，在 `GestureResultCallback.onCompleted/onCancelled` 回调结果
- [x] 服务不可用时返回 false 并打日志
- [x] 在 `ClickAccessibilityService` 中暴露 `fun tap(...)` 委托给 `GestureDispatcher`

**产出：** `GestureDispatcher.kt`

**验收：** 开启无障碍后，调试按钮触发一次点击，目标 App 有响应（如计算器按钮）

---

## P1-04 实现无障碍状态检测工具

**预估：** 1h  
**依赖：** P1-02

- [x] 创建 `util/PermissionChecker.kt`
- [x] 实现 `isAccessibilityServiceEnabled(context): Boolean`
  - 读取 `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES`
  - 匹配本服务完整类名
- [x] 实现 `openAccessibilitySettings(context)` 跳转系统设置

**产出：** `PermissionChecker.kt`

**验收：** 服务开启/关闭时函数返回值正确

---

## P1-05 实现 ClickScheduler 单点循环（核心）

**预估：** 3h  
**依赖：** P1-03, P0-06

- [x] 创建 `service/ClickScheduler.kt`
- [x] 构造函数注入：坐标、intervalMs、repeatCount（-1=无限）、onTick、onComplete、onError
- [x] 使用 `CoroutineScope(SupervisorJob + Dispatchers.Default)` 管理任务
- [x] 实现 `start()`：
  - 循环：调用 tap → 等待 interval → 递减 count
  - 支持 `pause()`：`Job.cancel` 或标志位挂起
  - 支持 `resume()`：从暂停处继续
  - 支持 `stop()`：取消并回调 onComplete
- [x] 实现间隔下限校验：`intervalMs >= 50`（常量 `MIN_INTERVAL_MS`）
- [x] 每次成功/失败更新内部计数器

**产出：** `ClickScheduler.kt`

**验收：** 单元测试：mock tap，验证 10 次循环、暂停/恢复、次数用尽停止

---

## P1-06 实现前台服务 ClickForegroundService

**预估：** 3h  
**依赖：** P1-05, P0-07

- [x] 创建 `service/ClickForegroundService.kt`
- [x] 创建通知渠道 `clicker_channel`（Android O+）
- [x] 实现 `onStartCommand` 解析 Intent extras：x, y, interval, count
- [x] `startForeground` 显示常驻通知（标题：连点运行中；含停止 Action）
- [x] 内部持有 `ClickScheduler`，绑定 `ClickAccessibilityService.tap`
- [x] 实现 `ACTION_STOP`：停止 scheduler + `stopForeground` + `stopSelf`
- [x] Manifest 注册：
  - `foregroundServiceType="specialUse"`
  - `property` 声明 special use 说明（API 34）

**产出：** `ClickForegroundService.kt`、通知布局

**验收：** 启动服务后通知栏可见；划掉应用后连点继续；点通知停止可终止

---

## P1-07 实现 Service 与 UI 的状态通信

**预估：** 2h  
**依赖：** P1-06

- [x] 创建 `service/ClickServiceState.kt` data class：state、currentCount、totalCount、errorMessage
- [x] 在 `ClickForegroundService` 内使用 `MutableStateFlow<ClickServiceState>`
- [x] 创建 `service/ClickServiceBinder` 或使用 `companion object` 暴露 `stateFlow`
- [x] 创建 `domain/usecase/ObserveClickStateUseCase.kt` 封装 Flow 订阅

**产出：** 状态模型 + Flow 暴露

**验收：** Activity 绑定 Service 后可 collect 到 Running → 计数递增 → Stopped

---

## P1-08 首页临时调试 UI（单点连点）

**预估：** 2h  
**依赖：** P1-04, P1-06, P1-07

- [x] 在 `HomeScreen` 添加临时调试区（P3 会重构为正式列表）：
  - `OutlinedTextField`：X、Y 坐标（Float）
  - `OutlinedTextField`：间隔 ms、次数（-1 表示无限）
  - 按钮：「检查无障碍」→ 未开启则跳转设置
  - 按钮：「开始连点」→ `startForegroundService`
  - 按钮：「停止」→ 发 `ACTION_STOP`
- [x] 显示当前状态与已点击次数（collect stateFlow）
- [x] 无障碍未开启时禁用「开始」并显示提示

**产出：** `HomeScreen.kt` 调试 UI

**验收：** 输入坐标 540,1200、间隔 500ms、次数 20，可稳定连点；次数到 0 自动停

---

## P1-09 单点连点集成测试与修复

**预估：** 2h  
**依赖：** P1-08

- [x] 真机测试 100 次连点，记录成功率（待真机验证）
- [x] 修复 `dispatchGesture` 失败重试（最多 2 次，间隔 50ms）
- [x] 修复服务被系统 kill 后的异常处理
- [x] 添加 `Log` 统一 TAG：`Clicker`

**产出：** 稳定可 demo 的单点连点

**验收：** 100 次连点成功率 ≥ 98%；无 ANR / 崩溃

---

# P2 — 悬浮窗控制（第 7–9 天）

## P2-01 悬浮窗权限检测与申请

**预估：** 1.5h  
**依赖：** P0-07

- [ ] 在 `PermissionChecker.kt` 添加：
  - `canDrawOverlays(context): Boolean`（`Settings.canDrawOverlays`）
  - `requestOverlayPermission(activity)` → `ACTION_MANAGE_OVERLAY_PERMISSION`
- [ ] 创建 `util/PermissionState.kt` 数据类汇总三项：无障碍、悬浮窗、通知

**产出：** 悬浮窗权限工具函数

**验收：** Android 11+ 设备可正确检测并跳转 overlay 授权页

---

## P2-02 通知权限申请（API 33+）

**预估：** 1h  
**依赖：** P0-07

- [ ] 在 `PermissionChecker.kt` 添加 `hasNotificationPermission`
- [ ] 创建 `util/NotificationPermissionRequester.kt`，在 Home 首次进入时请求 `POST_NOTIFICATIONS`
- [ ] 拒绝时显示说明：前台服务需要通知

**产出：** 通知权限请求逻辑

**验收：** API 33+ 首次启动弹出权限框；授权后通知正常显示

---

## P2-03 创建 OverlayWindowManager

**预估：** 2h  
**依赖：** P2-01

- [ ] 创建 `ui/overlay/OverlayWindowManager.kt`
- [ ] 使用 `WindowManager` + `TYPE_APPLICATION_OVERLAY` 添加 View
- [ ] 参数：`LayoutParams` 设置 `FLAG_NOT_FOCUSABLE`、初始位置右上角
- [ ] 实现 `show()` / `hide()` / `isShowing()`
- [ ] 处理 `Context` 用 `applicationContext` 避免泄漏

**产出：** `OverlayWindowManager.kt`

**验收：** 调用 show 后任意界面可见悬浮条；hide 后消失

---

## P2-04 实现可拖动悬浮控制条 UI

**预估：** 3h  
**依赖：** P2-03

- [ ] 创建 `ui/overlay/FloatingControlBar.kt`（Compose 用 `ComposeView` 嵌入，或传统 View）
- [ ] UI 元素：
  - 状态指示灯（运行绿 / 暂停黄 / 停止灰）
  - 当前计数 Text：`12 / 100` 或 `12 / ∞`
  - 按钮：▶ 开始、`⏸` 暂停、`⏹` 停止
- [ ] 实现拖动手势：监听 `ACTION_DOWN/MOVE/UP` 更新 `LayoutParams.x/y`
- [ ] 边缘吸附（可选）：松手时贴近左右边缘

**产出：** `FloatingControlBar.kt`

**验收：** 悬浮条可拖动；不遮挡时仍可操作下层 App

---

## P2-05 悬浮窗与 ClickForegroundService 联动

**预估：** 2h  
**依赖：** P2-04, P1-07

- [ ] 定义 Service Intent Actions：`ACTION_START`、`ACTION_PAUSE`、`ACTION_RESUME`、`ACTION_STOP`
- [ ] 扩展 `ClickForegroundService` 处理 pause/resume
- [ ] 悬浮窗按钮发送对应 Action 到 Service
- [ ] Service stateFlow 变化时更新悬浮窗 UI（注册回调或 collect）

**产出：** 双向联动

**验收：** 在游戏界面通过悬浮窗暂停/恢复/停止，行为正确

---

## P2-06 权限引导卡片组件

**预估：** 2h  
**依赖：** P2-01, P2-02, P1-04

- [ ] 创建 `ui/home/PermissionCard.kt` Composable
- [ ] 三行状态：无障碍 ✅/❌、悬浮窗 ✅/❌、通知 ✅/❌
- [ ] 每行未授权时显示「去开启」按钮
- [ ] 在 `HomeScreen` 顶部嵌入 `PermissionCard`
- [ ] `onResume` 时刷新权限状态

**产出：** `PermissionCard.kt`

**验收：** 逐项授权后卡片状态实时更新；全部就绪后显示「权限已就绪」

---

## P2-07 悬浮窗显示/隐藏入口

**预估：** 1h  
**依赖：** P2-03, P2-06

- [ ] 在 `HomeScreen` 添加开关：「显示悬浮控制条」
- [ ] 使用 DataStore 持久化开关状态（`util/PreferencesManager.kt` 占位，P3 完善）
- [ ] 连点开始时自动 show 悬浮窗；停止后可配置是否隐藏

**产出：** 悬浮窗开关

**验收：** 重启 App 后开关状态保持；连点启动后悬浮窗自动出现

---

## P2-08 悬浮窗阶段验收

**预估：** 1h  
**依赖：** P2-05, P2-07

- [ ] 切到第三方 App，悬浮窗仍可见可操作
- [ ] 旋转屏幕后悬浮窗不崩溃（位置可重置）
- [ ] 内存泄漏检查：反复 show/hide 10 次

**产出：** P2 阶段完成

**验收：** 满足 `plan.md` P2 验收标准

---

# P3 — 编辑器 + 脚本存储（第 10–13 天）

## P3-01 配置 Room 数据库

**预估：** 2h  
**依赖：** P0-02, P0-06

- [ ] 创建 `data/local/ClickScriptEntity.kt`（与 plan.md 一致，含 `ClickMode` TypeConverter）
- [ ] 创建 `data/local/ClickPointListConverter.kt`：JSON 序列化 `List<ClickPoint>`
- [ ] 创建 `data/local/ClickScriptDao.kt`：insert、update、delete、getById、getAll、observeAll
- [ ] 创建 `data/local/AppDatabase.kt`，version = 1
- [ ] 在 `di/DatabaseModule.kt` 提供 `@Singleton Database` 与 Dao

**产出：** Room 全套文件

**验收：** 仪器测试：插入/查询/删除脚本成功

---

## P3-02 实现 ScriptRepository

**预估：** 1.5h  
**依赖：** P3-01

- [ ] 创建 `domain/repository/ScriptRepository.kt` 接口
- [ ] 创建 `data/repository/ScriptRepositoryImpl.kt`
- [ ] 实现 Entity ↔ Domain 映射扩展函数
- [ ] 在 `di/RepositoryModule.kt` 绑定实现

**产出：** Repository 层

**验收：** UseCase 可获取 Flow<List<ClickScript>>

---

## P3-03 实现脚本 CRUD UseCase

**预估：** 1.5h  
**依赖：** P3-02

- [ ] `GetAllScriptsUseCase`：返回 `Flow<List<ClickScript>>`
- [ ] `GetScriptByIdUseCase`
- [ ] `SaveScriptUseCase`：新建/更新（根据 id 判断）
- [ ] `DeleteScriptUseCase`
- [ ] `DuplicateScriptUseCase`（可选）：复制脚本

**产出：** `domain/usecase/` 下 4–5 个文件

**验收：** 单元测试：mock Dao，验证保存与删除逻辑

---

## P3-04 实现 HomeViewModel + 脚本列表 UI

**预估：** 3h  
**依赖：** P3-03, P2-06

- [ ] 创建 `ui/home/HomeViewModel.kt`
- [ ] 状态：`scripts: List<ClickScript>`、`permissionState`、`isLoading`
- [ ] 创建 `ui/home/ScriptListItem.kt`：显示名称、模式标签、点数、间隔
- [ ] `HomeScreen` 重构：
  - 顶部 `PermissionCard`
  - `LazyColumn` 脚本列表
  - FAB「新建脚本」→ 导航 `Editor/0`（0 表示新建）
  - 列表项点击 → 编辑；长按 → 删除确认对话框
  - 每项「运行」快捷按钮 → 带脚本参数启动 Service
- [ ] 移除 P1 临时调试区（或移入开发者菜单）

**产出：** 正式首页

**验收：** 可新建、查看、删除脚本；列表空时显示 Empty 状态

---

## P3-05 实现 EditorViewModel

**预估：** 2h  
**依赖：** P3-03, P0-06

- [ ] 创建 `ui/editor/EditorViewModel.kt`
- [ ] 状态字段：
  - name、clickMode、intervalMs、intervalRandom、intervalMaxMs
  - repeatCount、points: List<ClickPoint>
  - isRunning（连点中只读）
  - validationErrors: List<String>
- [ ] 方法：`updateXxx`、`addPoint`、`removePoint`、`movePoint`、`save`、`validate`
- [ ] 校验规则：名称非空、至少 1 个点、间隔 ≥ 50ms、同时模式点数 ≤ 10

**产出：** `EditorViewModel.kt`

**验收：** 单元测试：校验逻辑覆盖边界

---

## P3-06 实现编辑器 — 基础信息区

**预估：** 2h  
**依赖：** P3-05

- [ ] 创建 `ui/editor/EditorScreen.kt`
- [ ] 区块 **基础信息**：
  - 脚本名称输入框
  - 点击模式下拉：顺序 / 同时（`SegmentedButton` 或 `ExposedDropdownMenu`）
  - 重复次数：`TextField` + 「无限」CheckBox（勾选时 count = -1）
- [ ] 连点运行中整个表单 `enabled = false`

**产出：** 编辑器基础区 UI

**验收：** 切换模式后 ViewModel 状态更新

---

## P3-07 实现编辑器 — 间隔设置区

**预估：** 1.5h  
**依赖：** P3-05

- [ ] 区块 **间隔设置**：
  - 固定间隔 `TextField`（ms），旁显示「最小 50ms」
  - 「随机间隔」Switch
  - 开启后显示最大间隔 `TextField`
  - 非法输入（min > max）显示红色错误文案
- [ ] 输入框仅允许数字

**产出：** 间隔设置 UI

**验收：** 随机开关关闭时 `intervalMaxMs` 为 null

---

## P3-08 实现编辑器 — 坐标列表区

**预估：** 2.5h  
**依赖：** P3-05

- [ ] 创建 `ui/editor/PointListSection.kt`
- [ ] `LazyColumn` 展示每个点：序号、X、Y、（顺序模式）delayAfterMs
- [ ] 每项：编辑按钮、删除按钮、拖拽排序手柄（`ReorderableList` 或上下箭头）
- [ ] 底部按钮：「手动添加点」「取点」
- [ ] 同时模式：所有点显示同色圆点标记；顺序模式：序号 1、2、3…

**产出：** 坐标列表 UI

**验收：** 增删改排序均反映到 ViewModel

---

## P3-09 实现编辑器 — 预览示意图

**预估：** 2h  
**依赖：** P3-08

- [ ] 创建 `ui/editor/CoordinatePreview.kt`
- [ ] Canvas 绘制设备屏幕比例矩形（16:9 或读取真实屏幕比例）
- [ ] 在对应比例位置绘制点击点圆点
- [ ] 顺序模式：点间虚线箭头表示顺序
- [ ] 同时模式：同色点 + 「同时」图例
- [ ] 无点时显示占位提示

**产出：** `CoordinatePreview.kt`

**验收：** 修改坐标后预览实时更新

---

## P3-10 实现编辑器 — 保存与运行栏

**预估：** 1h  
**依赖：** P3-05, P3-06

- [ ] 底部固定栏：「保存」「保存并运行」
- [ ] 保存前调用 `validate()`，失败 Snackbar 提示
- [ ] 保存成功 Snackbar + 可选返回首页
- [ ] 「保存并运行」：检查权限 → 保存 → 启动 `ClickForegroundService` 并传脚本 JSON / id

**产出：** 编辑器底栏

**验收：** 新建脚本保存后首页列表出现；保存并运行可立即连点

---

## P3-11 扩展 ClickForegroundService 支持完整脚本

**预估：** 2h  
**依赖：** P3-05, P1-06

- [ ] Intent 改为传递 `scriptId` 或序列化 `ClickScript`
- [ ] `ClickScheduler` 升级：接受 `ClickScript` 而非单点
  - 单点 = 仅 1 个点的顺序模式（兼容 P1）
- [ ] 根据 `clickMode` 分支调度（同时模式逻辑在 P4 完善，此处可先顺序）

**产出：** Service 支持脚本驱动

**验收：** 从编辑器运行多坐标顺序脚本（至少 2 点）正常

---

## P3-12 实现取点 Overlay — Window 层

**预估：** 2h  
**依赖：** P2-03

- [ ] 创建 `ui/overlay/PickPointOverlayManager.kt`
- [ ] 全屏半透明黑色 View（alpha 0.3），`FLAG_NOT_FOCUSABLE` 取消，需接收触摸
- [ ] `onTouch` 获取 `event.x/y`（屏幕绝对坐标）
- [ ] 提供 `show(onPointPicked)` / `dismiss`

**产出：** 取点 Window 管理器

**验收：** 点击屏幕任意位置可拿到坐标回调

---

## P3-13 实现取点 Overlay — UI 与模式分支

**预估：** 3h  
**依赖：** P3-12, P3-05

- [ ] 创建 `ui/overlay/PickPointOverlayContent.kt`
- [ ] 顶部提示条：「顺序模式：点击添加点」/「同时模式：点击选点，完成后点确认」
- [ ] 顺序模式：每次点击添加一点，显示序号标记 View
- [ ] 同时模式：点击添加点（同色），「确认本批」一次性返回 `List<ClickPoint>`
- [ ] 底部：「撤销上一点」「完成」「取消」
- [ ] 从 `EditorScreen`「取点」按钮打开，结果写回 ViewModel

**产出：** 完整取点流程

**验收：** 顺序取 3 点返回列表长度 3；同时模式取 2 点确认后一次返回 2 点

---

## P3-14 实现 DataStore 用户偏好

**预估：** 1.5h  
**依赖：** P2-07

- [ ] 创建 `data/local/PreferencesDataStore.kt`
- [ ] 存储：`showFloatingBar`、`defaultIntervalMs`、`keepScreenOn`
- [ ] 创建 `GetPreferencesUseCase` / `UpdatePreferencesUseCase`
- [ ] 替换 P2-07 中临时 Preferences 实现

**产出：** DataStore 偏好层

**验收：** 偏好读写持久化正确

---

## P3-15 实现设置页基础版

**预估：** 2h  
**依赖：** P3-14

- [ ] 创建 `ui/settings/SettingsViewModel.kt`
- [ ] 创建 `SettingsScreen`：
  - 默认点击间隔
  - 连点后保持悬浮窗显示 Switch
  - 关于：版本号、免责声明（静态文案）
- [ ] 从 Home 导航进入

**产出：** 设置页

**验收：** 修改默认间隔后，新建脚本预填该值

---

## P3-16 P3 阶段验收

**预估：** 1h  
**依赖：** P3-04 ~ P3-15

- [ ] 完整流程：新建 → 取点 → 保存 → 列表运行 → 悬浮窗控制 → 停止 → 再编辑
- [ ] 数据库升级路径留空（version 1 即可）
- [ ] 修复阻塞 bug

**产出：** MVP 完整闭环（单点 + 顺序多点 + 脚本）

**验收：** 满足 `plan.md` P0–P3 全部 MVP 条目

---

# P4 — 多点顺序/同时 + 随机间隔 + 滑动（第 14–17 天）

## P4-01 升级 GestureDispatcher — 多点同时

**预估：** 2h  
**依赖：** P1-03, P0-06

- [ ] 实现 `dispatchSimultaneousTaps(points: List<ClickPoint>, durationMs, callback)`
- [ ] 构建多条 `StrokeDescription`，`startTime` 均为 0
- [ ] 注入前校验 `points.size <= GestureDescription.getMaxStrokeCount()`
- [ ] 超过上限返回错误并不 dispatch

**产出：** 同时点击注入能力

**验收：** 2 点同时点击，目标 App 双指手势有响应（如地图缩放测试）

---

## P4-02 升级 GestureDispatcher — 滑动手势

**预估：** 1.5h  
**依赖：** P1-03

- [ ] 实现 `dispatchSwipe(fromX, fromY, toX, toY, durationMs, callback)`
- [ ] `Path.moveTo` + `lineTo` 构建滑动轨迹
- [ ] 单条 Stroke，`startTime=0`，`durationMs` 可配置（默认 300ms）

**产出：** 滑动注入能力

**验收：** 在桌面滑动翻页成功

---

## P4-03 升级 ClickScheduler — 顺序模式完整逻辑

**预估：** 2h  
**依赖：** P3-11, P1-05

- [ ] 每轮遍历 `points`，逐点 `dispatchTap`
- [ ] 每点点击后 `delay(point.delayAfterMs)`
- [ ] 全部点完成后 `delay(intervalMs)` 再进入下一轮
- [ ] 支持随机间隔：下一轮间隔取 `random(intervalMs..intervalMaxMs)`

**产出：** 顺序调度完整

**验收：** 3 点顺序脚本，每点 delay 不同，时序正确

---

## P4-04 升级 ClickScheduler — 同时模式逻辑

**预估：** 2h  
**依赖：** P4-01, P4-03

- [ ] 每轮调用 `dispatchSimultaneousTaps` 一次
- [ ] 完成后 `delay(intervalMs)` 进入下一轮
- [ ] 忽略 `delayAfterMs`（或 UI 隐藏该字段）
- [ ] 随机间隔同上

**产出：** 同时调度完整

**验收：** 2/3/5 点同时连点 50 轮，无漏点

---

## P4-05 编辑器 — 同时模式 UI 适配

**预估：** 1.5h  
**依赖：** P3-08, P4-04

- [ ] `clickMode == SIMULTANEOUS` 时隐藏 `delayAfterMs` 列
- [ ] 点数超过 10 时保存按钮禁用 + 提示
- [ ] 同时模式建议文案：「建议 2–5 点，点击时长 50–100ms」
- [ ] 预览区图例更新

**产出：** 同时模式编辑器体验

**验收：** 模式切换 UI 字段联动正确

---

## P4-06 实现随机间隔 UI 联动与运行验证

**预估：** 1h  
**依赖：** P3-07, P4-03

- [ ] 运行时在日志输出实际间隔（验证随机性）
- [ ] 单元测试：`RandomIntervalGenerator` 输出在 `[min,max]` 内

**产出：** 随机间隔端到端

**验收：** 设置 500–1500ms 随机，观察 10 次间隔均落在范围内

---

## P4-07 扩展数据模型 — 滑动手势

**预估：** 2h  
**依赖：** P0-06, P3-01

- [ ] 扩展 `ClickPoint` 或新增 `SwipeAction`：type、fromX/Y、toX/Y、durationMs
- [ ] 统一为 `ClickAction` 密封类：`Tap`、`Swipe`
- [ ] 更新 Room Converter 与数据库 version → 2
- [ ] 编写 `Migration(1, 2)`（若旧数据仅 Tap，默认填充）

**产出：** 支持滑动的数据模型 + 迁移

**验收：** 升级后旧脚本仍可加载；新脚本可含滑动

---

## P4-08 升级 ClickScheduler — 混合 Tap + Swipe

**预估：** 2h  
**依赖：** P4-02, P4-07

- [ ] 顺序执行 `List<ClickAction>`：Tap → tap；Swipe → swipe
- [ ] 同时模式仅允许 Tap 点集（UI 限制）；滑动仅顺序模式

**产出：** 混合动作调度

**验收：** 脚本「点击 A → 滑动 B→C → 点击 D」顺序正确

---

## P4-09 编辑器 — 滑动手势配置 UI

**预估：** 3h  
**依赖：** P4-07, P3-12

- [ ] 坐标列表增加类型切换：点击 / 滑动
- [ ] 滑动项：起点、终点坐标；滑动时长 ms
- [ ] 取点模式扩展：「取起点」「取终点」两步
- [ ] 预览区：滑动项绘制箭头线段

**产出：** 滑动编辑 UI

**验收：** 可配置并保存滑动动作；运行生效

---

## P4-10 实现深色模式主题

**预估：** 2h  
**依赖：** P0-05

- [ ] 完善 `ui/theme/Color.kt` 暗色配色
- [ ] `ClickerTheme` 跟随系统 `isSystemInDarkTheme()`
- [ ] 设置页添加主题选项：跟随系统 / 浅色 / 深色（可选，存入 DataStore）
- [ ] 检查悬浮窗、取点 Overlay 在暗色下可读性

**产出：** 深色模式

**验收：** 切换系统深色模式，各页面显示正常

---

## P4-11 dispatchGesture 失败重试与告警

**预估：** 1.5h  
**依赖：** P4-01, P4-04

- [ ] 在 `GestureDispatcher` 统一重试：最多 2 次，间隔 50ms
- [ ] 连续失败 5 次：`ClickScheduler` 暂停并通知 UI（Snackbar / 悬浮窗红点）
- [ ] 状态 Flow 增加 `failureCount` 字段

**产出：** 容错机制

**验收：** 模拟服务断开，UI 有明确错误提示

---

## P4-12 相对坐标存储（防旋转错位）

**预估：** 2h  
**依赖：** P3-05

- [ ] `ClickPoint` 增加 `isRelative: Boolean`；相对时 x/y 为 0.0–1.0
- [ ] 运行时根据当前屏幕宽高转换为绝对像素
- [ ] 取点时默认存相对坐标；预览与运行均转换
- [ ] 设置页选项：「使用相对坐标」（默认开启）

**产出：** 相对坐标支持

**验收：** 竖屏取点、横屏运行，点击位置视觉一致

---

## P4-13 P4 阶段验收

**预估：** 2h  
**依赖：** P4-01 ~ P4-12

- [ ] 顺序 5 点 + 随机间隔 10 分钟稳定性
- [ ] 同时 3 点 10 分钟稳定性
- [ ] 滑动 + 点击混合脚本
- [ ] 主流厂商机各测一轮

**产出：** 增强功能全部完成

**验收：** 满足 `plan.md` 2.2 节全部条目

---

# P5 — 测试与 Polish（第 18–20 天）

## P5-01 ClickScheduler 单元测试套件

**预估：** 3h  
**依赖：** P4-04

- [ ] 测试：单点 N 次停止
- [ ] 测试：顺序多点 + per-point delay
- [ ] 测试：同时多点每轮一次 dispatch
- [ ] 测试：pause / resume 计数连续
- [ ] 测试：随机间隔边界
- [ ] 测试：interval < 50 拒绝启动

**产出：** `ClickSchedulerTest.kt`

**验收：** `./gradlew test` 全部通过

---

## P5-02 Repository / UseCase 单元测试

**预估：** 2h  
**依赖：** P3-03

- [ ] `ScriptRepositoryTest`：CRUD
- [ ] `SaveScriptUseCaseTest`：校验失败不写入
- [ ] Mock Room Dao

**产出：** 数据层测试

**验收：** 测试通过

---

## P5-03 仪器测试 — Room 与导航

**预估：** 2h  
**依赖：** P3-01, P0-05

- [ ] `ClickScriptDaoTest`：插入查询
- [ ] `NavigationTest`：Home → Editor → Back
- [ ] Migration 1→2 测试（若 P4 升级了 DB）

**产出：** `androidTest/` 测试文件

**验收：** 连接设备/模拟器 `connectedAndroidTest` 通过

---

## P5-04 UI Polish — 首页与列表

**预估：** 2h  
**依赖：** P3-04

- [ ] 空状态插图 + 引导文案
- [ ] 列表项滑动删除（`SwipeToDismiss`）
- [ ] 加载骨架屏 / 下拉刷新（可选）
- [ ] 脚本重命名快捷入口

**产出：** 首页体验优化

**验收：** 视觉与交互无明显粗糙感

---

## P5-05 UI Polish — 编辑器

**预估：** 2h  
**依赖：** P3-10

- [ ] 未保存离开确认对话框
- [ ] 字段键盘类型优化（数字键盘）
- [ ] 错误提示靠近对应字段
- [ ] 大屏 / 平板基础适配（单列 → 双列预览）

**产出：** 编辑器体验优化

**验收：** 无误触、无误删

---

## P5-06 无障碍服务断开处理

**预估：** 1.5h  
**依赖：** P1-02

- [ ] `ClickAccessibilityService.onUnbind` 发送广播或 Flow 事件
- [ ] `ClickForegroundService` 收到后自动停止连点
- [ ] 首页 / 悬浮窗显示：「无障碍已关闭，请重新授权」

**产出：** 断开兜底

**验收：** 运行中手动关闭无障碍，App 停止连点并提示

---

## P5-07 厂商 ROM 后台限制引导

**预估：** 2h  
**依赖：** P1-06

- [ ] 创建 `ui/settings/BatteryOptimizationGuide.kt` 说明页
- [ ] 检测设备厂商，显示对应「自启动 / 电池优化白名单」引导（小米、华为、OPPO、vivo、原生）
- [ ] 提供跳转 `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`（可选）

**产出：** 后台保活引导

**验收：** 小米/华为设备上按引导设置后，后台连点 30 分钟不中断

---

## P5-08 性能与内存检查

**预估：** 2h  
**依赖：** P4-13

- [ ] Android Studio Profiler：连点 10 分钟无内存持续增长
- [ ] 修复 `OverlayWindowManager` / Service 泄漏
- [ ] 确保主线程无阻塞

**产出：** 性能报告（可写在 `docs/perf-notes.md`）

**验收：** 无 OOM；内存曲线平稳

---

## P5-09 多 API 级别回归

**预估：** 3h  
**依赖：** P4-13

- [ ] API 24 模拟器/真机：基础连点
- [ ] API 30：悬浮窗
- [ ] API 33：通知权限
- [ ] API 34：special use 前台服务
- [ ] 记录各版本差异到 `docs/compatibility.md`

**产出：** 兼容性文档

**验收：** 无阻塞级 bug

---

## P5-10 发布构建配置

**预估：** 2h  
**依赖：** P5-09

- [ ] 配置 `release`：`minifyEnabled true`，ProGuard rules for Hilt/Room
- [ ] 替换 debug 应用名为「连点器」
- [ ] 配置 `versionCode` / `versionName`
- [ ] 生成签名配置说明（`keystore` 不入库）
- [ ] `./gradlew assembleRelease` 产出 APK

**产出：** 可安装的 release APK

**验收：** release 包功能与 debug 一致；体积合理（< 15MB）

---

# P6 — 进阶功能（可选，按优先级排期）

> 以下每项可独立成 sprint，依赖 P5 完成。

## P6-01 录制回放 — 事件捕获

**预估：** 1 天  
**依赖：** P5

- [ ] 创建 `service/GestureRecorder.kt`
- [ ] 无障碍服务监听 `TYPE_VIEW_CLICKED` 或 overlay 层记录触摸（二选一方案）
- [ ] 记录序列为 `List<ClickAction>` + 时间戳
- [ ] 录制开始/结束 UI 入口

**验收：** 手动点 5 下，停止录制，生成 5 点脚本

---

## P6-02 录制回放 — 回放与保存

**预估：** 0.5 天  
**依赖：** P6-01

- [ ] 将录制结果填入 EditorViewModel
- [ ] 支持保存为脚本
- [ ] 回放时按录制时间间隔或统一间隔可选

**验收：** 录制 → 保存 → 运行，行为与录制一致（允许小偏差）

---

## P6-03 条件触发 — 节点查找

**预估：** 1.5 天  
**依赖：** P1-02

- [ ] 扩展无障碍 config：开启 `canRetrieveWindowContent`
- [ ] 创建 `service/NodeMatcher.kt`：按 text / viewId 查找节点
- [ ] 获取节点屏幕 bounds 中心点作为点击坐标

**验收：** 目标 App 某按钮出现文字「确定」时自动点击

---

## P6-04 条件触发 — 规则配置 UI

**预估：** 1 天  
**依赖：** P6-03

- [ ] 编辑器增加「条件触发」Tab：匹配文本、匹配 ViewId、超时
- [ ] 数据模型扩展 `TriggerRule`
- [ ] Room migration v3

**验收：** 配置规则后，连点等待条件满足再执行

---

## P6-05 导入 / 导出 JSON

**预估：** 1 天  
**依赖：** P3-02

- [ ] 定义 `script_schema.json` 格式（版本号、字段）
- [ ] 实现 `ExportScriptUseCase`：分享 Intent / 写文件
- [ ] 实现 `ImportScriptUseCase`：读文件 → 校验 → 入库
- [ ] 设置页或列表页「导入」「导出」入口

**验收：** 导出文件在另一设备导入后可运行

---

## P6-06 多配置文件快捷切换

**预估：** 0.5 天  
**依赖：** P3-04

- [ ] 首页列表项「设为默认」
- [ ] DataStore 存 `lastUsedScriptId`
- [ ] 悬浮窗增加脚本切换下拉（最近 3 个）

**验收：** 悬浮窗可切换脚本并立即运行

---

## P6-07 运行统计

**预估：** 1 天  
**依赖：** P1-07

- [ ] 创建 `data/local/RunHistoryEntity`：scriptId、startTime、endTime、clickCount
- [ ] 每次停止写入记录
- [ ] 设置页或独立「统计」页：今日点击次数、累计时长

**验收：** 运行后统计数据正确累加

---

# 附录 A — 关键文件清单

| 文件路径 | 阶段 | 说明 |
|----------|------|------|
| `ClickerApplication.kt` | P0 | Hilt 入口 |
| `ClickAccessibilityService.kt` | P1 | 无障碍服务 |
| `GestureDispatcher.kt` | P1/P4 | 手势注入 |
| `ClickScheduler.kt` | P1/P4 | 调度核心 |
| `ClickForegroundService.kt` | P1/P3 | 前台服务 |
| `OverlayWindowManager.kt` | P2 | 悬浮窗 |
| `FloatingControlBar.kt` | P2 | 悬浮控制条 |
| `AppDatabase.kt` | P3 | Room |
| `EditorScreen.kt` | P3/P4 | 脚本编辑器 |
| `PickPointOverlayManager.kt` | P3 | 取点 |
| `PermissionChecker.kt` | P1/P2 | 权限工具 |

---

# 附录 B — 每日建议排期（参考）

| 天 | 任务编号 | 当日目标 |
|----|----------|----------|
| D1 | P0-01 ~ P0-05 | 工程可运行 + 导航骨架 |
| D2 | P0-06 ~ P0-08 | 模型 + Git |
| D3 | P1-01 ~ P1-04 | 无障碍服务可开启 |
| D4 | P1-05 ~ P1-06 | 调度器 + 前台服务 |
| D5 | P1-07 ~ P1-09 | 单点连点可 demo |
| D6 | P2-01 ~ P2-04 | 悬浮窗显示 |
| D7 | P2-05 ~ P2-08 | 悬浮窗完整联动 |
| D8 | P3-01 ~ P3-04 | Room + 首页列表 |
| D9 | P3-05 ~ P3-08 | 编辑器主体 |
| D10 | P3-09 ~ P3-11 | 预览 + Service 脚本 |
| D11 | P3-12 ~ P3-16 | 取点 + 设置 + MVP 验收 |
| D12 | P4-01 ~ P4-04 | 同时点击 + 调度 |
| D13 | P4-05 ~ P4-09 | 滑动 + UI 适配 |
| D14 | P4-10 ~ P4-13 | 主题 + 坐标 + P4 验收 |
| D15 | P5-01 ~ P5-03 | 自动化测试 |
| D16 | P5-04 ~ P5-07 | UI polish + 容错 |
| D17 | P5-08 ~ P5-10 | 性能 + 发布包 |

---

# 附录 C — 完成定义（Definition of Done）

每个任务完成需满足：

1. **代码**已合入主干分支，无编译 error
2. **验收**条目已全部手动验证
3. 新增逻辑有对应 **单元测试**（P5 阶段补齐的可标记技术债）
4. 无已知 **崩溃 / ANR**
5. 权限相关改动已更新 **Manifest** 与用户可见说明文案

---

*文档版本：1.0 | 对应 plan.md 全部功能项 | 最后更新：与 plan.md 同步*
