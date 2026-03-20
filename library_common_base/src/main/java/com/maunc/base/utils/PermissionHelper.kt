package com.maunc.base.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * 统一封装权限请求
 */
class RequestPermissionHelper(
    private val caller: ActivityResultCaller,
    private val permission: String,
    private var onGranted: () -> Unit = {},
    private var onDenied: () -> Unit = {},
    private var onPermanentDenied: () -> Unit = {},
) {

    private var singlePermissionLauncher = caller.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            if (checkPermissionPermanentDenied()) {
                // 永久拒绝
                onPermanentDenied.invoke()
            } else {
                // 拒绝过
                onDenied.invoke()
            }
        } else {
            onGranted.invoke()
        }
    }

    fun requestPermission() {
        if (!(caller as Context).hasPermissionGranted(permission)) {
            singlePermissionLauncher.launch(permission)
        } else {
            onGranted.invoke()
        }
    }

    /**
     * 判断权限是否被永久拒绝（用户勾选了不再询问）
     */
    private fun checkPermissionPermanentDenied() =
        !ActivityCompat.shouldShowRequestPermissionRationale(
            caller as Activity, permission
        ) && ContextCompat.checkSelfPermission(
            caller as Activity, permission
        ) != PackageManager.PERMISSION_GRANTED
}


/**
 * 前往当前app设置页面
 */
fun AppCompatActivity.startAppSystemSettingPage() = startActivity(
    Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.parse("package:${packageName}")
    }
)

/**
 * 检查并申请文件读写权限
 */
private const val REQUEST_CODE_MANAGE_FILE = 1001
fun AppCompatActivity.checkFilePermission(): Boolean {
    // Android 10+ 且需要全文件访问：申请MANAGE_EXTERNAL_STORAGE
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (hasManageFilePermission()) {
            return true
        }
        // 跳转系统设置申请全文件访问
        startActivityIfNeeded(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.parse("package:${this@checkFilePermission.packageName}")
        }, REQUEST_CODE_MANAGE_FILE)
    }
    //Android 13+：申请细分的媒体权限
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return hasMediaReadPermission()
    }
    //Android 6.0 - 12：申请传统存储权限
    return hasLegacyStoragePermission()
}

/**
 * 用户是否已经永久拒绝这个权限(若拒绝则需要手动开启)
 * @param permission 权限
 */
fun AppCompatActivity.checkPermissionPermanentDenied(
    permission: String,
) = !shouldShowRequestPermissionRationale(
    permission
) && ContextCompat.checkSelfPermission(
    this, permission
) != PackageManager.PERMISSION_GRANTED

/**
 * 查看是否有权限(单个)
 * @param permission 权限
 */
fun Context.hasPermissionGranted(
    permission: String,
) = ContextCompat.checkSelfPermission(
    this, permission
) == PackageManager.PERMISSION_GRANTED

/**
 * 查看是否有权限(多个)
 * @param permissionList 权限集合
 */
fun Context.hasPermissionGranted(
    permissionList: Array<String>,
) = permissionList.all {
    ContextCompat.checkSelfPermission(
        this, it
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * 判断是否拥有全文件访问权限（Android 10+）
 */
@TargetApi(Build.VERSION_CODES.R)
fun hasManageFilePermission() = Environment.isExternalStorageManager()

/**
 * 判断是否拥有媒体文件读取权限（Android 13+）
 */
@TargetApi(Build.VERSION_CODES.TIRAMISU)
fun Context.hasMediaReadPermission() = arrayOf(
    Manifest.permission.READ_MEDIA_IMAGES,
    Manifest.permission.READ_MEDIA_VIDEO,
    Manifest.permission.READ_MEDIA_AUDIO
).all {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

/**
 * 判断是否拥有传统存储权限（Android 6.0-12）
 */
fun Context.hasLegacyStoragePermission() = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
).all {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

/**
 * 判断是否有相机权限
 */
fun Context.hasCameraPermission() = ContextCompat.checkSelfPermission(
    this, Manifest.permission.CAMERA
) == PackageManager.PERMISSION_GRANTED


/**
 * 判断是否有录音
 */
fun Context.hasRecordAudioPermission() = ContextCompat.checkSelfPermission(
    this, Manifest.permission.RECORD_AUDIO
) == PackageManager.PERMISSION_GRANTED