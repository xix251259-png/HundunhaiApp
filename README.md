# 混沌海·人道祖庭设定集 Android APP

## 项目简介

这是一个基于 Android WebView 架构的世界观设定集 APP，将《混沌海·人道祖庭》的完整设定内容封装为可安装的 Android 应用程序。

## 技术架构

- **核心架构**：原生 Android + WebView 混合架构
- **内容载体**：单文件 HTML（所有图片、样式、脚本全部内嵌）
- **最低支持版本**：Android 5.0 (API 21)
- **目标版本**：Android 13 (API 33)
- **编程语言**：Java
- **构建工具**：Gradle 7.4 + Android Gradle Plugin 7.2.0

## 功能特性

### ✅ 已实现功能

1. **完整内容展示**
   - 26卷正文 + 4篇附卷 + 后记完整内容
   - 9张精美插图（水墨国风风格）
   - 支持滚动浏览、缩放查看

2. **多语言切换**
   - 简体中文
   - English
   - 设置中切换，即时生效

3. **主题切换**
   - 默认朱红主题（水墨国风）
   - 夜间模式
   - 蓝色主题
   - 绿色主题
   - 金色主题

4. **内容管理（后台修改）**
   - 📁 从本地HTML文件导入
   - 🌐 从网络URL下载更新
   - 🔄 重置为内置默认内容
   - 支持随时更改、修订、添加内容

5. **离线使用**
   - 基础内容完全内置，无需网络
   - 自定义内容保存在本地存储
   - 无网络环境下可正常使用

6. **交互功能**
   - 底部导航栏（首页/目录/搜索/设置）
   - 全文搜索功能
   - 字体大小调节
   - 图片查看器
   - 词条跳转链接

## 项目结构

```
HundunhaiApp/
├── app/
│   ├── build.gradle              # 应用模块构建配置
│   ├── proguard-rules.pro        # 代码混淆规则
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml    # 应用清单
│           ├── assets/
│           │   └── index.html         # 内置HTML内容（约12MB）
│           ├── java/com/hundunhai/setting/
│           │   ├── MainActivity.java       # 主界面
│           │   ├── SettingsActivity.java   # 设置界面
│           │   └── ContentManagerActivity.java  # 内容管理
│           └── res/
│               ├── drawable/          # 图标资源
│               ├── layout/            # 布局文件
│               ├── menu/              # 菜单资源
│               ├── mipmap-*/          # 启动图标
│               ├── values/            # 中文资源
│               ├── values-en/         # 英文资源
│               └── xml/               # 配置文件
├── build.gradle                  # 项目根构建配置
├── settings.gradle               # 项目设置
├── gradle.properties             # Gradle属性
└── gradle/wrapper/
    └── gradle-wrapper.properties  # Gradle包装配置
```

## 如何使用

### 方法一：使用 Android Studio 构建

1. **下载项目**
   - 将整个 `HundunhaiApp` 文件夹下载到本地

2. **安装 Android Studio**
   - 下载地址：https://developer.android.com/studio
   - 安装时确保安装了 Android SDK

3. **打开项目**
   - 启动 Android Studio
   - 选择 "Open an Existing Project"
   - 选择 `HundunhaiApp` 文件夹
   - 等待 Gradle 同步完成（首次可能需要下载依赖）

4. **生成 Gradle Wrapper**
   - 打开终端，进入项目目录
   - 执行：`gradle wrapper --gradle-version 7.4`
   - （如果没有安装gradle，Android Studio会自动处理）

5. **构建 APK**
   - 菜单：Build → Build Bundle(s) / APK(s) → Build APK(s)
   - 构建完成后点击 "locate" 查看APK文件
   - APK位置：`app/build/outputs/apk/debug/app-debug.apk`

6. **安装到手机**
   - 将APK文件传输到Android手机
   - 在手机上安装APK（需要开启"未知来源"安装权限）

### 方法二：命令行构建

```bash
# 进入项目目录
cd HundunhaiApp

# 生成gradle wrapper（如需要）
gradle wrapper

# 构建debug版本
./gradlew assembleDebug

# 构建release版本（需要签名）
./gradlew assembleRelease
```

## 内容更新指南

### 方式一：从本地文件导入

1. 准备好更新后的 HTML 文件（单文件，所有资源内嵌）
2. 在 APP 中进入：设置 → 内容管理
3. 点击「选择HTML文件」
4. 选择准备好的 HTML 文件
5. 内容自动更新，返回主界面即可查看

### 方式二：从网络URL更新

1. 将更新后的 HTML 文件上传到可访问的网络地址
2. 在 APP 中进入：设置 → 内容管理
3. 在输入框中填写更新地址URL
4. 点击「从URL更新」
5. 等待下载完成，内容自动更新

### 方式三：重置为默认内容

1. 在 APP 中进入：设置 → 内容管理
2. 点击「重置为默认内容」
3. 恢复为 APP 内置的原始内容

## HTML内容制作规范

如需制作自定义内容，请遵循以下规范：

1. **单文件格式**：所有图片、CSS、JS必须内嵌到一个HTML文件中
2. **图片格式**：使用 Base64 编码内嵌图片
3. **字符编码**：UTF-8
4. **推荐尺寸**：适配移动端，宽度 100%
5. **交互脚本**：可自定义 JavaScript 实现交互功能

## 开发说明

### 修改包名

修改以下文件中的包名：
- `app/build.gradle` 中的 `applicationId`
- `AndroidManifest.xml` 中的 `package`
- Java 文件的包声明和目录结构

### 修改应用名称

- 中文：`res/values/strings.xml` 中的 `app_name`
- 英文：`res/values-en/strings.xml` 中的 `app_name`

### 修改默认内容

替换 `app/src/main/assets/index.html` 文件即可。

### 添加更多主题

1. 在 `res/values/colors.xml` 中添加颜色
2. 在 `res/values/themes.xml` 中添加主题样式
3. 在 `SettingsActivity.java` 中添加主题切换逻辑

## 注意事项

1. **首次构建**：首次打开项目需要下载依赖，可能需要几分钟
2. **网络权限**：APP需要网络权限以支持从URL更新内容
3. **存储权限**：Android 6.0以上需要动态申请存储权限
4. **内容大小**：内置内容约12MB，安装后占用空间约15-20MB
5. **离线使用**：基础功能完全离线，仅更新内容时需要网络

## 常见问题

**Q: 为什么构建失败？**
A: 请检查：
- Android SDK 是否正确安装
- Gradle 版本是否匹配
- 网络连接是否正常（需要下载依赖）

**Q: 安装后打不开？**
A: 请检查：
- 手机系统版本是否在 Android 5.0 以上
- 是否开启了未知来源安装权限
- APK 文件是否完整

**Q: 内容更新后不生效？**
A: 请尝试：
- 完全退出APP后重新打开
- 检查HTML文件格式是否正确
- 确认文件编码为UTF-8

## 版本信息

- 当前版本：1.0.0
- 构建日期：2026-06-19
- 内容版本：混沌海人道祖庭设定总纲 完整版

## 许可证

本项目仅供学习和个人使用。
