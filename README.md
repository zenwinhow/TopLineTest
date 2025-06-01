# 头条资讯 (TopLineTest)

## 目录
- [项目简介](#项目简介)
- [主要功能](#主要功能)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [如何运行](#如何运行)
- [已知问题与展望](#已知问题与展望)
- [截图预览 (建议添加)](#截图预览-建议添加)

## 项目简介
“头条资讯 (TopLineTest)” 是一款独立开发的 Android 新闻资讯类应用。本项目旨在提供一个集新闻浏览、视频观看、用户管理以及数据统计于一体的移动端内容平台。应用采用 MVC 架构模式进行开发，涵盖了从需求分析、UI 设计、功能实现到测试的完整流程。

## 主要功能

本项目实现了以下核心功能：

* **新闻模块**
    * **多样化资讯展示**：支持多种新闻列表项布局（如单图、多图模式）。
    * **新闻详情阅读**：展示新闻详细内容，支持图文混排。
    * **交互优化**：集成自定义下拉刷新 (`PullToRefresh`) 和上拉加载更多功能。
    * **广告轮播**：在新闻列表顶部集成广告图片轮播功能。

* **视频模块**
    * **视频列表与播放**：展示视频列表，并支持在线播放。
    * **手势控制播放器**：在视频播放界面，支持通过手势调节音量、屏幕亮度和播放进度，同时支持全屏切换。

* **用户中心模块**
    * **账户管理**：实现用户注册（密码采用MD5加密）、登录功能。
    * **个人信息**：支持用户查看和修改个人资料（如昵称、签名、头像更换）。
    * **密码安全**：提供密码修改和基于安全问题的密码找回功能。
    * **内容收藏**：用户可以收藏喜欢的新闻或视频，方便后续查看。
    * **数据持久化**：用户信息、收藏等数据通过 SQLite 数据库进行本地存储。

* **数据统计模块**
    * **图表展示**：使用 HelloCharts 图表库，可视化展示 Android、Java、Python 等课程的学习人数统计数据。

* **UI与交互特性**
    * **主界面导航**：采用底部 `RadioGroup` + `Fragment` 配合 `ViewPager` 实现滑动切换的主流导航方式。
    * **动态菜单**：集成了 BoomMenu 库，提供酷炫的弹出式菜单效果。
    * **自定义视图**：包含如自定义圆角图片 (`ImageViewRoundOval`) 等视图组件，提升界面美观度。

## 技术栈

* **编程语言**: Java
* **开发平台**: Android SDK
* **架构模式**: MVC (Model-View-Controller)
* **网络请求**: OkHttp
* **JSON 解析**: Gson
* **图片加载**: Glide
* **本地数据存储**: SQLite, SharedPreferences
* **UI 组件与库**:
    * Android Support Library (如 RecyclerView, ViewPager, Fragment)
    * [HelloCharts](https://github.com/lecho/hellocharts-android) (图表库)
    * [BoomMenu](https://github.com/Nightonke/BoomMenu) (弹出式菜单库)
    * 自定义 `PullToRefresh` (下拉刷新组件)
* **多媒体**: Android MediaPlayer, VideoView (用于视频播放)

## 项目结构

```
TopLineTest/
├── app/                                # 主应用模块
│   ├── src/main/java/com/zhengwenhao/topline104022021037/
│   │   ├── activity/                   # 各个界面的 Activity
│   │   ├── adapter/                    # RecyclerView 和 ViewPager 等的 Adapter
│   │   ├── bean/                       # 数据模型 (JavaBean)
│   │   ├── fragment/                   # 各个模块的 Fragment
│   │   ├── sqlite/                     # SQLite 数据库操作相关
│   │   ├── utils/                      # 工具类 (网络、JSON解析、MD5加密等)
│   │   └── view/                       # 自定义 View 组件
│   └── src/main/res/                   # 资源文件 (布局、图片、字符串等)
├── PullToRefresh/                      # 自定义的下拉刷新库模块
├── boommenu/                           # BoomMenu 第三方库模块
├── hellocharts-library/                # HelloCharts 第三方库模块
└── README.md                           # 项目说明文件
```


## 如何运行

1.  **环境准备**:
    * 确保已安装 Android Studio。
    * 配置好 Android SDK 环境。
2.  **导入项目**:
    * 在 Android Studio 中选择 "Open an existing Android Studio project"。
    * 浏览到项目根目录 `TopLineTest` 并打开。
3.  **构建与运行**:
    * 等待 Android Studio 完成项目同步和构建。
    * 连接 Android 设备或启动模拟器。
    * 点击 Android Studio 工具栏中的 "Run 'app'" 按钮。

## 已知问题与展望
* 可以进一步优化 UI/UX 细节。
* 考虑引入更现代的 Android 开发技术栈，如 Kotlin、MVVM 架构、Jetpack 组件等。

---
