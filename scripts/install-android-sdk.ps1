# Android SDK 安装脚本（Windows）
# 用法：在 PowerShell 中执行  .\scripts\install-android-sdk.ps1

$ErrorActionPreference = "Stop"

$SdkRoot = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { "$env:LOCALAPPDATA\Android\Sdk" }
$JavaHome = if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-21" }
$ProjectRoot = Split-Path -Parent $PSScriptRoot

Write-Host "SDK 目录: $SdkRoot"
Write-Host "JDK 目录: $JavaHome"

if (-not (Test-Path $JavaHome)) {
    Write-Error "未找到 JDK，请安装 JDK 21 或设置 JAVA_HOME"
}

$env:JAVA_HOME = $JavaHome
$env:ANDROID_HOME = $SdkRoot
$env:ANDROID_SDK_ROOT = $SdkRoot
$env:Path = "$JavaHome\bin;$SdkRoot\platform-tools;$env:Path"

New-Item -ItemType Directory -Force -Path $SdkRoot | Out-Null

# 1. 安装 cmdline-tools（腾讯云镜像，国内可访问）
$CmdlineZip = Join-Path $env:TEMP "cmdline-tools.zip"
$CmdlineUrl = "https://mirrors.cloud.tencent.com/AndroidSDK/commandlinetools-win-11076708_latest.zip"
Write-Host "下载 cmdline-tools ..."
curl.exe -L --retry 3 -o $CmdlineZip $CmdlineUrl
if (-not (Test-Path $CmdlineZip) -or (Get-Item $CmdlineZip).Length -lt 1000000) {
    Write-Error "cmdline-tools 下载失败"
}

$TmpDir = Join-Path $SdkRoot "cmdline-tools\_tmp"
Remove-Item $TmpDir -Recurse -Force -ErrorAction SilentlyContinue
Expand-Archive -Path $CmdlineZip -DestinationPath $TmpDir -Force
$LatestDir = Join-Path $SdkRoot "cmdline-tools\latest"
New-Item -ItemType Directory -Force -Path $LatestDir | Out-Null
$Inner = Join-Path $TmpDir "cmdline-tools"
if (Test-Path $Inner) {
    Copy-Item -Path "$Inner\*" -Destination $LatestDir -Recurse -Force
} else {
    Copy-Item -Path "$TmpDir\*" -Destination $LatestDir -Recurse -Force
}
Remove-Item $TmpDir -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item $CmdlineZip -Force -ErrorAction SilentlyContinue

$SdkManager = Join-Path $LatestDir "bin\sdkmanager.bat"
if (-not (Test-Path $SdkManager)) {
    Write-Error "sdkmanager 未找到: $SdkManager"
}

# 2. 接受许可
Write-Host "接受 SDK 许可 ..."
1..30 | ForEach-Object { "y" } | & $SdkManager --sdk_root=$SdkRoot --licenses | Out-Null

# 3. 安装项目所需组件（需能访问 Google 源；若失败请用 Android Studio SDK Manager）
$Packages = @(
    "platform-tools",
    "platforms;android-34",
    "platforms;android-36",
    "build-tools;34.0.0",
    "build-tools;36.1.0"
)
Write-Host "安装 SDK 组件: $($Packages -join ', ')"
& $SdkManager --sdk_root=$SdkRoot @Packages
if ($LASTEXITCODE -ne 0) {
    Write-Warning "sdkmanager 在线安装失败（可能无法访问 Google 源）。"
    Write-Warning "请打开 Android Studio -> Settings -> Languages & Frameworks -> Android SDK 手动勾选 API 34 / 36 后 Apply。"
}

# 4. 若仅有 android-36.1，为 Gradle 兼容生成 android-36 平台目录
$Platform361 = Join-Path $SdkRoot "platforms\android-36.1"
$Platform36 = Join-Path $SdkRoot "platforms\android-36"
if ((Test-Path $Platform361) -and -not (Test-Path $Platform36)) {
    Write-Host "从 android-36.1 生成 android-36 平台目录 ..."
    Copy-Item -Path $Platform361 -Destination $Platform36 -Recurse -Force
    (Get-Content "$Platform36\source.properties") -replace 'ApiLevel=36.1', 'ApiLevel=36' | Set-Content "$Platform36\source.properties"
    (Get-Content "$Platform36\package.xml") `
        -replace 'platforms;android-36.1', 'platforms;android-36' `
        -replace '<api-level>36.1</api-level>', '<api-level>36</api-level>' `
        -replace 'Android SDK Platform 36.1', 'Android SDK Platform 36' |
        Set-Content "$Platform36\package.xml"
}

# 5. 写入 local.properties
$LocalProps = Join-Path $ProjectRoot "local.properties"
$EscapedSdk = $SdkRoot -replace "\\", "\\\\"
@"
## 本文件由 install-android-sdk.ps1 生成，请勿提交 Git
sdk.dir=$EscapedSdk
"@ | Set-Content -Path $LocalProps -Encoding UTF8

Write-Host ""
Write-Host "完成。已写入 $LocalProps"
Write-Host "验证: cd $ProjectRoot; .\gradlew.bat assembleDebug"
