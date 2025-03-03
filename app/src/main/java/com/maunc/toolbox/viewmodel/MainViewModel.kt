package com.maunc.toolbox.viewmodel

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.base.BaseModel
import com.maunc.toolbox.base.BaseViewModel
import com.maunc.toolbox.constant.RUN_STATUS_NONE
import com.maunc.toolbox.constant.RUN_STATUS_START
import com.maunc.toolbox.constant.RUN_STATUS_STOP
import com.maunc.toolbox.constant.TIME_THREAD_NAME
import com.maunc.toolbox.database.table.RandomNameData
import com.maunc.toolbox.ext.getString
import java.util.Random

class MainViewModel : BaseViewModel<BaseModel>() {

    companion object {
        class TimeHandler(looper: Looper) : Handler(looper)
    }

    var runRandomStatus = MutableLiveData(RUN_STATUS_NONE)

    var targetRandomName = MutableLiveData(getString(R.string.random_none_text))

    var runDelayTime = MutableLiveData(15L)

    private var mTimeThread: HandlerThread? = null
    private var mHandler: TimeHandler? = null

    var randomGroupValue = MutableLiveData<List<RandomNameData>>()

    /**
     * 循环任务
     */
    private val runRuntime = object : Runnable {
        override fun run() {
            runDelayTime.value?.let { delay ->
                mHandler?.postDelayed(this, delay)
                //随机点名
                randomGroupValue.value?.let { data ->
                    val nextInt = Random().nextInt(data.size)
                    targetRandomName.postValue(data[nextInt].randomName)
                }
            }
        }
    }

    fun initHandler() {
        if (mTimeThread == null || mTimeThread!!.state == Thread.State.TERMINATED) {
            mTimeThread = HandlerThread(TIME_THREAD_NAME).also { it.start() }
        }
        mHandler?.removeCallbacksAndMessages(null) ?: kotlin.run {
            mTimeThread?.let {
                mHandler = TimeHandler(it.looper)
            }
        }
    }

    fun startRandom() {
        runRandomStatus.value = RUN_STATUS_START
        mHandler?.post(runRuntime)
    }

    fun stopRandom() {
        runRandomStatus.value = RUN_STATUS_STOP
        mHandler?.removeCallbacks(runRuntime)
    }

    fun endRandom() {
        runRandomStatus.value = RUN_STATUS_NONE
        mHandler?.removeCallbacks(runRuntime)
    }

    override fun onCleared() {
        super.onCleared()
        mHandler?.removeCallbacksAndMessages(null)
        mHandler = null
        mTimeThread?.quitSafely()
        mTimeThread = null
    }
}