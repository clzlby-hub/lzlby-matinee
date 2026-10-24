# Matinee TV 极简电视桌面 (lzlby-matinee)

基于 [Jarvis322/matinee](https://github.com/Jarvis322/matinee) 的深度定制优化版，专为智能电视与电视盒子打造的现代化、极简、无广告 Android TV 桌面启动器。

<p align="center">
  <img src="play/assets/feature-graphic-1024x500.png" alt="Matinee TV Launcher" width="820">
</p>

---

## ✨ 定制优化亮点

1. **🚀 极致瘦身（从 21.7MB 骤降至 2.0MB，精简 90%+）**
   - 彻底剥离了原版 v1.5.4/v1.5.5 中臃肿的 C++ UxPlay、libcrypto.a（单 ABI 即 6~8MB）、libplist 及 ALAC 跨架构编译代码。
   - 回归纯 Kotlin + Jetpack Compose 原生现代化轻量架构，启动极速、零发热、内存占用极低。

2. **🖼 必应每日 4K 高清壁纸自动同步**
   - 新增 `BingWallpaperClient`，全自动获取微软官方每日 4K UHD 极清壁纸。
   - 本地智能缓存与离线保护：无网络时自动使用本地已缓存壁纸；后台定时静默检查轮换。
   - 磨砂毛玻璃折射（Frosted Glass）双层渲染，壁纸绚丽且不影响文字与卡片可读性。

3. **🌊 Apple TV 风格流动光效**
   - 内置流体动态光效（极光、黄昏、海洋、沙丘等），开启后背景光影缓缓流淌，质感拉满。

4. **🇨🇳 多语言深度精简**
   - 移除德语、西语、法语、日语、土耳其语等多余语言资源，仅保留**简体中文**与**英文**。

5. **📡 联动独立硬件级 AirPlay 投屏服务**
   - 状态栏与接收器监听器已无缝关联自建的高性能硬件解码专用 AirPlay 服务（`io.github.jqssun.airplay`），状态检测实时准确。

---

## 📱 核心功能特性

- **应用自由排列**：长按遥控器 OK 键可拖拽排序、自定义图标或放入文件夹。
- **双布局模式**：支持单排流光（Row）与经典栅格（Grid）双布局切换。
- **HDMI 与直播信号源**：支持电视天线直播、HDMI-CEC 设备状态检测与一键切换。
- **轻量状态栏**：时间、天气、网络名称与内存清理指示。
- **纯净无扰**：无广告、无推广、无数据收集。

---

## 🛠 编译与构建

```bash
# 构建 Release APK
./gradlew assembleRelease

# 安装到电视
adb connect <电视IP>:<ADB端口>
adb install -r app/build/outputs/apk/release/app-release.apk
```

---

## 📄 许可证

基于 [Apache License 2.0](LICENSE) 开源。
