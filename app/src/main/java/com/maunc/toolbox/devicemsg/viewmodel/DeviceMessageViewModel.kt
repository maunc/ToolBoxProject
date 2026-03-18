package com.maunc.toolbox.devicemsg.viewmodel

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import java.lang.reflect.Method

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