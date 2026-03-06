package com.maunc.videoplayer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

/**
 * GSYVideoType.setShowType(GSYVideoType.)设置视频比例
 */
class MauncCustomVideoPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : StandardGSYVideoPlayer(context, attrs) {

    companion object {
        const val TAG = "MauncCustomVideoPlayer"
    }

    //播放暂停
    private var customControllerIv: AppCompatImageView? = null

    override fun init(context: Context?) {
        super.init(context)
        customControllerIv = findViewById(R.id.custom_video_controller_iv)

        //初始化所有View的事件
        initViewEventListener()
    }

    private fun initViewEventListener() {
        customControllerIv?.setOnClickListener {
            if (isPlaying()) {
                pausePlay()
            } else {
                resumePlay()
            }
        }
    }

    fun isPlaying() = gsyVideoManager.isPlaying

    fun startVideoPlay(
        videoUrl: String?,
        title: String = "",
        cacheWithPlay: Boolean = true,
    ) {
        if (videoUrl == null) return
        setUp(videoUrl, cacheWithPlay, title)
        startPlayLogic()
    }

    fun pausePlay() {
        if (!gsyVideoManager.isPlaying) return
        customControllerIv?.setImageResource(R.drawable.icon_pause)
        gsyVideoManager.pause()
    }

    fun resumePlay() {
        if (gsyVideoManager.isPlaying) return
        gsyVideoManager.start()
    }

    /**
     * 重写布局以实现自定义布局
     */
    override fun getLayoutId(): Int {
        return R.layout.custom_video_layout
    }

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
}