package com.maunc.toolbox.randomname.viewmodel

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomSpeed
import com.maunc.toolbox.randomname.constant.RANDOM_NAME_THREAD_NAME
import com.maunc.toolbox.randomname.constant.RUN_STATUS_NONE
import com.maunc.toolbox.randomname.constant.RUN_STATUS_START
import com.maunc.toolbox.randomname.constant.RUN_STATUS_STOP
import com.maunc.toolbox.randomname.database.table.RandomNameData
import java.util.Random

class RandomNameMainViewModel : BaseRandomNameViewModel<BaseModel>() {

    companion object {
        class RandomNameHandler(looper: Looper) : Handler(looper)
    }

    var runRandomStatus = MutableLiveData(RUN_STATUS_NONE)

    var targetRandomName = MutableLiveData(obtainString(R.string.random_none_text))

    var runDelayTime = MutableLiveData(obtainMMKV.getLong(randomSpeed))

    var transitRandomName = MutableLiveData(GLOBAL_NONE_STRING)

    private var mTimeThread: HandlerThread? = null
    private var mHandler: RandomNameHandler? = null

    var randomGroupValue = MutableLiveData<List<RandomNameData>>()

    /**
     * 循环任务
     */
    private val runRuntime = object : Runnable {
        override fun run() {
            runDelayTime.value?.let { delay ->
                //随机点名
                randomGroupValue.value?.let { data ->
                    while (true) {
                        val nextInt = Random().nextInt(data.size)
                        if (data[nextInt].randomName != transitRandomName.value) {
                            targetRandomName.postValue(data[nextInt].randomName)
                            transitRandomName.postValue(data[nextInt].randomName)
                            mHandler?.postDelayed(this, delay)
                            break
                        }
                    }
                }
            }
        }
    }

    fun initHandler() {
        if (mTimeThread == null || mTimeThread!!.state == Thread.State.TERMINATED) {
            mTimeThread = HandlerThread(RANDOM_NAME_THREAD_NAME).also { it.start() }
        }
        mHandler?.removeCallbacksAndMessages(null) ?: kotlin.run {
            mTimeThread?.let {
                mHandler = RandomNameHandler(it.looper)
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

    fun endRandom(endAfterAction: () -> Unit = {}) {
        runRandomStatus.value = RUN_STATUS_NONE
        mHandler?.removeCallbacks(runRuntime)
        endAfterAction.invoke()
    }

    override fun onCleared() {
        super.onCleared()
        mHandler?.removeCallbacksAndMessages(null)
        mHandler = null
        mTimeThread?.quitSafely()
        mTimeThread = null
    }
}