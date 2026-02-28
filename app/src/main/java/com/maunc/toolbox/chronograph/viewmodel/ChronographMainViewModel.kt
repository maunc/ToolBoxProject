package com.maunc.toolbox.chronograph.viewmodel

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.maunc.toolbox.R
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_NONE
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_PAUSE
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_START
import com.maunc.toolbox.chronograph.constant.DELAY_MILLS
import com.maunc.toolbox.chronograph.data.ChronographData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.constant.SCALE_X
import com.maunc.toolbox.commonbase.constant.SCALE_Y
import com.maunc.toolbox.commonbase.constant.TRANSLATION_Y
import com.maunc.toolbox.commonbase.ext.animateToAlpha
import com.maunc.toolbox.commonbase.ext.obtainDimensFloat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@SuppressLint("ObjectAnimatorBinding", "Recycle")
class ChronographMainViewModel : BaseViewModel<BaseModel>() {
    private var mChronographJob: Job? = null
    private val _mChronographTimeValue = MutableStateFlow(0L)
    val mChronographTimeValue: StateFlow<Long> = _mChronographTimeValue.asStateFlow()//当前时间
    var mRunChronographStatus = MutableLiveData(CHRONOGRAPH_STATUS_NONE)// 当前计时状态
    private var mPauseStatus = MutableLiveData(true) //是否为暂停,区别停止和暂停用的
    private var mStartChronographTime = MutableLiveData(SystemClock.elapsedRealtime())//开始时的时间
    var mTimeTvIsScaleAnim = MutableLiveData(false)//计时文本是否执行缩放 true放大  false缩小
    private var mRankEnable = MutableLiveData(false)//当前计时是否开始排名
    private var mRankIndex = MutableLiveData(0)//当前排名的名次
    var mRankChronographData = MutableLiveData<ChronographData?>()//当前排名的信息

    // 是否开始计时
    fun isChronograph(): Boolean = mRunChronographStatus.value!! == CHRONOGRAPH_STATUS_START

    private fun enableChronographJob() {
        mChronographJob?.cancel()
        mChronographJob = viewModelScope.launch {
            while (isActive) {
                val totalElapsedMs = SystemClock.elapsedRealtime() - mStartChronographTime.value!!
                _mChronographTimeValue.value = totalElapsedMs
                delay(DELAY_MILLS)
            }
        }
    }

    private fun startChronometer() {
        mRunChronographStatus.value = CHRONOGRAPH_STATUS_START
        mStartChronographTime.value = SystemClock.elapsedRealtime()
        enableChronographJob()
    }

    private fun pauseChronometer() {
        mRunChronographStatus.value = CHRONOGRAPH_STATUS_PAUSE
        mPauseStatus.value = true
        mChronographJob?.cancel()
    }

    private fun resumeChronometer() {
        mRunChronographStatus.value = CHRONOGRAPH_STATUS_START
        val elapsedTime = SystemClock.elapsedRealtime() - mStartChronographTime.value!!
        mStartChronographTime.value = SystemClock.elapsedRealtime() - elapsedTime
        enableChronographJob()
    }

    private fun stopChronometer() {
        mRunChronographStatus.value = CHRONOGRAPH_STATUS_NONE
        _mChronographTimeValue.value = 0L
        mChronographJob?.cancel()
        mPauseStatus.value = false
        mRankIndex.value = 0
        mTimeTvIsScaleAnim.value = false
        mRankEnable.value = false
        mRankChronographData.value = null
    }

    /**
     * 左按钮事件
     */
    fun leftControllerChronograph() {
        if (isChronograph()) {
            handleRankTime()
        } else {
            stopChronometer()
        }
    }

    /**
     * 左按钮事件
     */
    fun rightControllerChronograph() {
        if (isChronograph()) {
            pauseChronometer()
        } else {
            if (mPauseStatus.value!!) {
                resumeChronometer()
            } else {
                startChronometer()
            }
        }
    }

    /**
     * 中间按钮事件
     */
    fun middleControllerChronograph() = startChronometer()

    /**
     * 插入排名
     */
    private fun handleRankTime() {
        mTimeTvIsScaleAnim.value = true
        mRankIndex.value = mRankIndex.value!!.plus(1)
        mRankChronographData.value = ChronographData(
            mRankIndex.value!!,
            mChronographTimeValue.value,
            if (mRankEnable.value!!) {
                mChronographTimeValue.value - mRankChronographData.value?.time!!
            } else {
                mChronographTimeValue.value
            }
        )
        // 第一次点击代表开始排名
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

    /**
     * 计时文本动画
     */
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
}