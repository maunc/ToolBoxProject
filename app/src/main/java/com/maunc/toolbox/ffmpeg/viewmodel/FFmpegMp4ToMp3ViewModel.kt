package com.maunc.toolbox.ffmpeg.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
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
import com.maunc.toolbox.commonbase.utils.createFileDir
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_NONE
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.constant.MP4_TO_MP3_SAVE_PATH
import com.maunc.toolbox.ffmpeg.constant.MP4_TO_MP3_SAVE_PATH_NAME
import com.maunc.toolbox.ffmpeg.constant.SAVE_FFMPEG_PREFIX
import com.maunc.toolbox.ffmpeg.constant.SAVE_ROOT_PATH
import com.maunc.toolbox.ffmpeg.constant.SELECT_VIDEO_MAX_NUM
import java.io.File

class FFmpegMp4ToMp3ViewModel : BaseViewModel<BaseModel>() {

    var transStatus = MutableLiveData(FFMPEG_NONE)//转换状态
    var currentSelectMp4File = MutableLiveData<LocalMedia?>(null)//选中的视频对象
    var currentMp4FilePath = MutableLiveData<String?>(null)//目标视频文件路径
    var currentMp3FilePath = MutableLiveData<String?>(null)//要转换的目标音频文件路径

    fun startSelectMp4File(context: Context) {
        PictureSelector.create(context)
            .openGallery(SelectMimeType.ofVideo())
            .setMaxSelectNum(SELECT_VIDEO_MAX_NUM)
            .setImageEngine(obtainGlideEngin)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    currentSelectMp4File.value = result?.get(0)
                    currentMp4FilePath.value = result?.get(0)?.realPath
                    currentMp3FilePath.value = obtainToMp3Path(result?.get(0)?.realPath)
                }

                override fun onCancel() {}
            })
    }

    fun startTransformation() {
        if (currentMp4FilePath.value == null && currentMp3FilePath.value == null) {
            return
        }
        val ffmpegCmd = obtainFFmpegMp4ToMp3(currentMp4FilePath.value!!, currentMp3FilePath.value!!)
        transStatus.value = FFMPEG_START
        FFmpegKit.executeAsync(ffmpegCmd, { session: FFmpegSession ->
            runOnUiThread {
                val returnCode = session.returnCode
                if (ReturnCode.isSuccess(returnCode)) {
                    "MP4 TO MP3 Conversion successful".loge()
                    transStatus.value = FFMPEG_SUCCESS
                } else if (ReturnCode.isCancel(returnCode)) {
                    "MP4 TO MP3 Conversion canceled ${session.logs}".loge()
                } else {
                    "MP4 TO MP3 Conversion failed ${session.logs}".loge()
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

    /**
     * -y是覆盖已有的mp3文件
     */
    private fun obtainFFmpegMp4ToMp3(mp4Path: String, mp3Path: String): String {
        return "-y -i $mp4Path -vn -acodec libmp3lame -b:a 192k $mp3Path"
    }

    fun obtainToMp3Path(mp4Path: String?): String? {
        if (mp4Path == null) return null
        val videoFile = File(mp4Path)
        val fileNameWithExt = videoFile.name
        val fileNameWithoutExt = if (fileNameWithExt.contains(".")) {
            fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf("."))
        } else {
            fileNameWithExt
        }
        val mp3FileName = "$SAVE_FFMPEG_PREFIX$fileNameWithoutExt.mp3"
        return File(MP4_TO_MP3_SAVE_PATH, mp3FileName).absolutePath
    }
}