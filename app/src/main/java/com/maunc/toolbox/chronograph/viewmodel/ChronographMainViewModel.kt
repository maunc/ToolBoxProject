package com.maunc.toolbox.chronograph.viewmodel

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.maunc.toolbox.R
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_NONE
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_START
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_STOP
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_THREAD_NAME
import com.maunc.toolbox.chronograph.constant.DELAY_MILLS
import com.maunc.toolbox.chronograph.constant.SPEED_NUM
import com.maunc.toolbox.chronograph.data.ChronographData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.constant.SCALE_X
import com.maunc.toolbox.commonbase.constant.SCALE_Y
import com.maunc.toolbox.commonbase.constant.TRANSLATION_Y
import com.maunc.toolbox.commonbase.ext.animateToAlpha
import com.maunc.toolbox.commonbase.ext.obtainDimensFloat
import java.util.Locale
import java.util.concurrent.TimeUnit

@SuppressLint("ObjectAnimatorBinding", "Recycle")
class ChronographMainViewModel : BaseViewModel<BaseModel>() {

    companion object {
        private class ChronographHandler(looper: Looper) : Handler(looper)
    }

    private var mTimeThread: HandlerThread? = null
    private var mHandler: ChronographHandler? = null

    //当前时间
    var mChronographTimeValue = MutableLiveData(0f)
    var mRunChronographStatus = MutableLiveData(CHRONOGRAPH_STATUS_NONE)
    private var mRankIndex = MutableLiveData(0)
    var mRankChronographData = MutableLiveData<ChronographData>()

    //计时文本是否执行缩放 true放大  false缩小
    private var mTimeTvIsScaleAnim = MutableLiveData(false)

    private var mRankEnable = MutableLiveData(false)

    private val timeRuntime = object : Runnable {
        override fun run() {
            mHandler?.postDelayed(this, DELAY_MILLS)
            calculateTime()
        }
    }

    private fun calculateTime() {
        mChronographTimeValue.value?.let { plusValue ->
            plusValue.plus(SPEED_NUM).let {
                mChronographTimeValue.postValue(it)
            }
        }
    }

    fun leftControllerChronograph() {
        if (isChronograph()) {
            handleRankTime()
        } else {
            endChronograph()
        }
    }

    fun rightControllerChronograph() {
        if (isChronograph()) {
            stopChronograph()
        } else {
            startChronograph()
        }
    }

    fun startChronograph() {
        mHandler?.post(timeRuntime)
        mRunChronographStatus.value = CHRONOGRAPH_STATUS_START
    }

    fun stopChronograph() {
        mHandler?.removeCallbacksAndMessages(null)
        mRunChronographStatus.value = CHRONOGRAPH_STATUS_STOP
    }

    fun endChronograph() {
        mHandler?.removeCallbacksAndMessages(null)
        mRunChronographStatus.value = CHRONOGRAPH_STATUS_NONE
        mChronographTimeValue.value = 0f
        mRankIndex.value = 0
        mTimeTvIsScaleAnim.value = false
        mRankEnable.value = false
        mRankChronographData.value = null
    }

    fun isScaleAnim(): Boolean = mTimeTvIsScaleAnim.value!!

    fun isChronograph(): Boolean = mRunChronographStatus.value!! == CHRONOGRAPH_STATUS_START

    private fun handleRankTime() {
        mTimeTvIsScaleAnim.value = true
        mRankIndex.value = mRankIndex.value!!.plus(1)
        mRankChronographData.value = ChronographData(
            mRankIndex.value!!,
            mChronographTimeValue.value!!,
            if (mRankEnable.value!!) {
                mChronographTimeValue.value!! - mRankChronographData.value?.time!!
            } else {
                mChronographTimeValue.value!!
            }
        )
        // 第一次点击代表开始计时
        mRankEnable.value = true
    }

    /**
     * 点击截取当前时间进入排行榜 会有布局变化
     */
    fun startRankAfterUI(textView: TextView, recyclerView: RecyclerView) {
        animateToScale(
            startScale = 1f,
            endScale = 0.7f,
            view = textView,
            startTranslationY = 0f,
            endTranslationY = -obtainDimensFloat(R.dimen.chronograph_time_tv_translate_y)
        )
        recyclerView.animateToAlpha(
            startAlpha = 0f,
            endAlpha = 1f,
            time = 200
        )
    }

    /**
     * 恢复布局变化
     */
    fun restoreUI(textView: TextView, recyclerView: RecyclerView) {
        animateToScale(
            startScale = 0.7f,
            endScale = 1f,
            view = textView,
            startTranslationY = -obtainDimensFloat(R.dimen.chronograph_time_tv_translate_y),
            endTranslationY = 0f
        )
        recyclerView.animateToAlpha(
            startAlpha = 1f,
            endAlpha = 0f,
            time = 200
        )
    }

    // 计时文本动画
    private fun animateToScale(
        view: View,
        startScale: Float = 1f,
        endScale: Float = 0.7f,
        startTranslationY: Float = 0f,
        endTranslationY: Float = -obtainDimensFloat(R.dimen.chronograph_time_tv_translate_y),
        time: Long = 200,
    ) {
        val scaleXAnimator = ObjectAnimator.ofFloat(
            view, SCALE_X, startScale, endScale
        ).apply {
            duration = time
        }
        val scaleYAnimator = ObjectAnimator.ofFloat(
            view, SCALE_Y, startScale, endScale
        ).apply {
            duration = time
        }
        val translateYAnimator =
            ObjectAnimator.ofFloat(
                view, TRANSLATION_Y, startTranslationY,
                endTranslationY
            ).apply {
                duration = time
            }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, translateYAnimator)
        animatorSet.start()
    }

    fun initHandler() {
        if (mTimeThread == null || mTimeThread!!.state == Thread.State.TERMINATED) {
            mTimeThread = HandlerThread(CHRONOGRAPH_THREAD_NAME).also {
                it.start()
            }
        }
        mHandler?.removeCallbacksAndMessages(null) ?: kotlin.run {
            mTimeThread?.let {
                mHandler = ChronographHandler(it.looper)
            }
        }
    }

    fun timeUnitMillion(lapSpeedMillions: Long): String { // int * 1000
        val minutes = TimeUnit.MILLISECONDS.toMinutes(lapSpeedMillions)
        val seconds =
            (TimeUnit.MILLISECONDS.toSeconds(lapSpeedMillions) - TimeUnit.MINUTES.toSeconds(minutes))
        val seconds2 = lapSpeedMillions / 1000.0f - (lapSpeedMillions / 1000).toInt()
        val minutesStr = String.format(Locale.getDefault(), "%02d", minutes)
        val secondStr = String.format(Locale.getDefault(), "%05.2f", (seconds + seconds2))
        return "$minutesStr:$secondStr"
    }

    override fun onCleared() {
        mHandler?.removeCallbacksAndMessages(null)
        mHandler = null
        mTimeThread?.quitSafely()
        mTimeThread = null
        super.onCleared()
    }
}