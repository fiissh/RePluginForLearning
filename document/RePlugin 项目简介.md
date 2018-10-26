## 关于 RePlugin

`RePlugin` 是360开源的一款 `Android` 平台全面插件化框架。具体介绍请参考 [RePlugin](https://github.com/Qihoo360/RePlugin/blob/dev/README_CN.md)。

 [RePluginForLearning](https://github.com/fiissh/RePluginForLearning) 是基于 [RePlugin V2.3.0](https://github.com/Qihoo360/RePlugin/releases/tag/v2.3.0) 版本进行的完整部署，改变了原有的项目结构以及部分配置，主要变动如下：

* 为方便测试，改变原有项目结构，将 `replugin-host-library`、`replugin-plugin-library`、`replugin-host-gradle` 和 `replugin-plugin-gradle` 四个核心项目部署在同一个 `Project` 下；
* 增加演示用的 `Sample` 项目，其中包含一个 `Host` 项目 `app`，两个 `Plugin` 项目 `replugin-plugin-sample-a` 和 `replugin-plugin-sample-b`；
* 改变 `replugin-host-gradle` 和 `replugin-plugin-gradle` 两个 `Gradle 插件` 项目的发布行为，把插件发布到 `jcenter` 修改为发布到 `outputs/gradle/` 目录下，并将 `outputs/gradle/repos/` 目录添加到 `Maven` 索引地址中
* 其他代码逻辑的更改请参考具体的 `commit` 记录

## RePlugin 项目结构

`RePlugin` 的核心项目分为四个 `Module`，其中两个为 `Android Library` 项目，两个为 `Gradle 插件`项目：

* replugin-host-gradle： `Gradle 插件`项目，对应 `com.qihoo360.replugin:replugin-host-gradle:version` 的依赖，由 `Host` 负责在根 `build.gradle` 中引入，主要作用是负责在 `Host` 项目的编译期生产各类文件：
    * 根据用户配置，生成 `HostBuildConfig` 类，方便 `RePlugin 框架`读取并自定义其属性，如 进程数量、各种类型的坑位的数量、是否使用 `AppCompat` 依赖库、`plugin-builtin.json` 文件等
    * 自动生成带有 `RePlugin` 插件坑位的 `AndroidManifest.xml` 文件。
* replugin-host-library： `Android Library` 项目，对应 `com.qihoo360.replugin:replugin-host-lib:version` 的依赖，由 `Host` 项目在 `Module` 的 `build.gradle` 中引入，主要负责 `RePlugin` 框架的初始化、类加载、启动以及管理 `Plugin` 等；
* replugin-plugin-gradle： `Gradle 插件`项目，对应 `com.qihoo360.replugin:replugin-plugin-gradle:version` 的依赖，由 `Plugin` 负责在根 `build.gradle` 中引入，主要负责在 `Plugin` 的编译期配置 `Plugin` 打包的相关信息以及动态替换 `Plugin` 项目中的继承结构，例如修改 `Activity` 的继承和 `Provider` 的重定向等；
* replugin-plugin-library： `Android Library` 项目，对应 `com.qihoo360.replugin:replugin-plugin-lib:version` 的依赖，由 `Plugin` 项目在 `Module` 的 `build.gradle` 中引入，主要功能是通过 `Java 反射`技术来调用 `Host` 程序中 `replugin-host-library` 的相关接口，并提供 `双向通信` 的功能。

另外三个 `Module` 为 `RePlugin` 的演示 `Sample`，主要用于对 `RePlugin` 的接口进行调用演示。

`document` 和 `outputs` 目录则主要用于存放文档以及相关的产出。

## RePlugin 基本原理

`RePlugin` 会在 `Host` 项目启动时 `Hook` 默认的 `PathClassLoader` 并替换为自定义的 `RePluginClassLoader`，并通过 `replugin-host-gradle` 插件在 `Host` 项目的编译期将预先定义的`坑位`写入 `AndroidManifest.xml`。

当我们启动一个 `Plugin` 中的 `ActivityA` 时，会将目标 `ActivityA` 记录下来，将 `ActivityA` 替换成通过`坑位`注册在 `AndroidManifest.xml` 中的 `ActivityNS`。

通过 `RePluginClassLoader` 和 `PluginDexClassLoader`（用于加载 `Plugin` 的 `ClassLoader`） 拦截 `ActivityNS` 的创建，并返回 `ActivityA` 的实例。

## AndroidStudio 中部署 `RePlugin`

为了方便测试以及学习，肥肥基于 [RePlugin V2.3.0](https://github.com/Qihoo360/RePlugin) 重新部署了 [RePluginForLearning](https://github.com/fiissh/RePluginForLearning)，除非特殊说明，后续文档中所有提到 `RePlugin` 的地方都将默认为 `RePluginForLearning`。

使用 `AndroidStudio` 

## RePlugin 核心概念

### Hook 点

`RePlugin` 的特点是只有一个 `Hook` 点。在 `Host` 应用启动的时候，`RePlugin` 将系统的 `PathClassLoader` 替换为自定义的 `RePluginClassLoader`，并且只修改了 `loadClass` 方法的行为以用于加载 `Plugin` 的类。

每一个 `Plugin` 都会有一个 `PluginDexClassLoader`，`Host` 应用中的 `RePluginClassLoader` 会通过调用 `Plugin` 应用中的 `PluginDexClassLoader` 以实现加载 `Plugin` 中的类好资源的目的。

### 进程

`RePlugin` 启东市会默认启动 `UI 进程`和 `Persistent 进程`两个进程：

* UI 进程：程序的主进程，代码中使用 `PROCESS_UI` 表示；
* Persistent 进程：守护进程，默认使用 `:GuardService` 表示进程名，代码中使用 `PROCESS_PERSIST` 表示。

`Persistent 进程`是 `RePlugin` 框架的核心进程之一。所有的其他进程在启动组件的时候都会通过 `PmHostSvc` 与 `Persistent 进程`通信。`Persistent 进程`主要有两方面的作用：

* `PluginManagerServer` 用于插件的管理，包括加载和卸载插件、更新插件信息、验证签名以及版本检查等；
* `PluginServiceServer`： 用于 `Service` 的启动等调度工作。

### 坑位

`坑位` 是 `RePlugin` 中的一个概念，指的是预先在 `Host` 项目的 `AndroidManifest.xml` 文件中注册的一些组件（包括 `Activity`、`Service` 和 `ContentProvider`）。这些坑位组件的代码都是由 `replugin-host-gradle` 插件在 `Host` 项目的编译期生成的，在实际的业务中并不会被使用。

`坑位` 的作用是，在启动 `Plugin` 项目中的的组件时，会用预先注册的`坑位`替代要启动的组件，并且建立一个与`坑位`真实组件之间的对应关系（使用 `ActivityState` 表示），然后在加载类的时候 `RePluginClassLoader` 根据该对应关系，通过 `PluginDexClassLoader` 加载对应的资源。

### ClassLoader

系统中常见的 `ClassLoader` 有如下三种：

* BootClassLoader：系统启动时创建
* PathClassLoader：应用程序启动时创建，只能加载内部（也就是应用程序自己的）`dex`
* DexClassLoader：可以加载外部的 `dex`

`RePlugin` 框架中有两种类型的 `ClassLoader`：

* RePluginClassLoader： `Host` 应用的 `ClassLoader`，继承自 `PathClassLoader`，也是 `RePlugin` 框架唯一 `Hook` 的地方
* PluginDexClassLoader： `Plugin` 应用的 `ClassLoader`，继承自 `DexClassLoader`。`RePluginClassLoader` 通过它可以访问 `Plugin` 中的资源

## RePlugin 核心类

* RePlugin：框架对外的入口类，主要提供安装插件、卸载插件、更新插件、预加载插件、`startActivity` 等方法的入口；
* RePlugin.App：针对 `Application` 的入口类，所有针对 `Plugin` 的 `Application` 的调用都从此处进行初始化；
* PmBase：插件的管理类，主要用于初始化插件、加载插件等；
* PluginContainers：插件容器管理类
* PluginCommImpl: 本地接口的实现，如 `startActivity`、`getActivityInfo` 和 `loadPluginActivity` 等
* PluginLibraryInternalProxy： 类似于 `Activity` 的接口实现，内部实现了 `startActivity` 的逻辑以及 `Plugin` 中的 `Activity` 生命周期的接口
