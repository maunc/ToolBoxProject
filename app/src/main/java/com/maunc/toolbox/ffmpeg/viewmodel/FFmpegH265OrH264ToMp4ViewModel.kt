package com.maunc.toolbox.ffmpeg.viewmodel

import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import com.maunc.base.BaseApp
import com.maunc.base.ext.loge
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_NONE
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.constant.H265_OR_H264_TO_MP4_SAVE_PATH
import com.maunc.toolbox.ffmpeg.constant.SAVE_FFMPEG_PREFIX
import com.maunc.toolbox.ffmpeg.data.FFmpegH265OrH264ToMp4ResultData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class FFmpegH265OrH264ToMp4ViewModel : BaseViewModel<BaseModel>() {

    var transStatus = MutableLiveData(FFMPEG_NONE)//转换状态
    var resultList = MutableLiveData<MutableList<FFmpegH265OrH264ToMp4ResultData>>()
    var isExistFile = MutableLiveData<Boolean>()// 是否有数据

    //查询条件
    private val uri: Uri = MediaStore.Files.getContentUri("external")
    private val projection = arrayOf(
        MediaStore.Files.FileColumns.DATA,          // 文件路径
        MediaStore.Files.FileColumns.DISPLAY_NAME,  // 文件名称
        MediaStore.Files.FileColumns.SIZE,          // 文件大小
        MediaStore.Files.FileColumns.DATE_MODIFIED  // 文件修改时间（秒）
    )
    private val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE ? OR " +
            "${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE ?"
    private val args = arrayOf("%.h265", "%.h264")

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun initH265AndH264FileList() {
        val fileList = mutableListOf<FFmpegH265OrH264ToMp4ResultData>()
        viewModelScope.launch(Dispatchers.IO) {
            BaseApp.app.contentResolver.query(
                uri, projection, selection, args, null
            )?.use { cursor ->
                val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                val fileNameIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val dateModifiedIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
                while (cursor.moveToNext()) {
                    val path = cursor.getString(dataIndex)
                    if (path.lowercase().endsWith(".h265") || path.lowercase().endsWith(".h264")) {
                        val fileName = cursor.getString(fileNameIndex) ?: "未知文件名"
                        val size = cursor.getLong(sizeIndex)
                        // DATE_MODIFIED 返回的是秒级时间戳，需要转成毫秒
                        val dateModifiedMs = cursor.getLong(dateModifiedIndex) * 1000
                        fileList.add(
                            FFmpegH265OrH264ToMp4ResultData(
                                fileName = fileName,
                                fileSize = size,
                                filePath = path,
                                time = dateModifiedMs,
                                timeString = if (dateModifiedMs > 0) dateFormat.format(
                                    dateModifiedMs
                                ) else "未知时间"
                            )
                        )
                    }
                }
            }
            resultList.postValue(fileList)
        }
    }

    fun startTransformation(fileData: FFmpegH265OrH264ToMp4ResultData) {
        transStatus.value = FFMPEG_START
        val ffmpegCmd = obtainFFmpegH265OrH264ToMp4(fileData)
        FFmpegKit.executeAsync(ffmpegCmd, { session: FFmpegSession ->
            runOnUiThread {
                val returnCode = session.returnCode
                if (ReturnCode.isSuccess(returnCode)) {
                    "M3U8 TO MP4 Conversion successful".loge()
                    transStatus.value = FFMPEG_SUCCESS
                } else if (ReturnCode.isCancel(returnCode)) {
                    "M3U8 TO MP4 Conversion canceled ${session.logs}".loge()
                } else {
                    "M3U8 TO MP4 Conversion failed ${session.logs}".loge()
                    transStatus.value = FFMPEG_ERROR
                }
            }
        }, { log ->
            // 实时日志回调（可用于解析进度）
            "FFmpeg日志：${log.message}".loge()
        }, { statistics ->
            // 统计信息回调（如帧率、比特率等）
            "统计信息：$statistics".loge()
        })
    }

    private fun obtainFFmpegH265OrH264ToMp4(fileData: FFmpegH265OrH264ToMp4ResultData): String {
        val fileNameWithExt = fileData.fileName
        val fileNameWithoutExt = if (fileNameWithExt.contains(".")) {
            fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf("."))
        } else {
            fileNameWithExt
        }
        val mp4FileName = "$SAVE_FFMPEG_PREFIX$fileNameWithoutExt.mp4"
        return "-i ${fileData.filePath} -c:v copy -y ${
            File(H265_OR_H264_TO_MP4_SAVE_PATH, mp4FileName).absolutePath
        }"
    }
}