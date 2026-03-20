package com.maunc.toolbox.ffmpeg.viewmodel

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.lifecycle.MutableLiveData
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import com.maunc.base.ext.loge
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.toolbox.commonbase.utils.obtainGlideEngin
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_NONE
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.constant.MP4_TO_GIF_SAVE_PATH
import com.maunc.toolbox.ffmpeg.constant.SAVE_FFMPEG_PREFIX
import com.maunc.toolbox.ffmpeg.constant.SELECT_MP4_TO_GIF_MAX_NUM
import java.io.File
import java.util.Locale

class FFmpegMp4ToGifViewModel : BaseViewModel<BaseModel>() {

    var transStatus = MutableLiveData(FFMPEG_NONE)//转换状态
    var currentSelectMp4File = MutableLiveData<LocalMedia?>(null)//选中的视频对象
    var currentMp4FilePath = MutableLiveData<String?>(null)//目标视频文件路径
    var currentGifFilePath = MutableLiveData<String?>(null)//要转换的目标gif文件路径
    var videoDurationMs = MutableLiveData(0L)//视频总时长
    var clipStartMs = MutableLiveData(0L)//裁剪开始时间
    var clipEndMs = MutableLiveData(0L)//裁剪结束时间

    fun startSelectMp4File(context: Context) {
        PictureSelector.create(context)
            .openGallery(SelectMimeType.ofVideo())
            .setMaxSelectNum(SELECT_MP4_TO_GIF_MAX_NUM)
            .setImageEngine(obtainGlideEngin)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    val localMedia = result?.get(0)
                    currentSelectMp4File.value = localMedia
                    val mp4Path = localMedia?.realPath
                    currentMp4FilePath.value = mp4Path
                    currentGifFilePath.value = obtainToGifPath(mp4Path)
                    val duration = obtainVideoDurationMs(mp4Path)
                    videoDurationMs.value = duration
                    clipStartMs.value = 0L
                    clipEndMs.value = duration
                }

                override fun onCancel() {}
            })
    }

    fun startTransformation() {
        if (currentMp4FilePath.value == null || currentGifFilePath.value == null) {
            return
        }
        val ffmpegCmd = obtainFFmpegMp4ToGif(
            currentMp4FilePath.value!!,
            currentGifFilePath.value!!,
            clipStartMs.value ?: 0L,
            clipEndMs.value ?: 0L,
        )
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
            "FFmpeg日志：${log.message}".loge()
        }, { statistics ->
            "统计信息：$statistics".loge()
        })
    }

    /**
     * -y覆盖已存在文件
     * fps=10+scale=480:-1 在体积与清晰度之间做平衡，适合大部分场景
     */
    private fun obtainFFmpegMp4ToGif(
        mp4Path: String,
        gifPath: String,
        clipStartMs: Long,
        clipEndMs: Long,
    ): String {
        val safeStartMs = clipStartMs.coerceAtLeast(0L)
        val safeEndMs = clipEndMs.coerceAtLeast(safeStartMs + 100L)
        val durationMs = (safeEndMs - safeStartMs).coerceAtLeast(100L)
        val startSec = safeStartMs / 1000f
        val durationSec = durationMs / 1000f
        return String.format(
            Locale.US,
            "-y -ss %.3f -t %.3f -i \"%s\" -vf \"fps=10,scale=480:-1:flags=lanczos\" \"%s\"",
            startSec, durationSec, mp4Path, gifPath,
        )
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

    fun updateClipStartMs(value: Long) {
        val end = clipEndMs.value ?: 0L
        val duration = videoDurationMs.value ?: 0L
        clipStartMs.value = value.coerceIn(0L, duration.coerceAtLeast(0L))
        if ((clipStartMs.value ?: 0L) >= end) {
            clipEndMs.value = (clipStartMs.value ?: 0L) + 100L
        }
    }

    fun updateClipEndMs(value: Long) {
        val start = clipStartMs.value ?: 0L
        val duration = videoDurationMs.value ?: 0L
        clipEndMs.value = value.coerceIn(0L, duration.coerceAtLeast(0L))
        if ((clipEndMs.value ?: 0L) <= start) {
            clipStartMs.value = ((clipEndMs.value ?: 0L) - 100L).coerceAtLeast(0L)
        }
    }


    private fun obtainVideoDurationMs(videoPath: String?): Long {
        if (videoPath.isNullOrEmpty()) return 0L
        val retriever = MediaMetadataRetriever()
        return runCatching {
            retriever.setDataSource(videoPath)
            val durationMs = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull() ?: 0L
            durationMs
        }.getOrElse { 0L }
            .also { retriever.release() }
    }
}

