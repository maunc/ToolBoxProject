package com.maunc.toolbox.commonbase.utils

import android.annotation.SuppressLint
import java.io.File


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
    return if (file.exists() && file.isFile) {
        file.length()
    } else {
        0L
    }
}