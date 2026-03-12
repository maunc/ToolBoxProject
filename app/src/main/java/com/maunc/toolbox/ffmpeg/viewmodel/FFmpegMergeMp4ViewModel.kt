package com.maunc.toolbox.ffmpeg.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.logd
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.obtainGlideEngin
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_NONE
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.constant.MERGE_MP4_SAVE_PATH
import com.maunc.toolbox.ffmpeg.constant.SELECT_MERGE_MP4_MAX_NUM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException

class FFmpegMergeMp4ViewModel : BaseViewModel<BaseModel>() {

    var transStatus = MutableLiveData(FFMPEG_NONE)//转换状态
    var currentSelectList = mutableListOf<LocalMedia>() //中转视频列表
    var targetSelectList = MutableLiveData<MutableList<LocalMedia>>()//最终视频列表
    var playCurrentIndex = 0 //当前播放下标

    private var unificationVideoList = mutableListOf<String>() // 统一所有合并视频的格式
    private var tempMp4TextFilePath = "" // 临时文件(记录合并的视频列表)

    fun startSelectMp4File(context: Context) {
        PictureSelector.create(context)
            .openGallery(SelectMimeType.ofVideo())
            .setMaxSelectNum(SELECT_MERGE_MP4_MAX_NUM)
            .setImageEngine(obtainGlideEngin)
            .setSelectedData(currentSelectList)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    currentSelectList.clear()
                    currentSelectList.addAll(result ?: mutableListOf())
                    targetSelectList.postValue(currentSelectList)
                }

                override fun onCancel() {}
            })
    }

    fun startMergeMp4List(fileName: String = "aaa") {
        if (targetSelectList.value == null) return
        if (targetSelectList.value!!.isEmpty()) return
        transStatus.value = FFMPEG_START
        viewModelScope.launch(Dispatchers.IO) {
            targetSelectList.value!!.forEach { video ->
                val async = async {
                    unificationVideoParameter(
                        video.realPath, MERGE_MP4_SAVE_PATH, video.width, video.height
                    )
                }.await()
                unificationVideoList.add(async)
                val file = async {
                    createVideoTextTemp()
                }.await()
                tempMp4TextFilePath = file?.absolutePath ?: ""
            }
            if (tempMp4TextFilePath == "") return@launch
            val ffmpegCmd = obtainFFmpegMergeMp4Cmd(fileName)
            FFmpegKit.executeAsync(ffmpegCmd, { session: FFmpegSession ->
                runOnUiThread {
                    val returnCode = session.returnCode
                    if (ReturnCode.isSuccess(returnCode)) {
                        "Merge MP4 Conversion successful".loge()
                        transStatus.value = FFMPEG_SUCCESS
                        deleteVideoTextTempAndNormalVideo()
                    } else if (ReturnCode.isCancel(returnCode)) {
                        "Merge MP4 Conversion canceled ${session.logs}".loge()
                    } else {
                        "Merge MP4 Conversion failed ${session.logs}".loge()
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
    }

    private fun obtainFFmpegMergeMp4Cmd(fileName: String = "HTool_Merge_Video"): String {
        return "-y -f concat -safe 0 -i $tempMp4TextFilePath " +
                "-c:v mpeg4 -b:v 5000k -q:v 2 " +  // 去掉-r 30，避免参数冲突
                "-vf fps=30 " +                    // 用fps滤镜替代-r，兼容vsync 0
                "-c:a aac -b:a 192k " +
                "-fflags +genpts+igndts -async 1 -vsync 0 " +
                "-movflags +faststart " +
                File(MERGE_MP4_SAVE_PATH, "${fileName}.mp4").absolutePath
    }

    private fun createVideoTextTemp(): File? {
        val file = File(MERGE_MP4_SAVE_PATH, "video_text_temp.text")
        try {
            FileWriter(file).use { writer ->
                for (path in unificationVideoList) {
                    writer.write("file '${path}'\n")
                }
            }
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun deleteVideoTextTempAndNormalVideo() {
        if (tempMp4TextFilePath == "") return
        try {
            val file = File(tempMp4TextFilePath)
            if (file.exists()) file.delete()
            val targetDir = File(MERGE_MP4_SAVE_PATH)
            if (!targetDir.exists() || !targetDir.isDirectory) return
            val files = targetDir.listFiles()
            if (files == null || files.isEmpty()) return
            for (fileData in files) {
                if (fileData.isFile && fileData.name.contains("normal_")) {
                    fileData.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun unificationVideoParameter(
        sourcePath: String, outputDir: String, width: Int, height: Int,
    ): String {
        val fileName = "normal_${System.currentTimeMillis()}.mp4"
        val outputPath = File(outputDir, fileName).absolutePath
        val normalizeCmd = if (width > height) {// 是横屏
            "-i $sourcePath " +
                    "-s 1920x1080 -r 30 -c:v mpeg4 " +
                    "-b:v 5000k -pix_fmt yuv420p -c:a aac -ar 44100 -ac 2 " +
                    outputPath
        } else {
            "-i $sourcePath " +
                    "-vf \"scale=-1:1080,pad=1920:1080:(ow-iw)/2:(oh-ih)/2:black\" -r 30 -c:v mpeg4 " +
                    "-b:v 5000k -pix_fmt yuv420p -c:a aac -ar 44100 -ac 2 " +
                    outputPath
        }
        val execute = FFmpegKit.execute(normalizeCmd)
        return if (ReturnCode.isSuccess(execute.returnCode)) outputPath else ""
    }
}