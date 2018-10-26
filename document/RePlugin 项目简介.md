# 关于 RePlugin

`RePlugin` 是360开源的一款 `Android` 平台全面插件化框架。具体介绍请参考 [RePlugin](https://github.com/Qihoo360/RePlugin/blob/dev/README_CN.md)。

 [RePluginForLearning](https://github.com/fiissh/RePluginForLearning) 是基于 [RePlugin V2.3.0](https://github.com/Qihoo360/RePlugin/releases/tag/v2.3.0) 版本进行的完整部署，改变了原有的项目结构以及部分配置，主要变动如下：

* 为方便测试，改变原有项目结构，将 `replugin-host-library`、`replugin-plugin-library`、`replugin-host-gradle` 和 `replugin-plugin-gradle` 四个核心项目部署在同一个 `Project` 下；
* 增加演示用的 `Sample` 项目，其中包含一个 `Host` 项目 `app`，两个 `Plugin` 项目 `replugin-plugin-sample-a` 和 `replugin-plugin-sample-b`；
* 改变 `replugin-host-gradle` 和 `replugin-plugin-gradle` 两个 `Gradle 插件` 项目的发布行为，把插件发布到 `jcenter` 修改为发布到 `outputs/gradle/` 目录下，并将 `outputs/gradle/repos/` 目录添加到 `Maven` 索引地址中
* 其他代码逻辑的更改请参考具体的 `commit` 记录

# RePlugin 项目结构

`RePlugin` 的核心项目分为四个 `Module`，其中两个为 `Android Library` 项目，两个为 `Gradle 插件`项目：

* replugin-host-gradle： `Gradle 插件`项目，对应 `com.qihoo360.replugin:replugin-host-gradle:version` 的依赖，由 `Host` 负责在根 `build.gradle` 中引入，主要作用是负责在 `Host` 项目的编译期生产各类文件：
    * 根据用户配置，生成 `HostBuildConfig` 类，方便 `RePlugin 框架`读取并自定义其属性，如 进程数量、各种类型的坑位的数量、是否使用 `AppCompat` 依赖库、`plugin-builtin.json` 文件等
    * 自动生成带有 `RePlugin` 插件坑位的 `AndroidManifest.xml` 文件。
* replugin-host-library： `Android Library` 项目，对应 `com.qihoo360.replugin:replugin-host-lib:version` 的依赖，由 `Host` 项目在 `Module` 的 `build.gradle` 中引入，主要负责 `RePlugin` 框架的初始化、类加载、启动以及管理 `Plugin` 等；
* replugin-plugin-gradle： `Gradle 插件`项目，对应 `com.qihoo360.replugin:replugin-plugin-gradle:version` 的依赖，由 `Plugin` 负责在根 `build.gradle` 中引入，主要负责在 `Plugin` 的编译期配置 `Plugin` 打包的相关信息以及动态替换 `Plugin` 项目中的继承结构，例如修改 `Activity` 的继承和 `Provider` 的重定向等；
* replugin-plugin-library： `Android Library` 项目，对应 `com.qihoo360.replugin:replugin-plugin-lib:version` 的依赖，由 `Plugin` 项目在 `Module` 的 `build.gradle` 中引入，主要功能是通过 `Java 反射`技术来调用 `Host` 程序中 `replugin-host-library` 的相关接口，并提供 `双向通信` 的功能。

另外三个 `Module` 为 `RePlugin` 的演示 `Sample`，主要用于对 `RePlugin` 的接口进行调用演示。

`document` 和 `outputs` 目录则主要用于存放文档以及相关的产出。

# AndroidStudio 中部署 `RePlugin`

为了方便测试以及学习，肥肥基于 [RePlugin V2.3.0](https://github.com/Qihoo360/RePlugin) 重新部署了 [RePluginForLearning](https://github.com/fiissh/RePluginForLearning)，除非特殊说明，后续文档中所有提到 `RePlugin` 的地方都将默认为 `RePluginForLearning`。

使用 `AndroidStudio` 
 