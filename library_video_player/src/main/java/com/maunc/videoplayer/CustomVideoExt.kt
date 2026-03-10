package com.maunc.videoplayer

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import java.util.Formatter
import java.util.Locale

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun TextView.setTextOrMarquee(text: String) {
    this.text = text
    ellipsize = TextUtils.TruncateAt.MARQUEE
    marqueeRepeatLimit = -1
    isSingleLine = true
    isFocusable = true
    isSelected = true
    isFocusableInTouchMode = true
}

fun stringForTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000L
    val seconds = totalSeconds % 60L
    val minutes = totalSeconds / 60L % 60L
    val hours = totalSeconds / 3600L
    val stringBuilder = StringBuilder()
    val mFormatter = Formatter(stringBuilder, Locale.getDefault())
    return if (hours > 0L) mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
        .toString() else mFormatter.format("%02d:%02d", minutes, seconds).toString()
}

fun StandardGSYVideoPlayer.setVideoStatusListener(
    onPrepared: (String?) -> Unit = {},
    onProgress: (Long, Long, Long, Long) -> Unit = { progress, secProgress, currentTime, totalTime -> },
    onComplete: (String?, Boolean) -> Unit = { url, isAuto -> },
) {
    setGSYVideoProgressListener { progress, secProgress, currentTime, totalTime ->
        onProgress.invoke(progress, secProgress, currentTime, totalTime)
    }
    setGSYStateUiListener {

    }
    setVideoAllCallBack(object : VideoAllCallBack {
        override fun onStartPrepared(url: String?, vararg titleAndGSYVideoView: Any?) {}

        override fun onPrepared(url: String?, vararg titleAndGSYVideoView: Any?) {
            onPrepared.invoke(url)
        }

        override fun onClickStartIcon(p0: String?, vararg p1: Any?) {}

        override fun onClickStartError(p0: String?, vararg p1: Any?) {}

        override fun onClickStop(p0: String?, vararg p1: Any?) {}

        override fun onClickStopFullscreen(p0: String?, vararg p1: Any?) {}

        override fun onClickResume(p0: String?, vararg p1: Any?) {}

        override fun onClickResumeFullscreen(p0: String?, vararg p1: Any?) {}

        override fun onClickSeekbar(p0: String?, vararg p1: Any?) {}

        override fun onClickSeekbarFullscreen(p0: String?, vararg p1: Any?) {}

        override fun onAutoComplete(url: String?, vararg titleAndGSYVideoView: Any?) {
            onComplete.invoke(url, true)
        }

        override fun onComplete(url: String?, vararg titleAndGSYVideoView: Any?) {
            onComplete.invoke(url, false)
        }

        override fun onEnterFullscreen(p0: String?, vararg p1: Any?) {}

        override fun onQuitFullscreen(p0: String?, vararg p1: Any?) {}

        override fun onQuitSmallWidget(p0: String?, vararg p1: Any?) {}

        override fun onEnterSmallWidget(p0: String?, vararg p1: Any?) {}

        override fun onTouchScreenSeekVolume(p0: String?, vararg p1: Any?) {}

        override fun onTouchScreenSeekPosition(p0: String?, vararg p1: Any?) {}

        override fun onTouchScreenSeekLight(p0: String?, vararg p1: Any?) {}

        override fun onPlayError(p0: String?, vararg p1: Any?) {}

        override fun onClickStartThumb(p0: String?, vararg p1: Any?) {}

        override fun onClickBlank(url: String?, vararg titleAndGSYVideoView: Any?) {}

        override fun onClickBlankFullscreen(p0: String?, vararg p1: Any?) {}
    })
}