package com.maunc.toolbox.randomname.viewmodel

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.utils.RANDOM_AUTO
import com.maunc.toolbox.commonbase.utils.RANDOM_MANUAL
import com.maunc.toolbox.commonbase.utils.RANDOM_NOW
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomSelectRecyclerVisible
import com.maunc.toolbox.commonbase.utils.randomSpeed
import com.maunc.toolbox.commonbase.utils.randomType
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
    private var mRunUIHandler: Handler? = null

    var runRandomStatus = MutableLiveData(RUN_STATUS_NONE)//当前随机时状态
    var runRandomType = MutableLiveData(obtainMMKV.getInt(randomType))//点名类型
    var autoTypeRunNum = MutableLiveData(0)//在自动模式下经过多少时间结束一次点名(中转值)
    var autoTypeRunNumThreshold = MutableLiveData(20)//在自动模式下经过多少时间结束一次点名(总值)
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
        MutableLiveData<MutableList<RandomNameData>>(mutableListOf())//待点的数据 用于观察
    private var notSelects: MutableList<RandomNameData> = mutableListOf()//待点的数据 用于中转给观察者
    private var selects: MutableList<RandomNameData> = mutableListOf()//已点过的数据 用于中转给观察者

    /**
     * 手动点名任务
     */
    private val runManualRuntime = object : Runnable {
        override fun run() {
            runDelayTime.value?.let { delay ->
                obtainRandomList().let { data ->
                    while (true) {
                        if (data.size == 1) {
                            updateTargetName(data[0].randomName, data[0].randomName)
                            break
                        }
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

    /**
     * 自动点名任务
     */
    private val runAutoRuntime = object : Runnable {
        override fun run() {
            runDelayTime.value?.let { delay ->
                obtainRandomList().let { data ->
                    while (true) {
                        if (data.size == 1) {
                            updateTargetName(data[0].randomName, data[0].randomName)
                            stopAutoRandom()
                            break
                        }
                        val nextInt = Random().nextInt(data.size)
                        if (data[nextInt].randomName != transitRandomName.value) {
                            updateTargetName(data[nextInt].randomName, data[nextInt].randomName)
                            autoTypeRunNum.postValue(autoTypeRunNum.value!! + 1)
                            if (autoTypeRunNum.value!! == 20) {
                                stopAutoRandom()
                            } else {
                                mHandler?.postDelayed(this, delay)
                            }
                            break
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取随机列表
     */
    private fun obtainRandomList(): MutableList<RandomNameData> {
        return if (showSelectRecycler.value!!) {
            notSelectNameList.value!!
        } else {
            randomGroupValue.value!!
        }
    }

    /**
     * 更新当前点名
     */
    private fun updateTargetName(targetName: String, transitName: String) {
        targetRandomName.postValue(targetName)
        transitRandomName.postValue(transitName)
    }

    /**
     * 初始化数据
     */
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
        mRunUIHandler = Handler(Looper.getMainLooper())
    }

    /**
     * 开始点名
     */
    fun startRandom() {
        if (showSelectRecycler.value!!) {
            notSelectNameList.value?.let {
                if (it.isEmpty()) {
                    showDoneRandomTips.value = true
                    return
                }
            }
        }
        runRandomStatus.value = RUN_STATUS_START
        if (runRandomType.value!! == RANDOM_MANUAL) {
            mHandler?.post(runManualRuntime)
        }
        if (runRandomType.value!! == RANDOM_NOW) {
            stopNowRandom()
        }
        if (runRandomType.value!! == RANDOM_AUTO) {
            mHandler?.post(runAutoRuntime)
        }
    }

    /**
     * 暂停立即点名模式
     */
    private fun stopNowRandom() {
        obtainRandomList().let { data ->
            if (data.isEmpty()) return
            val nextInt = Random().nextInt(data.size)
            targetRandomName.value = data[nextInt].randomName
        }
        runRandomStatus.value = RUN_STATUS_STOP
        handleSelectData()
    }

    /**
     * 暂停手动点名模式
     */
    fun stopManualRandom() {
        runRandomStatus.value = RUN_STATUS_STOP
        mHandler?.removeCallbacks(runManualRuntime)
        handleSelectData()
    }

    /**
     * 暂停自动点名模式
     */
    fun stopAutoRandom() {
        mHandler?.removeCallbacks(runAutoRuntime)
        mRunUIHandler?.post {
            autoTypeRunNum.value = 0
            runRandomStatus.value = RUN_STATUS_STOP
            handleSelectData()
        }
    }

    /**
     * 处理已点名单
     */
    private fun handleSelectData() {
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
        var bool = true
        selectNameList.value?.let { selectList ->
            for (index in selectList.indices) {
                if (selectList[index].randomName == targetRandomName.value!!) {
                    bool = false
                    break
                }
            }
            if (bool) {
                selects.add(RandomNameData(toGroupName.value!!, targetRandomName.value!!))
                selectNameList.postValue(selects)
            }
        }
    }

    /**
     * 结束状态
     */
    fun endRandom() {
        runRandomStatus.value = RUN_STATUS_NONE
        mHandler?.removeCallbacksAndMessages(null)
    }

    override fun onCleared() {
        super.onCleared()
        mHandler?.removeCallbacksAndMessages(null)
        mRunUIHandler?.removeCallbacksAndMessages(null)
        mHandler = null
        mRunUIHandler = null
        mTimeThread?.quitSafely()
        mTimeThread = null
    }
}