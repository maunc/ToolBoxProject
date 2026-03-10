package com.maunc.videoplayer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CustomVideoPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : StandardGSYVideoPlayer(context, attrs), CustomVideoLifecycle {

    companion object {
        const val TAG = "MauncCustomVideoPlayer"
    }

    val mCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var customControllerImageView: AppCompatImageView? = null//播放暂停
    private var customSeekBar: SeekBar? = null//进度条
    private var customTitleTextView: AppCompatTextView? = null//标题
    private var customSpeedTextView: AppCompatTextView? = null//倍速
    private var customCurrentTimeTextView: AppCompatTextView? = null//当前时间
    private var customMaxTimeTextView: AppCompatTextView? = null//最大时间

    override fun init(context: Context?) {
        super.init(context)
        customControllerImageView = findViewById(R.id.custom_video_controller_iv)
        customSeekBar = findViewById(R.id.custom_video_seekbar)
        customTitleTextView = findViewById(R.id.custom_video_title_tv)
        customSpeedTextView = findViewById(R.id.custom_video_speed_tv)
        customCurrentTimeTextView = findViewById(R.id.custom_video_current_time_tv)
        customMaxTimeTextView = findViewById(R.id.custom_video_max_time_tv)

        isAutoFullWithSize = false
        setVideoStatusListener(
            onPrepared = {
                customSeekBar?.max = 100
                customMaxTimeTextView?.text = stringForTime(gsyVideoManager.duration)
            },
            onProgress = { progress, secProgress, currentTime, totalTime ->
                Log.e(TAG, "当前进度:${progress},${secProgress},${currentTime},${totalTime}")
                customCurrentTimeTextView?.text = stringForTime(currentTime)
                customSeekBar?.progress = ((currentTime * 100.0) / totalTime).toInt()
            },
            onComplete = { url, isAuto ->
                customControllerImageView?.setImageResource(R.drawable.icon_pause)
                customCurrentTimeTextView?.text = customMaxTimeTextView?.text
                customSeekBar?.progress = 100
                onCompleteListener?.onComplete()
            }
        )
        initViewEventListener()
    }

    private fun initViewEventListener() {
        customControllerImageView?.setOnClickListener {
            if (isPlaying()) {
                pausePlay()
            } else {
                resumePlay()
            }
        }

        customSeekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newPosition = (duration * progress) / 100
                    customCurrentTimeTextView?.text = stringForTime(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                gsyVideoManager.seekTo((duration * (seekBar?.progress ?: 0)) / 100)
            }
        })
    }

    fun goneSpeedView() = customSpeedTextView?.gone()

    fun isPlaying() = gsyVideoManager.isPlaying

    fun setNewVideoPlay(
        videoUrl: String?,
        title: String = "",
        cacheWithPlay: Boolean = true,
    ) {
        if (videoUrl == null) return
        setTitle(title)
        setUp(videoUrl, cacheWithPlay, title)
    }

    fun playVideoPlay() {
        startPlayLogic()
        customControllerImageView?.setImageResource(R.drawable.icon_play)
    }

    private fun reStartVideoPlay() {
        if (mOriginUrl == null || mOriginUrl == "") {
            return
        }
        playVideoPlay()
    }

    private fun pausePlay() {
        if (!gsyVideoManager.isPlaying) return
        gsyVideoManager.pause()
        customControllerImageView?.setImageResource(R.drawable.icon_pause)
    }

    private fun resumePlay() {
        if (!currentPlayer.isInPlayingState) {
            reStartVideoPlay()
        }
        if (!gsyVideoManager.isPlaying) {
            gsyVideoManager.start()
            customControllerImageView?.setImageResource(R.drawable.icon_play)
        }
    }

    /**
     * 设置标题
     */
    fun setTitle(title: String) {
        if (title == "") return
        customTitleTextView?.visible()
        customTitleTextView?.setTextOrMarquee(title)
    }

    /**
     * GSYVideoType.SCREEN_TYPE_FULL
     * GSYVideoType.SCREEN_MATCH_FULL
     * GSYVideoType.SCREEN_TYPE_CUSTOM
     * GSYVideoType.SCREEN_TYPE_4_3
     * GSYVideoType.SCREEN_TYPE_16_9
     * GSYVideoType.SCREEN_TYPE_18_9
     */
    fun setScaleType(type: Int) {
        GSYVideoType.setShowType(type)
    }

    /**
     * 重写布局以实现自定义布局
     */
    override fun getLayoutId(): Int = R.layout.custom_video_layout

    override fun touchLongPress(e: MotionEvent?) {
        Log.d(TAG, "touchLongPress,${e}")
    }

    override fun touchDoubleUp(e: MotionEvent?) {
        Log.d(TAG, "touchDoubleUp,${e}")
    }

    override fun touchSurfaceUp() {
        Log.d(TAG, "touchSurfaceUp")
    }

    override fun touchSurfaceDown(x: Float, y: Float) {
        Log.d(TAG, "touchSurfaceDown,x:${x},y:${y}")

    }

    override fun touchSurfaceMove(deltaX: Float, deltaY: Float, y: Float) {
        Log.d(TAG, "touchSurfaceMove,deltaX:${deltaX},deltaY:${deltaY},y:${y}")
    }

    override fun onPause() {
        pausePlay()
    }

    override fun onResume() {
        resumePlay()
    }

    override fun onDestroy() {
        release()
        gsyVideoManager.releaseMediaPlayer()
    }

    private var onCompleteListener: OnCompleteListener? = null

    fun setOnCompleteListener(onCompleteListener: OnCompleteListener) {
        this.onCompleteListener = onCompleteListener
    }

    fun interface OnCompleteListener {
        fun onComplete()
    }
}