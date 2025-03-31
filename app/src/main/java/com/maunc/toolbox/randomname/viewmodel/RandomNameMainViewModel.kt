package com.maunc.toolbox.randomname.viewmodel

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomSelectRecyclerVisible
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

    private var mTimeThread: HandlerThread? = null
    private var mHandler: RandomNameHandler? = null

    var runRandomStatus = MutableLiveData(RUN_STATUS_NONE)//当前随机时状态
    var toGroupName = MutableLiveData(GLOBAL_NONE_STRING)//当前数据属于哪个分组
    var targetRandomName = MutableLiveData(obtainString(R.string.random_none_text))//随机中选中了哪一个
    var transitRandomName = MutableLiveData(GLOBAL_NONE_STRING) //避免随机相同的数据造成UI上的卡顿错觉
    var runDelayTime = MutableLiveData(obtainMMKV.getLong(randomSpeed))//相差多少时间随机一次
    var showSelectRecycler =
        MutableLiveData(obtainMMKV.getBoolean(randomSelectRecyclerVisible))//是否启用查看已点名单
    var randomGroupValue = MutableLiveData<MutableList<RandomNameData>>(mutableListOf())//总数据 不会变

    /**启动已点名单后这些值才有用*/
    var showDoneRandomTips = MutableLiveData(false) //已经点完所有人的tips
    var selectNameList = MutableLiveData<MutableList<RandomNameData>>(mutableListOf())//已点过的数据 用于观察
    var notSelectNameList =
        MutableLiveData<MutableList<RandomNameData>>(mutableListOf())//未点过的数据 用于观察
    private var notSelects: MutableList<RandomNameData> = mutableListOf()//未点过的数据 用于中转给观察者
    private var selects: MutableList<RandomNameData> = mutableListOf()//已点过的数据 用于中转给观察者

    /**
     * 循环任务
     */
    private val runRuntime = object : Runnable {
        override fun run() {
            runDelayTime.value?.let { delay ->
                //随机点名
                obtainRandomList().let { data ->
                    if (data.size == 1) {
                        updateTargetName(data[0].randomName, data[0].randomName)
                        return
                    }
                    while (true) {
                        val nextInt = Random().nextInt(data.size)
                        if (data[nextInt].randomName != transitRandomName.value) {
                            updateTargetName(data[nextInt].randomName, data[nextInt].randomName)
                            mHandler?.postDelayed(this, delay)
                            break
                        }
                    }
                }
            }
        }
    }

    private fun obtainRandomList(): MutableList<RandomNameData> {
        return if (showSelectRecycler.value!!) {
            notSelectNameList.value!!
        } else {
            randomGroupValue.value!!
        }
    }

    private fun updateTargetName(
        targetName: String,
        transitName: String,
    ) {
        targetRandomName.postValue(targetName)
        transitRandomName.postValue(transitName)
    }

    fun initData() {
        if (notSelects.isNotEmpty()) {
            notSelects.clear()
        }
        notSelects.addAll(randomGroupValue.value!!)
        selects.clear()
        selectNameList.postValue(selects)
        notSelectNameList.postValue(notSelects)
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

    fun startRandom(
        startError: () -> Unit = {},
    ) {
        if (showSelectRecycler.value!!) {
            notSelectNameList.value?.let {
                if (it.isEmpty()) {
                    startError.invoke()
                    return
                }
            }
        }
        runRandomStatus.value = RUN_STATUS_START
        mHandler?.post(runRuntime)

    }

    fun stopRandom() {
        runRandomStatus.value = RUN_STATUS_STOP
        mHandler?.removeCallbacks(runRuntime)
        if (!showSelectRecycler.value!!) {
            return
        }
        notSelectNameList.value?.let { notSelectList ->
            for (index in notSelectList.indices) {
                val randomNameData = notSelectList[index]
                if (randomNameData.randomName == targetRandomName.value!!) {
                    notSelects.remove(randomNameData)
                    notSelectNameList.postValue(notSelects)
                    break
                }
            }
        }
        var bool = false
        selectNameList.value?.let { selectList ->
            for (index in selectList.indices) {
                val randomNameData = selectList[index]
                if (randomNameData.randomName == targetRandomName.value!!) {
                    bool = true
                    break
                }
            }
            if (!bool) {
                selects.add(RandomNameData(toGroupName.value!!, targetRandomName.value!!))
                selectNameList.postValue(selects)
            }
        }
    }

    fun endRandom(
        endAfterAction: () -> Unit = {},
    ) {
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