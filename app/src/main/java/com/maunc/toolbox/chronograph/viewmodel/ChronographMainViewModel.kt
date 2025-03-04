package com.maunc.toolbox.chronograph.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_THREAD_NAME
import com.maunc.toolbox.chronograph.constant.DELAY_MILLS
import com.maunc.toolbox.chronograph.constant.SPEED_NUM
import com.maunc.toolbox.chronograph.data.ChronographData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.loge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat", "DefaultLocale")
class ChronographMainViewModel : BaseViewModel<BaseModel>() {

    companion object {
        private class ChronographHandler(looper: Looper) : Handler(looper)
    }

    private var mTimeThread: HandlerThread? = null
    private var mHandler: ChronographHandler? = null

    //当前时间
    var mChronographTimeValue = MutableLiveData(0f)
    private var mRunChronographStatus = MutableLiveData<Boolean>()
    private var mRankDiffValue = MutableLiveData(0f)
    private var mRankIndex = MutableLiveData(0)
    var mRankChronographData = MutableLiveData<ChronographData>()
    var showControllerLayout = MutableLiveData(true)

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

    fun startChronograph() {
        mHandler?.post(timeRuntime)
        mRunChronographStatus.value = true
        showControllerLayout.value = false
    }

    fun stopChronograph() {
        mHandler?.removeCallbacks(timeRuntime)
        mRunChronographStatus.value = false
    }

    fun endChronograph() {
        mHandler?.removeCallbacks(timeRuntime)
        mRunChronographStatus.value = false
        showControllerLayout.value = true
        mChronographTimeValue.value = 0f
        mRankDiffValue.value = 0f
        mRankIndex.value = 0
        mRankChronographData.value = null
    }

    fun handleRankTime() {
        mRankDiffValue.value?.let {
            mRankDiffValue.value = mChronographTimeValue.value?.minus(it)
            mRankChronographData.value = ChronographData(
                mRankIndex.value!!.plus(1),
                formatTime(mChronographTimeValue.value!!),
                formatTime(mRankDiffValue.value!!)
            )
        }
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