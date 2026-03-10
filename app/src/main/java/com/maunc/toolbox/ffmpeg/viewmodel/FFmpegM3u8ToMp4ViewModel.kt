package com.maunc.toolbox.ffmpeg.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import com.maunc.toolbox.ToolBoxApplication
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.logd
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.utils.obtainFileSize
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_NONE
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.constant.M3U8_TO_MP4_SAVE_PATH
import com.maunc.toolbox.ffmpeg.constant.SAVE_FFMPEG_PREFIX
import com.maunc.toolbox.ffmpeg.data.FFmpegM3u8ResultData
import com.maunc.toolbox.ffmpeg.data.FFmpegM3u8TsSegmentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class FFmpegM3u8ToMp4ViewModel : BaseViewModel<BaseModel>() {

    var transStatus = MutableLiveData(FFMPEG_NONE)//转换状态
    var m3u8ResultList = MutableLiveData<MutableList<FFmpegM3u8ResultData>>()
    var isExistM3u8File = MutableLiveData<Boolean>()// 是否有数据

    //查询条件
    private val uri: Uri = MediaStore.Files.getContentUri("external")
    private val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
    private val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE ?"
    private val args = arrayOf("%.m3u8")


    fun startTransformation(m3u8Data: FFmpegM3u8ResultData) {
        val ffmpegCmd = obtainFFmpegM3u8ToMp4(m3u8Data)
        transStatus.value = FFMPEG_START
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
            "FFmpeg日志：${log.message}".logd()
        }, { statistics ->
            // 统计信息回调（如帧率、比特率等）
            "统计信息：$statistics".logd()
        })
    }

    private fun obtainFFmpegM3u8ToMp4(m3u8Data: FFmpegM3u8ResultData): String {
        val fileNameWithExt = m3u8Data.m3u8FileName
        val fileNameWithoutExt = if (fileNameWithExt.contains(".")) {
            fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf("."))
        } else {
            fileNameWithExt
        }
        val mp4FileName = "$SAVE_FFMPEG_PREFIX$fileNameWithoutExt.mp4"
        return "-allowed_extensions ALL -i ${m3u8Data.m3u8FilePath} -c copy -y ${
            File(M3U8_TO_MP4_SAVE_PATH, mp4FileName).absolutePath
        }"
//        return "-allowed_extensions ALL -i ${m3u8Data.m3u8FilePath} -c:v mpeg4 -c:a mp3 -y ${
//            File(M3U8_TO_MP4_SAVE_PATH, mp4FileName).absolutePath
//        }"
    }

    fun initM3u8FileList() {
        viewModelScope.launch(Dispatchers.IO) {
            val m3u8FilePathList = mutableListOf<String>()
            ToolBoxApplication.app.contentResolver.query(
                uri, projection, selection, args, null
            )?.use { cursor ->
                val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                while (cursor.moveToNext()) {
                    val path = cursor.getString(dataIndex)
                    if (path.lowercase().endsWith(".m3u8")) {
                        m3u8FilePathList.add(path)
                    }
                }
            }
            if (m3u8FilePathList.isEmpty()) {
                m3u8ResultList.postValue(mutableListOf())
                return@launch
            }
            val m3u8ResultDataList = mutableListOf<FFmpegM3u8ResultData>()
            m3u8FilePathList.forEach { m3u8Path ->
                parseM3u8File(m3u8Path)?.let { m3u8Result ->
                    if (!m3u8Result.isAvailable) return@let
                    val resultWithCover = m3u8Result.copy(
                        coverBitmap = generateM3u8CoverBitmap(m3u8Result)
                    )
                    m3u8ResultDataList.add(resultWithCover)
                }
            }
            m3u8ResultList.postValue(m3u8ResultDataList)
        }
    }

    /**
     * 解析本地 m3u8 文件
     * @return 解析结果（包含片段数、每个片段的路径/时长）
     */
    private fun parseM3u8File(m3u8FilePath: String): FFmpegM3u8ResultData? {
        val m3u8File = File(m3u8FilePath)
        val m3u8FileName = m3u8File.name
        val m3u8ParentDir = m3u8File.parentFile
        if (!m3u8File.exists() || !m3u8File.isFile) return null
        val segmentList = mutableListOf<FFmpegM3u8TsSegmentData>()
        var isStandardM3u8 = false
        var currentSegmentIndex = 0
        var pendingDuration: Float? = null
        try {
            FileInputStream(m3u8File).use { fis ->
                BufferedReader(InputStreamReader(fis, StandardCharsets.UTF_8)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val trimmedLine = line?.trim() ?: continue
                        if (trimmedLine.isEmpty()) continue
                        //检查是否为标准 m3u8 文件
                        if (trimmedLine == "#EXTM3U") {
                            isStandardM3u8 = true
                            continue
                        }
                        //解析 #EXTINF 行，提取片段时长
                        if (trimmedLine.startsWith("#EXTINF:")) {
                            pendingDuration = filterSegmentFileDuration(trimmedLine)
                            continue
                        }
                        //解析 ts 路径（#EXTINF 的下一行）
                        if (pendingDuration != null && !trimmedLine.startsWith("#")) {
                            val tsPair = checkSegmentFileLegal(trimmedLine, m3u8ParentDir)
                            val segmentFileExist = tsPair.first
                            val segmentFilePath = tsPair.second
                            val segmentInfo = FFmpegM3u8TsSegmentData(
                                index = currentSegmentIndex,
                                duration = pendingDuration ?: 0f,
                                filePath = segmentFilePath,
                                fileSize = obtainFileSize(segmentFilePath),
                                isFileExist = segmentFileExist
                            )
                            segmentList.add(segmentInfo)
                            // 重置状态，准备解析下一个片段
                            currentSegmentIndex++
                            pendingDuration = null
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val localSegmentCount = segmentList.count { it.isFileExist }
        // 所有ts分片都存在才可用
        val isAvailable = segmentList.isNotEmpty() && localSegmentCount == segmentList.size
        return FFmpegM3u8ResultData(
            m3u8FileName = m3u8FileName,
            m3u8FilePath = m3u8FilePath,
            totalSegmentCount = segmentList.size,
            segmentList = segmentList,
            isStandardM3u8 = isStandardM3u8,
            localSegmentCount = localSegmentCount,
            isAvailable = isAvailable
        )
    }

    /**
     * 纯内存生成m3u8第一帧Bitmap（不生成本地文件）
     * @param m3u8Result 已解析的m3u8信息
     */
    private fun generateM3u8CoverBitmap(m3u8Result: FFmpegM3u8ResultData): Bitmap? {
        // 获取第一个有效ts分片
        val firstTs = m3u8Result.segmentList.firstOrNull { it.isFileExist } ?: return null
        val tsFilePath = File(firstTs.filePath).absoluteFile
        val tempCoverPath = File(
            ToolBoxApplication.app.cacheDir,
            "temp_HToolBox_m3u8_cover_${System.currentTimeMillis()}.jpg"
        ).absolutePath
        // 截取ts第一帧到临时文件
        val cmd = "-i $tsFilePath -frames:v 1 -f image2 -y -s 320x240 $tempCoverPath"
        val session: FFmpegSession = FFmpegKit.execute(cmd)
        return if (ReturnCode.isSuccess(session.returnCode)) {
            val bitmap = BitmapFactory.decodeFile(tempCoverPath)
            File(tempCoverPath).delete()
            bitmap
        } else {
            File(tempCoverPath).delete()
            null
        }
    }

    /**
     * 验证ts(片段)文件在合法性
     * @param tsSegmentFilePath m3u8中解析出的ts路径
     * @param m3u8ParentDir m3u8文件的父目录
     * 返回文件是否存在和这个ts文件的路径
     */
    private fun checkSegmentFileLegal(
        tsSegmentFilePath: String,
        m3u8ParentDir: File?,
    ): Pair<Boolean, String> {
        val tsAbsolutePath = filterSegmentFilePathToAvailablePath(tsSegmentFilePath, m3u8ParentDir)
        if (tsAbsolutePath == "") {
            return Pair(false, "")
        }
        val tsFile = File(tsAbsolutePath)
        return Pair(tsFile.exists() && tsFile.isFile, tsAbsolutePath)
    }

    /**
     * 过滤m3u8文件指定的路径为正常内存路径
     * 处理绝对路径/相对路径/file://协议
     * @param m3u8FileLineSegment m3u8每一行所写的String
     * @param m3u8ParentDir m3u8文件的父目录
     */
    private fun filterSegmentFilePathToAvailablePath(
        m3u8FileLineSegment: String,
        m3u8ParentDir: File?,
    ): String {
        if (m3u8FileLineSegment.isBlank()) return ""
        return if (m3u8FileLineSegment.startsWith("file:///")) {
            m3u8FileLineSegment.replace("file:///", "/")
        } else if (m3u8FileLineSegment.startsWith("/")) {
            m3u8FileLineSegment
        } else if (m3u8ParentDir != null && m3u8ParentDir.exists()) {
            File(m3u8ParentDir, m3u8FileLineSegment).absolutePath
        } else ""
    }

    /**
     * 从 #EXTINF 行提取片段时长
     * 示例：#EXTINF:10.0, → 返回 10.0；#EXTINF:5.5,video → 返回 5.5
     */
    private fun filterSegmentFileDuration(extInfLine: String): Float {
        return try {
            // 截取 #EXTINF: 后的部分，直到第一个逗号
            extInfLine.substringAfter("#EXTINF:")
                .substringBefore(",").trim().toFloat()
        } catch (e: Exception) {
            0f
        }
    }
}