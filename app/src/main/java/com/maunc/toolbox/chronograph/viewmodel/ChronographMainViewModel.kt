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
import com.maunc.toolbox.commonbase.ext.getDimensFloat
import com.maunc.toolbox.commonbase.ext.loge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat", "DefaultLocale", "ObjectAnimatorBinding", "Recycle")
class ChronographMainViewModel : BaseViewModel<BaseModel>() {

    companion object {
        private class ChronographHandler(looper: Looper) : Handler(looper)
    }

    private var mTimeThread: HandlerThread? = null
    private var mHandler: ChronographHandler? = null

    //当前时间
    var mChronographTimeValue = MutableLiveData(0f)
    var mRunChronographStatus = MutableLiveData(CHRONOGRAPH_STATUS_NONE)
    private var mRankDiffValue = MutableLiveData(0f)
    private var mRankIndex = MutableLiveData(0)
    var mRankChronographData = MutableLiveData<ChronographData>()

    //计时文本是否执行缩放 true放大  false缩小
    var mTimeTvIsScaleAnim = MutableLiveData(false)

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
        mRankDiffValue.value = 0f
        mRankIndex.value = 0
        mTimeTvIsScaleAnim.value = false
        mRankChronographData.value = null
    }

    fun isScaleAnim(): Boolean = mTimeTvIsScaleAnim.value!!

    fun isChronograph(): Boolean = mRunChronographStatus.value!! == CHRONOGRAPH_STATUS_START

    fun handleRankTime() {
        mTimeTvIsScaleAnim.value = true
        mRankDiffValue.value?.let {
            mRankDiffValue.value = mChronographTimeValue.value?.minus(it)
            mRankIndex.value = mRankIndex.value!!.plus(1)
            mRankChronographData.value = ChronographData(
                mRankIndex.value!!,
                formatTime(mChronographTimeValue.value!!),
                formatTime(mRankDiffValue.value!!)
            )
        }
    }

    /**
     * 点击截取当前时间进入排行榜 会有布局变化
     */
    fun startRankAfterUI(textView: TextView, recyclerView: RecyclerView) {
        animateToScale(
            startScale = 1f,
            endScale = 0.8f,
            view = textView,
            startTranslationY = 0f,
            endTranslationY = -getDimensFloat(R.dimen.chronograph_time_tv_translate_y)
        )
        recyclerView.animateToAlpha(startAlpha = 0f, endAlpha = 1f, time = 200)
    }

    /**
     * 恢复所有布局变化
     */
    fun restoreUI(textView: TextView, recyclerView: RecyclerView) {
        animateToScale(
            startScale = 0.8f,
            endScale = 1f,
            view = textView,
            startTranslationY = -getDimensFloat(R.dimen.chronograph_time_tv_translate_y),
            endTranslationY = 0f
        )
        recyclerView.animateToAlpha(
            startAlpha = 1f,
            endAlpha = 0f,
            time = 200
        )
    }

    // 计时文本动画
    fun animateToScale(
        view: View,
        startScale: Float = 1f,
        endScale: Float = 0.8f,
        startTranslationY: Float = 0f,
        endTranslationY: Float = -getDimensFloat(R.dimen.chronograph_time_tv_translate_y),
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

    private fun formatTime(timeValue: Float): String {
        val minutes = (timeValue / 60).toInt() // 转换为分钟
        val remainingSeconds = timeValue % 60 // 计算剩余的秒数
        return String.format("%02d", minutes) + ":" + String.format("%05.2f", remainingSeconds)
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

    fun currentTime() {
        val currentTimeMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
        sdf.format(Date(currentTimeMillis)).loge()
    }

    override fun onCleared() {
        mHandler?.removeCallbacksAndMessages(null)
        mHandler = null
        mTimeThread?.quitSafely()
        mTimeThread = null
        super.onCleared()
    }
}