<p align="center">
  <a href="https://github.com/Qihoo360/RePlugin/wiki">
    <img alt="RePlugin Logo" src="https://github.com/Qihoo360/RePlugin/wiki/img/RePlugin.png" width="400"/>
  </a>
</p>


[![license](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat)](https://github.com/Qihoo360/RePlugin/blob/master/LICENSE)

`RePlugin` 是360开源的一款 `Android` 平台全面插件化框架。具体介绍请参考 [RePlugin](https://github.com/Qihoo360/RePlugin/blob/dev/README_CN.md)。

 [RePluginForLearning](https://github.com/fiissh/RePluginForLearning) 是基于 [RePlugin V2.3.0](https://github.com/Qihoo360/RePlugin/releases/tag/v2.3.0) 版本进行的完整部署，改变了原有的项目结构以及部分配置，主要变动如下：

* 为方便测试，改变原有项目结构，将 `replugin-host-library`、`replugin-plugin-library`、`replugin-host-gradle` 和 `replugin-plugin-gradle` 四个核心项目部署在同一个 `Project` 下；
* 增加演示用的 `Sample` 项目，其中包含一个 `Host` 项目 `app`，两个 `Plugin` 项目 `replugin-plugin-sample-a` 和 `replugin-plugin-sample-b`；
* 改变 `replugin-host-gradle` 和 `replugin-plugin-gradle` 两个 `Gradle 插件` 项目的发布行为，把插件发布到 `jcenter` 修改为发布到 `outputs/gradle/` 目录下，并将 `outputs/gradle/repos/` 目录添加到 `Maven` 索引地址中
* 其他代码逻辑的更改请参考具体的 `commit` 记录

`RePlugin` 目前支持的特性：

| 特性 | 描述 |
|:-------------:|:-------------:|
| 组件 | **四大组件（含静态Receiver）** |
| 升级无需改主程序Manifest | **完美支持** |
| Android特性 | **支持近乎所有（包括SO库等）** |
| TaskAffinity & 多进程 | **支持（*坑位方案*）** |
| 插件类型 | **支持自带插件（*自识别*）、外置插件** |
| 插件间耦合 | **支持Binder、Class Loader、资源等** |
| 进程间通讯 | **支持同步、异步、Binder、广播等** |
| 自定义Theme & AppComat | **支持** |
| DataBinding | **支持** |
| 安全校验 | **支持** |
| 资源方案 | **独立资源 + Context传递（相对稳定）** |
| Android 版本 | **API Level 9+ （2.3及以上）** |

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

使用 `AndroidStudio` 将项目 `clone` 到本地并打开之后，会自动加载所需要引用的所有资源：

* 请执行两个 `Plugin` 项目 `replugin-plugin-sample-a` 和 `replugin-plugin-sample-b` 的完整编译
* 执行 `app` 的 `copyOutputsToHostApplication` 任务，将 `replugin-plugin-sample-a` 和 `replugin-plugin-sample-b` 生成的 `Plugin` 复制到 `app` 项目的 `assets/plugins` 目录中。

之后便可以运行调试 `Host` 项目 `app`。

如果需要本地调试 `replugin-host-gradle` 和 `replugin-plugin-library` 两个 `Gradle` 插件项目，请按照如下步骤执行：

* 执行 `replugin-host-gradle` 和 `replugin-plugin-library` 的完整编译
* 执行 `replugin-host-gradle` 和 `replugin-plugin-library` 各自项目中的 `uploadArchives` 的任务，将 `Gradle` 插件部署到本丢 `outputs/gradle/repos/` 目录下；
* 清空项目的所有依赖，重新编译项目，便可使用本地的 `Gradle` 插件。 

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
* RePluginApplication：`RePlugin` 对外提供方便 `Host` 继承的 `Application` 类；
* RePluginCallbacks：主要用来生成 `Host` 和 `Plugin` 的 `ClassLoader`、要使用的的 `Plugin` 不存在时或者 `Plugin` 文件过大时回调等
* RePluginEventCallbacks: 框架对外事件回调接口集，`Host` 需继承此类，并复写相应的方法来自定义框架的事件处理机制，包括安装 `Plugin` 成功、失败、启动 `Plugin` 中的 `Activity` 时的一些事件回调等；
* RePluginConfig：主要用于对框架的一些初始化设置；
* RePlugin.App：针对 `Application` 的入口类，所有针对 `Plugin` 的 `Application` 的调用都从此处进行初始化；
* PmBase：插件的管理类，主要用于初始化插件、加载插件等；
* PluginContainers：插件容器管理类；
* PluginCommImpl: 本地接口的实现，如 `startActivity`、`getActivityInfo` 和 `loadPluginActivity` 等；
* PluginLibraryInternalProxy： 类似于 `Activity` 的接口实现，内部实现了 `startActivity` 的逻辑以及 `Plugin` 中的 `Activity` 生命周期的接口；

## Module 介绍

### replugin-host-gradle

`replugin-host-gradle` 是 `Host` 应用的 `Gradle` 插件，其主要职责是：

* 在 `Host` 应用打包（编译）的过程中，为组件（`Activity`、`Service` 和 `Provider`）在 `AndroidManifest.xml` 中生成对应的坑位，`AndroidManifest.xml` 文件在打包前后的差异请比较 `src/main/AndroidManifest.xml` 和 `build/intermediates/manifests/full/debug/AndroidManifest.xml` 两个文件。生成 `坑位` 的具体业务，请参考 `replugin-host-gradle/src/main/groovy/com.qihoo360.replugin.gradle.host.handlemanifest/ComponentsGenerator.groovy`
* 根据 `Host` 项目的 `build.gradle` 中配置的 `repluginHostConfig` 生成对应的 `HostBuildConfig.java` 类（`build/intermediates/classes/debug/com/qihoo360/replugin/gen/` 目录下）：

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qihoo360.replugin.gen;

public class RePluginHostConfig {
    public static String PERSISTENT_NAME = ":GuardService";
    public static boolean PERSISTENT_ENABLE = true;
    public static int ACTIVITY_PIT_COUNT_TS_STANDARD = 2;
    public static int ACTIVITY_PIT_COUNT_TS_SINGLE_TOP = 2;
    public static int ACTIVITY_PIT_COUNT_TS_SINGLE_TASK = 2;
    public static int ACTIVITY_PIT_COUNT_TS_SINGLE_INSTANCE = 3;
    public static int ACTIVITY_PIT_COUNT_NTS_STANDARD = 6;
    public static int ACTIVITY_PIT_COUNT_NTS_SINGLE_TOP = 2;
    public static int ACTIVITY_PIT_COUNT_NTS_SINGLE_TASK = 3;
    public static int ACTIVITY_PIT_COUNT_NTS_SINGLE_INSTANCE = 2;
    public static int ACTIVITY_PIT_COUNT_TASK = 2;
    public static boolean ACTIVITY_PIT_USE_APPCOMPAT = true;
    public static int ADAPTER_COMPATIBLE_VERSION = 10;
    public static int ADAPTER_CURRENT_VERSION = 12;

    public RePluginHostConfig() {
    }
}

```

对应的 `build.gradle` 配置如下：

```groovy
repluginHostConfig {

    /** 自定义进程的数量(除 UI 和 Persistent 进程) */
    countProcess = 3

    /** 是否使用常驻进程？ */
    persistentEnable = true

    /** 常驻进程名称（也就是上面说的 Persistent 进程，开发者可自定义）*/
    persistentName = ':GuardService'

    /** 背景不透明的坑的数量 */
    countNotTranslucentStandard = 6
    countNotTranslucentSingleTop = 2
    countNotTranslucentSingleTask = 3
    countNotTranslucentSingleInstance = 2

    /** 背景透明的坑的数量 */
    countTranslucentStandard = 2
    countTranslucentSingleTop = 2
    countTranslucentSingleTask = 2
    countTranslucentSingleInstance = 3

    /** 宿主中声明的 TaskAffinity 的组数 */
    countTask = 2

    /** 是否使用 AppCompat 库 com.android.support:appcompat-v7:25.2.0 */
    useAppCompat = true

    /** HOST 向下兼容的插件版本 */
    compatibleVersion = 10

    /** HOST 插件版本 */
    currentVersion = 12

    /** plugins-builtin.json 文件名自定义,默认是 "plugins-builtin.json" */
    builtInJsonFileName = "plugins-builtin.json"

    /** 是否自动管理 plugins-builtin.json 文件,默认自动管理 */
    autoManageBuiltInJsonFile = true

    /** assert目录下放置插件文件的目录自定义,默认是 assert 的 "plugins" */
    pluginDir = "plugins"

    /** 插件文件的后缀自定义,默认是".jar" 暂时支持 jar 格式*/
    pluginFilePostfix = ".jar"

    /** 当发现插件目录下面有不合法的插件 jar (有可能是特殊定制 jar)时是否停止构建,默认是 true */
    enablePluginFileIllegalStopBuild = true

}
```

以上 `repluginHostConfig` 配置项是 `replugin-host-gradle` 插件支持的全部配置项，详细信息请移步 `replugin-host-gradle/src/main/groovy/com.qihoo360.replugin.gradle.host/Replugin.RepluginConfig.groovy`。

* 根据 `Host` 项目的 `assets/plugin` 目录，解析 `Plugin` 文件并生成包含文件名、包名、版本、路径等属性的 `plugins-builtin.json`，该文件最终存放在 `assets` 的根目录。以下为 `app` 项目下包含 `sample-a.jar` 和 `sample-b.jar` 两个 `Plugin` 项目时生成的 `plugins-builtin.json` 文件（`build/intermediates/assets/assets` 目录）：

```json
[
  {
    "high": null,
    "frm": null,
    "ver": 1,
    "low": null,
    "pkg": "com.qihoo360.replugin.sample.a",
    "path": "plugins/sample-a.jar",
    "name": "sample-a"
  },
  {
    "high": null,
    "frm": null,
    "ver": 1,
    "low": null,
    "pkg": "com.qihoo360.replugin.sample.b",
    "path": "plugins/sample-b.jar",
    "name": "sample-b"
  }
]
```

### replugin-host-library

`replugin-host-library` 是 `Host` 项目的依赖库，也是 `RePlugin` 最为核心的项目。它的主要职责有：

* 初始化 `RePlugin` 框架
* 在 `Host` 应用启动时，`Hook` 应用程序的 `PathClassLoader`
* 加载、启动和卸载插件等插件管理的相关功能
* 通过 `Binder` 机制实现多进程的数据交互
* 实现插件之间、插件与宿主之间的数据交互

### replugin-plugin-gradle

`replugin-plugin-gradle` 是 `Plugin` 项目的 `Gradle` 插件，其主要原理是 通过 `Transfrom API` 和 `Javassist` 对编译期间字节码的动态修改，达到动态替换的目的：

* 将 `Plugin` 项目中的 `Activity` 的继承全部替换成 `RePlugin` 中预定义的 `Activity` 类型，例如将 `replugin-plugin-sample-a/src/main/java/com.qihoo360.replugin.sample.a.MainActivity.java` 原本的 `父 Activity` 替换为 `PluginAppCompatActivity`（可在 `replugin-plugin-sample-a/build/intermediates/classes/release/com/qihoo360/replugin/sample/a/MainActivity.class` 查看），`Activity` 的替换规则如下：

```groovy
    loaderActivityRules = [
            'android.app.Activity'                    : 'com.qihoo360.replugin.loader.a.PluginActivity',
            'android.app.TabActivity'                 : 'com.qihoo360.replugin.loader.a.PluginTabActivity',
            'android.app.ListActivity'                : 'com.qihoo360.replugin.loader.a.PluginListActivity',
            'android.app.ActivityGroup'               : 'com.qihoo360.replugin.loader.a.PluginActivityGroup',
            'android.support.v4.app.FragmentActivity' : 'com.qihoo360.replugin.loader.a.PluginFragmentActivity',
            'android.support.v7.app.AppCompatActivity': 'com.qihoo360.replugin.loader.a.PluginAppCompatActivity',
            'android.preference.PreferenceActivity'   : 'com.qihoo360.replugin.loader.a.PluginPreferenceActivity',
            'android.app.ExpandableListActivity'      : 'com.qihoo360.replugin.loader.a.PluginExpandableListActivity'
    ]
```

* 将 `Plugin` 项目中对 `android.support.v4.content.LocalBroadcastManager` 的调用修改为 `com.qihoo360.replugin.loader.b.PluginLocalBroadcastManager` 调用
* 将 `Plugin` 项目中对 `android.content.ContentResolver` 的调用修改为 `com.qihoo360.replugin.loader.p.PluginProviderClient` 调用
* 将 `Plugin` 项目中对 `android.content.ContentProviderClient` 的调用修改为 `com.qihoo360.loader2.mgr.PluginProviderClient2` 调用
* 将 `Plugin` 项目中对 `android.content.res.Resources.getIdentifier` 调用的第三个参数修改为 `Plugin` 项目的包名

以上替换的具体内容可参考 `replugin-plugin-gradle/src/main/groovy/com.qihoo360.replugin.gradle.plugin.injector.Injectors.groovy`。

以上具体的修改逻辑可参考 `replugin-plugin-gradle/src/main/groovy/com.qihoo360.replugin.gradle.plugin.injector` 包。

### replugin-plugin-library

`replugin-plugin-library` 是 `Plugin` 项目的依赖库，其主要职责是通过反射的方式使用 `Host` 项目中的接口和功能。

## License

RePlugin is [Apache v2.0 licensed](./LICENSE).