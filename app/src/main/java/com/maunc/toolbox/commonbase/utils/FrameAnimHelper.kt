package com.maunc.toolbox.commonbase.utils

import android.content.res.TypedArray
import android.widget.ImageView
import com.maunc.toolbox.ToolBoxApplication

class FrameAnimHelper() {
    companion object {
        @JvmStatic
        val SELECTED_A: Int = 1

        @JvmStatic
        val SELECTED_B: Int = 2

        @JvmStatic
        val SELECTED_C: Int = 3

        @JvmStatic
        val SELECTED_D: Int = 4
    }

    private var mAnimationListener: AnimationListener? = null

    private var mImageView: ImageView? = null
    private lateinit var mFrameRes: IntArray
    private var defRes = 0
    private var mDuration = 0 //每帧动画的播放间隔
    private lateinit var mDurations: IntArray //每帧动画的播放间隔数组
    private var mLastFrame = 0
    private var mIsRepeat = false
    private var mDelay = 0 //下一遍动画播放的延迟时间

    private var mCurrentSelect = 0
    private var mCurrentFrame = 0
    private var mNext = false
    private var mPause = false

    constructor(
        iv: ImageView?,
        frameRes: Int,
        defRes: Int,
        duration: Int,
        isRepeat: Boolean,
    ) : this() {
        this.mImageView = iv
        this.mFrameRes = getRes(frameRes)
        this.defRes = defRes
        this.mDuration = duration
        this.mLastFrame = mFrameRes.size - 1
        this.mIsRepeat = isRepeat
//        play(0)
    }

    constructor(
        iv: ImageView?,
        frameRes: Int,
        defRes: Int,
        durations: IntArray,
        isRepeat: Boolean,
    ) : this() {
        this.mImageView = iv
        this.mFrameRes = getRes(frameRes)
        this.defRes = defRes
        this.mDurations = durations
        this.mLastFrame = mFrameRes.size - 1
        this.mIsRepeat = isRepeat
//        playByDurations(0)
    }

    constructor(
        iv: ImageView?,
        frameRes: Int,
        defRes: Int,
        duration: Int,
        delay: Int,
    ) : this() {
        this.mImageView = iv
        this.mFrameRes = getRes(frameRes)
        this.defRes = defRes
        this.mDuration = duration
        this.mDelay = delay
        this.mLastFrame = mFrameRes.size - 1
//        playAndDelay(0)
    }

    constructor(
        iv: ImageView?,
        frameRes: Int,
        defRes: Int,
        durations: IntArray,
        delay: Int,
    ) : this() {
        this.mImageView = iv
        this.mFrameRes = getRes(frameRes)
        this.defRes = defRes
        this.mDurations = durations
        this.mDelay = delay
        this.mLastFrame = mFrameRes.size - 1
//        playByDurationsAndDelay(0)
    }

    fun setAnimationListener(listener: AnimationListener?) {
        this.mAnimationListener = listener
    }

    fun restartAnimation() {
        if (mPause) {
            mPause = false
            when (mCurrentSelect) {
                SELECTED_A -> playByDurationsAndDelay(mCurrentFrame)
                SELECTED_B -> playAndDelay(mCurrentFrame)
                SELECTED_C -> playByDurations(mCurrentFrame)
                SELECTED_D -> play(mCurrentFrame)
                else -> {}
            }
        }
    }

    fun pause() {
        this.mPause = true
    }

    fun release() {
        pause()
        mCurrentFrame = 0
        mImageView?.setImageResource(defRes)
    }

    fun isPause(): Boolean = this.mPause

    /**
     * 获取需要播放的动画资源
     */
    private fun getRes(frameRes: Int): IntArray {
        val typedArray: TypedArray = ToolBoxApplication.app.resources.obtainTypedArray(frameRes)
        val len = typedArray.length()
        val resId = IntArray(len)
        for (i in 0 until len) {
            resId[i] = typedArray.getResourceId(i, -1)
        }
        typedArray.recycle()
        return resId
    }

    fun play(i: Int) {
        mImageView?.postDelayed({
            if (mPause) {
                mCurrentSelect = SELECTED_D
                mCurrentFrame = i
                return@postDelayed
            }
            if (0 == i) {
                mAnimationListener?.onAnimationStart()
            }
            mImageView?.setImageResource(mFrameRes[i])
            if (i == mLastFrame) {
                if (mIsRepeat) {
                    mAnimationListener?.onAnimationRepeat()
                    play(0)
                } else {
                    mAnimationListener?.onAnimationEnd()
                }
            } else {
                play(i + 1)
            }
        }, mDuration.toLong())
    }

    fun playByDurations(i: Int) {
        mImageView?.postDelayed({
            if (mPause) {
                mCurrentSelect = SELECTED_C
                mCurrentFrame = i
                return@postDelayed
            }
            if (0 == i) {
                mAnimationListener?.onAnimationStart()
            }
            mImageView?.setImageResource(mFrameRes[i])
            if (i == mLastFrame) {
                if (mIsRepeat) {
                    mAnimationListener?.onAnimationRepeat()
                    playByDurations(0)
                } else {
                    mAnimationListener?.onAnimationEnd()
                }
            } else {
                playByDurations(i + 1)
            }
        }, mDurations[i].toLong())
    }

    fun playAndDelay(i: Int) {
        mImageView?.postDelayed({
            if (mPause) {
                mCurrentSelect = SELECTED_B
                mCurrentFrame = i
                return@postDelayed
            }
            mNext = false
            if (0 == i) {
                mAnimationListener?.onAnimationStart()
            }
            mImageView?.setImageResource(mFrameRes[i])
            if (i == mLastFrame) {
                mAnimationListener?.onAnimationRepeat()
                mNext = true
                playAndDelay(0)
            } else {
                playAndDelay(i + 1)
            }
        }, (if (mNext && mDelay > 0) mDelay else mDuration).toLong())
    }

    fun playByDurationsAndDelay(i: Int) {
        mImageView?.postDelayed({
            if (mPause) {   // 暂停和播放需求
                mCurrentSelect = SELECTED_A
                mCurrentFrame = i
                return@postDelayed
            }
            if (0 == i) {
                mAnimationListener?.onAnimationStart()
            }
            mImageView?.setImageResource(mFrameRes[i])
            if (i == mLastFrame) {
                mAnimationListener?.onAnimationRepeat()
                mNext = true
                playByDurationsAndDelay(0)
            } else {
                playByDurationsAndDelay(i + 1)
            }
        }, (if (mNext && mDelay > 0) mDelay else mDurations[i]).toLong())
    }

    interface AnimationListener {
        fun onAnimationStart()

        fun onAnimationEnd()

        fun onAnimationRepeat()
    }
}