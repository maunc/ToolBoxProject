package com.maunc.toolbox.devicemsg.viewmodel

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.maunc.base.BaseApp
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.toolbox.ToolBoxApplication
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeviceMessageViewModel : BaseViewModel<BaseModel>() {

    companion object {
        const val UNKNOWN = "Unknown"
    }

    /**
     * 设备制造商
     */
    fun obtainManufacturer() = try {
        val manufacturer = Build.MANUFACTURER
        if (TextUtils.isEmpty(manufacturer)) UNKNOWN else manufacturer.trim()
    } catch (e: Exception) {
        UNKNOWN
    }

    /**
     * 安卓版本对应的号码
     */
    fun obtainAndroidApiLevelVersion() = try {
        Build.VERSION.SDK_INT
    } catch (e: Exception) {
        -1
    }

    /**
     * 安卓版本
     */
    fun obtainAndroidApiVersion() = try {
        val version = Build.VERSION.RELEASE
        if (TextUtils.isEmpty(version)) UNKNOWN else version.trim()
    } catch (e: Exception) {
        UNKNOWN
    }

    /**
     * Linux内核版本
     */
    fun obtainKernelVersion() = try {
        val kernelVersion = getSystemProperty("ro.build.version.release")// 安卓系统版本
        val kernelRelease = getSystemProperty("ro.kernel.version")// 内核详细版本
        val buildKernel = Build.VERSION.INCREMENTAL
        if (kernelRelease.isNotEmpty()) kernelRelease
        else if (buildKernel != null && buildKernel.isNotEmpty()) buildKernel
        else UNKNOWN
    } catch (e: java.lang.Exception) {
        UNKNOWN
    }

    /**
     * 获取手机硬件型号（原始值，如"23127PN0CC"对应小米14 Ultra）
     */
    fun obtainDeviceModel() = try {
        val model = Build.MODEL
        if (TextUtils.isEmpty(model)) UNKNOWN else model.trim()
    } catch (e: Exception) {
        UNKNOWN
    }

    /**
     * 获取主板型号（核心字段，如"sm8650"、"kirin9010"、"mt6893"）
     * 优先级：ro.board.platform > ro.product.board > Build.BOARD
     */
    fun obtainBoardModel(): String {
        val huaweiPlatform = getSystemProperty("ro.hw.platform")
        if (!TextUtils.isEmpty(huaweiPlatform)) {
            return huaweiPlatform.trim().lowercase()
        }
        val boardPlatform = getSystemProperty("ro.board.platform")
        if (!TextUtils.isEmpty(boardPlatform)) {
            return boardPlatform.trim()
        }
        val productBoard = getSystemProperty("ro.product.board")
        if (!TextUtils.isEmpty(productBoard)) {
            return productBoard.trim()
        }
        return try {
            val buildBoard = Build.BOARD
            if (TextUtils.isEmpty(buildBoard)) UNKNOWN else buildBoard.trim()
        } catch (e: Exception) {
            UNKNOWN
        }
    }

    /**
     * 获取主板硬件版本
     */
    fun obtainBoardHardwareVersion(): String {
        val hardwareVer = getSystemProperty("ro.board.hardware")
        return if (TextUtils.isEmpty(hardwareVer)) UNKNOWN else hardwareVer.trim()
    }

    /**
     * Build TAGS（例如 release-keys / test-keys）
     */
    fun obtainBuildTags(): String = try {
        val tags = Build.TAGS
        if (TextUtils.isEmpty(tags)) UNKNOWN else tags.trim()
    } catch (e: Exception) {
        UNKNOWN
    }

    /**
     * 系统构建时间（常被当作“出厂时间”的近似值）
     *
     * 说明：Android 通常无法获取真实出厂日期；Build.TIME 是系统镜像构建时间（毫秒）。
     */
    fun obtainBuildTime(): String = try {
        val timeMillis = Build.TIME
        if (timeMillis <= 0L) UNKNOWN
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.format(Date(timeMillis))
    } catch (e: Exception) {
        UNKNOWN
    }

    /**
     * CPU 指令集（ABI 列表）
     */
    @SuppressLint("ObsoleteSdkInt")
    fun obtainCpuAbis(): String = try {
        val abis = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS?.toList().orEmpty()
        } else {
            listOfNotNull(Build.CPU_ABI, Build.CPU_ABI2).filter { it.isNotBlank() }
        }
        if (abis.isEmpty()) UNKNOWN else abis.joinToString(", ")
    } catch (e: Exception) {
        UNKNOWN
    }

    /**
     * Build ID（构建ID）
     */
    fun obtainBuildId(): String = try {
        val id = Build.ID
        if (TextUtils.isEmpty(id)) UNKNOWN else id.trim()
    } catch (e: Exception) {
        UNKNOWN
    }

    /**
     * Android CodeName（开发代号）
     */
    fun obtainCodeName(): String = try {
        val code = Build.VERSION.CODENAME
        if (TextUtils.isEmpty(code)) UNKNOWN else code.trim()
    } catch (e: Exception) {
        UNKNOWN
    }

    /**
     * 硬件识别码（常用：ANDROID_ID）
     *
     * 说明：受系统/隐私策略影响，不保证跨恢复出厂/跨用户稳定；但一般用于设备唯一标识足够。
     */
    @SuppressLint("HardwareIds")
    fun obtainAndroidId(): String = try {
        val id = Settings.Secure.getString(
            BaseApp.app.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        if (TextUtils.isEmpty(id)) UNKNOWN else id.trim()
    } catch (e: Exception) {
        UNKNOWN
    }

    @SuppressLint("PrivateApi")
    private fun getSystemProperty(key: String): String {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val getMethod: Method = clazz.getMethod("get", String::class.java)
            getMethod.isAccessible = true
            val value = getMethod.invoke(null, key) as String?
            value?.trim() ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}