/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.qihoo360.replugin;

import android.content.Context;

import com.qihoo360.replugin.helper.LogDebug;
import com.qihoo360.replugin.helper.LogRelease;

import java.io.File;

/**
 * 用来自定义RePlugin行为的类。 <p>
 * 必须在RePlugin.App.attachBaseContext中被传递。一旦生效就无法再改变 <p>
 * 具体用法： <p>
 * <code>
 * RePlugin.App.attachBaseContext(this, "xxx", new RePluginConfig().setCallbacks(cb).setPersistentName(":srv2"));
 * </code>
 *
 * @author RePlugin Team
 */

public final class RePluginConfig {

    /**
     * 插件框架对外回调接口集
     */
    private RePluginCallbacks callbacks;
    /**
     * 插件化框架对外事件回调接口集
     */
    private RePluginEventCallbacks eventCallbacks;

    /**
     * 插件的安装目录。默认初始化为 /data/data/package/files/ 目录
     */
    private File pnInstallDir;
    /**
     * 是否验证签名，默认为 false
     */
    private boolean verifySign = false;
    /**
     * 是否开启 persistent 进程。默认为 false
     */
    private boolean persistentEnable = true;

    /**
     * 是否允许插件使用宿主类。默认为 false
     */
    private boolean useHostClassIfNotFound = false;
    /**
     * 在插件安装时，是否将文件 移动 到 app_p_a 目录下。默认为 true
     */
    private boolean moveFileWhenInstalling = true;
    /**
     * 是否输出详细的 LOG 信息。默认为 false
     */
    private boolean printDetailLog = false;
    /**
     * 框架默认版本号，默认为 4
     */
    private int defaultFrameworkVersion = 4;

    /**
     * host 的版本名称
     */
    private String hostVersionName = "";
    /**
     * host 的编译 ID
     */
    private String hostBuildID = "";

    /**
     * 是否在 ART 上对首次加载插件速度做优化，默认为false
     */
    private boolean optimizeArtLoadDex = false;

    /**
     * 获取插件回调方法。通常无需调用此方法。
     *
     * @return RePluginCallbacks
     */
    public RePluginCallbacks getCallbacks() {
        return callbacks;
    }

    /**
     * 设置插件回调方法，可自定义插件框架的回调行为
     *
     * @param callbacks 可供外界使用的回调
     * @return RePluginConfig
     */
    public RePluginConfig setCallbacks(RePluginCallbacks callbacks) {
        if (!checkAllowModify()) {
            return this;
        }
        this.callbacks = callbacks;
        return this;
    }

    /**
     * 获取插件化框架的事件回调方法，通常无需调用此方法。
     *
     * @return RePluginEventCallbacks
     */
    public RePluginEventCallbacks getEventCallbacks() {
        return eventCallbacks;
    }

    /**
     * 设置插件化框架的事件回调方法，调用者可自定义插件框架的事件回调行为
     *
     * @param eventCallbacks 可供外界使用的回调
     * @return RePluginConfig
     */
    public RePluginConfig setEventCallbacks(RePluginEventCallbacks eventCallbacks) {
        if (!checkAllowModify()) {
            return this;
        }
        this.eventCallbacks = eventCallbacks;
        return this;
    }

    /**
     * 获取"p-n型插件安装的路径"
     *
     * @return File
     */
    public File getPnInstallDir() {
        return pnInstallDir;
    }

    /**
     * 设置“p-n型插件安装的路径”。默认为宿主的files目录下 <p>
     * 提示："纯APK"方案不受此方法的影响，新方案可在任意目录上安装
     *
     * @param pnInstallDir 插件安装的路径
     * @return RePluginConfig
     */
    public RePluginConfig setPnInstallDir(File pnInstallDir) {
        if (!checkAllowModify()) {
            return this;
        }
        this.pnInstallDir = pnInstallDir;
        return this;
    }

    /**
     * 是否开启插件签名校验
     *
     * @return boolean
     */
    public boolean getVerifySign() {
        return verifySign;
    }

    /**
     * 设置插件是否开启签名校验。默认为False。但强烈建议开启此开关。 <p>
     * 此开关将必须和 RePlugin.addCertSignature 配合使用。<p>
     * 注意：该功能仅针对“纯APK”插件
     *
     * @param verifySign
     * @return RePluginConfig
     */
    public RePluginConfig setVerifySign(boolean verifySign) {
        if (!checkAllowModify()) {
            return this;
        }
        this.verifySign = verifySign;
        return this;
    }

    /**
     * 是否当插件没有指定类时，使用宿主的类？ <p>
     * 有关该开关的具体说明，请参见setUseHostClass方法
     *
     * @return boolean
     * @since 1.3.0
     */
    public boolean isUseHostClassIfNotFound() {
        return useHostClassIfNotFound;
    }

    /**
     * 当插件没有指定类时，是否允许使用宿主的类？若为true，则当插件内没有指定类时，将默认使用宿主的。 <p>
     * 例如：插件中用反射使用A类（如通过UI的XML标签），但A在插件中不存在，则使用宿主中的相同的A类，若宿主也不存在，则抛出ClassNotFound异常 <p>
     * 适用场景：宿主有FrescoView、Common View等
     *
     * @param useHostClassIfNotFound 是否使用宿主类
     * @return RePluginConfig
     * @since 1.3.0
     */
    public RePluginConfig setUseHostClassIfNotFound(boolean useHostClassIfNotFound) {
        if (!checkAllowModify()) {
            return this;
        }
        this.useHostClassIfNotFound = useHostClassIfNotFound;
        return this;
    }

    /**
     * 在插件安装时，是否将文件“移动”到app_p_a目录下？默认为True。 <p>
     * 有关该开关的具体说明，请参见setMoveFileWhenInstalling方法
     *
     * @return boolean
     * @since 2.0.0
     */
    public boolean isMoveFileWhenInstalling() {
        return moveFileWhenInstalling;
    }

    /**
     * 在插件安装时，是否将文件“移动”到app_p_a目录下？默认为True。 <p>
     * 若为False，则表示只是“复制”到app_p_a目录下，原来安装前的APK文件还会保留。不推荐这么做，那样会浪费内部存储空间 <p>
     * 注：只针对“纯APK”方案插件，对p-n无任何效果（因为p-n本身就不是一个标准的APK，必须“释放”而不能“移动”到app_plugin_v3目录下）
     *
     * @param moveFileWhenInstalling 是否将文件“移动”到app_p_a目录下？
     * @return RePluginConfig
     * @since 2.0.0
     */
    public RePluginConfig setMoveFileWhenInstalling(boolean moveFileWhenInstalling) {
        if (!checkAllowModify()) {
            return this;
        }
        this.moveFileWhenInstalling = moveFileWhenInstalling;
        return this;
    }

    /**
     * 获取宿主的 BuildID
     *
     * @return String
     * @since 2.2.2
     */
    public String getHostBuildID() {
        return hostBuildID;
    }

    /**
     * 设置宿主的 BuildID <p>
     * BuildID 是一个比 VersionName 和 VersionCode 更细的维度（例如：服务器每 build 一次，版本号加 1)
     *
     * @param buildID 宿主的BuildID
     * @return RePluginConfig
     * @since 2.2.2
     */
    public RePluginConfig setHostBuild(String buildID) {
        if (!checkAllowModify()) {
            return this;
        }
        hostBuildID = buildID;
        return this;
    }

    /**
     * 获取宿主的 VersionName
     *
     * @return String
     * @since 2.2.2
     */
    public String getHostVersionName() {
        return hostVersionName;
    }

    /**
     * 设置宿主的 VersionName
     *
     * @param versionName 宿主的VersionName
     * @return RePluginConfig
     * @since 2.2.2
     */
    public RePluginConfig setHostVersionName(String versionName) {
        if (!checkAllowModify()) {
            return this;
        }
        hostVersionName = versionName;
        return this;
    }

    /**
     * 获取宿主的VersionBuild号
     *
     * @return String
     * @since 2.2.2
     */
    public String getHostVersionBuild() {
        return RePlugin.getConfig().getHostVersionName() + "." + RePlugin.getConfig().getHostBuildID();
    }

    /**
     * 是否打印更详细的日志？
     *
     * @return boolean
     * @since 2.0.0
     */
    public boolean isPrintDetailLog() {
        return printDetailLog;
    }

    /**
     * 是否打印更详细的日志？注意，可能会导致“刷屏”，以及因输出内存日志而出现一定的性能问题。 <p>
     * 默认为：False。若为 Release 版 AAR 则此开关无效
     *
     * @param printDetailLog 是否打印？
     * @return RePluginConfig
     * @since 2.0.0
     */
    public RePluginConfig setPrintDetailLog(boolean printDetailLog) {
        this.printDetailLog = printDetailLog;
        return this;
    }

    /**
     * 获取框架默认版本号
     *
     * @return int
     * @since 2.1.0
     */
    public int getDefaultFrameworkVersion() {
        return defaultFrameworkVersion;
    }

    /**
     * 设置框架默认版本号
     *
     * @param defaultFrameworkVersion 框架默认版本号
     * @return RePluginConfig
     * @since 2.1.0
     */
    public RePluginConfig setDefaultFrameworkVersion(int defaultFrameworkVersion) {
        if (!checkAllowModify()) {
            return this;
        }
        this.defaultFrameworkVersion = defaultFrameworkVersion;
        return this;
    }

    /**
     * 针对RePlugin.App.AttachBaseContext的调用，初始化默认值
     * <p>
     * 该方法主要是用于初始化一些依赖 Context 的对象
     *
     * @param context
     */
    void initDefaults(Context context) {
        // 初始化插件的安装目录 pnInstallDir，
        if (pnInstallDir == null) {
            pnInstallDir = context.getFilesDir();
        }

        // 初始化 插件框架对外回调接口集
        // FIXME RePluginCallbacks 的自定义实现可以通过 setCallbacks(RePluginCallbacks rePluginCallbacks) 传入，在此处进行初始化是不是有点过早？
        if (callbacks == null) {
            callbacks = new RePluginCallbacks(context);
        }

        // 初始化 插件化框架对外事件回调接口集
        //  FIXME  RePluginEventCallbacks 的自定义实现可以通过 setEventCallbacks(RePluginEventCallbacks rePluginEventCallbacks) 传入，在此处进行初始化是不是有点过早？
        if (eventCallbacks == null) {
            eventCallbacks = new RePluginEventCallbacks(context);
        }
    }

    /**
     * 不允许在 attachBaseContext 调用完成之后再修改 RePluginConfig 对象中的内容
     *
     * @return
     */
    private boolean checkAllowModify() {
        if (RePlugin.App.sAttached) {
            // 不能在此处抛异常，因为个别情况下，宿主的attachBaseContext可能会被调用多次，导致最终出现异常。这里只打出日志即可。
            // throw new IllegalStateException("Already called attachBaseContext. Do not modify!");
            if (LogRelease.LOGR) {
                LogRelease.e(LogDebug.PLUGIN_TAG, "rpc.cam: do not modify", new Throwable());
            }
            return false;
        }
        return true;
    }

    /**
     * 是否在 ART 上对首次加载插件速度做优化
     *
     * @return boolean
     */
    public boolean isOptimizeArtLoadDex() {
        return optimizeArtLoadDex;
    }

    /**
     * 是否在 ART 上对首次加载插件速度做优化，默认为false
     *
     * @param optimizeArtLoadDex
     * @return RePluginConfig
     * @since 2.2.2
     */
    public RePluginConfig setOptimizeArtLoadDex(boolean optimizeArtLoadDex) {
        if (!checkAllowModify()) {
            return this;
        }
        this.optimizeArtLoadDex = optimizeArtLoadDex;
        return this;
    }
}