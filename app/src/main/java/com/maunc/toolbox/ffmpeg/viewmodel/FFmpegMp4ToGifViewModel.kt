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
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_NONE
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.constant.MP4_TO_GIF_SAVE_PATH
import com.maunc.toolbox.ffmpeg.constant.SAVE_FFMPEG_PREFIX
import com.maunc.toolbox.ffmpeg.constant.SELECT_MP4_TO_GIF_MAX_NUM
import java.io.File

class FFmpegMp4ToGifViewModel : BaseViewModel<BaseModel>() {

    var transStatus = MutableLiveData(FFMPEG_NONE)//转换状态
    var currentSelectMp4File = MutableLiveData<LocalMedia?>(null)//选中的视频对象
    var currentMp4FilePath = MutableLiveData<String?>(null)//目标视频文件路径
    var currentGifFilePath = MutableLiveData<String?>(null)//要转换的目标gif文件路径

    fun startSelectMp4File(context: Context) {
        PictureSelector.create(context)
            .openGallery(SelectMimeType.ofVideo())
            .setMaxSelectNum(SELECT_MP4_TO_GIF_MAX_NUM)
            .setImageEngine(obtainGlideEngin)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    currentSelectMp4File.value = result?.get(0)
                    currentMp4FilePath.value = result?.get(0)?.realPath
                    currentGifFilePath.value = obtainToGifPath(result?.get(0)?.realPath)
                }

                override fun onCancel() {}
            })
    }

    fun startTransformation() {
        if (currentMp4FilePath.value == null && currentGifFilePath.value == null) {
            return
        }
        val ffmpegCmd = obtainFFmpegMp4ToGif(currentMp4FilePath.value!!, currentGifFilePath.value!!)
        transStatus.value = FFMPEG_START
        FFmpegKit.executeAsync(ffmpegCmd, { session: FFmpegSession ->
            runOnUiThread {
                val returnCode = session.returnCode
                if (ReturnCode.isSuccess(returnCode)) {
                    "MP4 TO GIF Conversion successful".loge()
                    transStatus.value = FFMPEG_SUCCESS
                } else if (ReturnCode.isCancel(returnCode)) {
                    "MP4 TO GIF Conversion canceled ${session.logs}".loge()
                } else {
                    "MP4 TO GIF Conversion failed ${session.logs}".loge()
                    transStatus.value = FFMPEG_ERROR
                }
            }
        }, { log ->
            "FFmpeg日志：${log.message}".logd()
        }, { statistics ->
            "统计信息：$statistics".logd()
        })
    }

    /**
     * -y覆盖已存在文件
     * fps=10+scale=480:-1 在体积与清晰度之间做平衡，适合大部分场景
     */
    private fun obtainFFmpegMp4ToGif(mp4Path: String, gifPath: String): String {
        return "-y -i \"$mp4Path\" -vf \"fps=10,scale=480:-1:flags=lanczos\" \"$gifPath\""
    }

    fun obtainToGifPath(mp4Path: String?): String? {
        if (mp4Path == null) return null
        val videoFile = File(mp4Path)
        val fileNameWithExt = videoFile.name
        val fileNameWithoutExt = if (fileNameWithExt.contains(".")) {
            fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf("."))
        } else {
            fileNameWithExt
        }
        val gifFileName = "$SAVE_FFMPEG_PREFIX$fileNameWithoutExt.gif"
        return File(MP4_TO_GIF_SAVE_PATH, gifFileName).absolutePath
    }
}

