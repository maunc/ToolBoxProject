package com.maunc.toolbox.commonbase.utils

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import com.maunc.toolbox.ToolBoxApplication
import java.io.File
import java.io.IOException


private const val KB = 1024L
private const val MB = KB * 1024
private const val GB = MB * 1024

/**
 * @param bytes 文件大小（字节）
 * @return 格式化字符串，如 "2.5GB"、"1024KB"、"512B"
 */
@SuppressLint("DefaultLocale")
fun formatFileSize(bytes: Long): String {
    return when {
        bytes >= GB -> String.format("%.2fGB", bytes.toFloat() / GB)
        bytes >= MB -> String.format("%.2fMB", bytes.toFloat() / MB)
        bytes >= KB -> String.format("%.2fKB", bytes.toFloat() / KB)
        bytes > 0 -> "${bytes}B"
        else -> "0 B"
    }
}

/**
 * 获取文件的字节大小（本地文件）
 * @param filePath 文件路径
 * @return 字节数，文件不存在/不是文件返回0
 */
fun obtainFileSize(filePath: String): Long {
    val file = File(filePath)
    return if (file.exists() && file.isFile) file.length() else 0L
}

/**
 * 在SDCard根目录创建文件夹
 */
fun createFileDirFromSdCard(dirName: String): Boolean {
    if (!isSDCardAvailable()) return false
    if (dirName.trim().isEmpty()) return false
    val sdCardRoot = File(obtainSDCardRootPath())
    val targetFile = File(sdCardRoot, dirName)
    try {
        if (targetFile.exists() && targetFile.isDirectory) return false
        return targetFile.mkdirs()
    } catch (e: Exception) {
        return false
    }
}

fun createFileDir(rootPath: String, dirName: String): Boolean {
    if (!File(rootPath).exists()) return false
    if (dirName.trim().isEmpty()) return false
    val targetFile = File(rootPath, dirName)
    try {
        if (targetFile.exists() && targetFile.isDirectory) return false
        return targetFile.mkdirs()
    } catch (e: Exception) {
        return false
    }
}

/**
 * 获取SDCARD跟目录
 */
fun obtainSDCardRootPath(): String {
    val rootFile = getSDCardRootPath()
    return if (rootFile != null) rootFile.absolutePath else ""
}

private fun getSDCardRootPath(): File? {
    if (!isSDCardAvailable()) {
        return null
    }
    var sdCardRoot: File?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        sdCardRoot = ToolBoxApplication.app.getExternalFilesDir(null)
        if (sdCardRoot != null) {
            sdCardRoot = sdCardRoot.parentFile?.parentFile?.parentFile?.parentFile
        }
    } else {
        sdCardRoot = Environment.getExternalStorageDirectory()
    }
    return if (sdCardRoot != null && isPathWritable(sdCardRoot)) sdCardRoot else null
}

/**
 * 检查SD卡是否挂载且可用
 */
fun isSDCardAvailable(): Boolean {
    return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
}

/**
 * 检查路径是否可读写
 */
private fun isPathWritable(path: File?): Boolean {
    if (path == null || !path.exists()) {
        return false
    }
    val testFile = File(path, "test_write.txt")
    try {
        if (testFile.createNewFile()) {
            testFile.delete()
            return true
        }
    } catch (e: IOException) {
        return false
    }
    return false
}