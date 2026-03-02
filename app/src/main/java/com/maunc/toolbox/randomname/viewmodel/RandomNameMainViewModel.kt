package com.maunc.toolbox.randomname.viewmodel

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.constant.ARRAY_INDEX_ONE
import com.maunc.toolbox.commonbase.constant.ARRAY_INDEX_ZERO
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.database.randomNameTransactionDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.randomname.constant.RANDOM_AUTO
import com.maunc.toolbox.randomname.constant.RANDOM_MANUAL
import com.maunc.toolbox.randomname.constant.RANDOM_NAME_THREAD_NAME
import com.maunc.toolbox.randomname.constant.RANDOM_NOW
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MAX
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MEDIUM
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MIN
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

    var reStartSelectDataEvent = MutableLiveData<Boolean>() //是否重置数据
    var targetRunRandomName = MutableLiveData(obtainString(R.string.random_none_text))//随机中选中了哪一个
    var targetRandomName = MutableLiveData<String>()//随机结果选中了哪一个
    var randomTips = MutableLiveData(GLOBAL_NONE_STRING) //tips
    var runRandomStatus = MutableLiveData(RUN_STATUS_NONE)//当前随机时状态
    var autoTypeRunNum = MutableLiveData(0)//在自动模式下经过多少时间结束一次点名(中转值)
    var autoTypeRunNumThreshold = MutableLiveData(20)//在自动模式下经过多少时间结束一次点名(总值)
    var toGroupName = MutableLiveData(GLOBAL_NONE_STRING)//当前数据属于哪个分组
    var transitRandomName = MutableLiveData(GLOBAL_NONE_STRING) //避免随机相同的数据造成UI上的卡顿错觉
    var randomGroupValue = MutableLiveData<MutableList<RandomNameData>>(mutableListOf())//总数据 不会变

    /**启动已点名单后这些值才有用*/
    var notSelects: MutableList<RandomNameData> = mutableListOf()//待点的数据
    var selects: MutableList<RandomNameData> = mutableListOf()//已点过的数据

    /**
     * 手动点名任务
     */
    private val runManualRuntime = object : Runnable {
        override fun run() {
            appViewModel.randomNameRunSpeed.value?.let { delay ->
                obtainRandomList().let { data ->
                    while (true) {
                        if (data.size == ARRAY_INDEX_ONE) {
                            updateTargetName(
                                data[ARRAY_INDEX_ZERO].randomName,
                                data[ARRAY_INDEX_ZERO].randomName
                            )
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
            appViewModel.randomNameRunSpeed.value?.let { delay ->
                obtainRandomList().let { data ->
                    while (true) {
                        if (data.size == ARRAY_INDEX_ONE) {
                            updateTargetName(
                                data[ARRAY_INDEX_ZERO].randomName,
                                data[ARRAY_INDEX_ZERO].randomName
                            )
                            stopAutoRandom()
                            break
                        }
                        val nextInt = Random().nextInt(data.size)
                        if (data[nextInt].randomName != transitRandomName.value) {
                            updateTargetName(data[nextInt].randomName, data[nextInt].randomName)
                            autoTypeRunNum.postValue(autoTypeRunNum.value!! + 1)
                            if (autoTypeRunNum.value!! >= autoTypeRunNumThreshold.value!!) {
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
        return if (!appViewModel.randomNameRunRepeat.value!!) {
            notSelects
        } else {
            randomGroupValue.value!!
        }
    }

    /**
     * 更新当前点名
     */
    private fun updateTargetName(targetName: String, transitName: String) {
        targetRunRandomName.postValue(targetName)
        transitRandomName.postValue(transitName)
    }

    /**
     * 初始化
     */
    fun initViewModelConfig() {
        initHandler()
        initRandomList()
    }

    fun initRandomList() {
        launch({
            randomNameTransactionDao.querySelectGroupData()
        }, {
            toGroupName.value = it.randomNameGroup.groupName
            randomGroupValue.value = it.randomNameDataList
            initRestartData()
        }, {
            "initRandomList Error:${it.message},${it.stackTrace}".loge()
            toGroupName.value = GLOBAL_NONE_STRING
            randomGroupValue.value = mutableListOf()
        })
    }

    private fun initRestartData() {
        if (notSelects.isNotEmpty()) {
            notSelects.clear()
        }
        notSelects.addAll(randomGroupValue.value!!)
        selects.clear()
        randomTips.value = GLOBAL_NONE_STRING
        if (appViewModel.randomNameRunType.value == RANDOM_AUTO) {
            when (appViewModel.randomNameRunSpeed.value) {
                RANDOM_SPEED_MAX -> autoTypeRunNumThreshold.value = 40
                RANDOM_SPEED_MEDIUM -> autoTypeRunNumThreshold.value = 20
                RANDOM_SPEED_MIN -> autoTypeRunNumThreshold.value = 10
            }
        }
        reStartSelectDataEvent.value = true
    }

    private fun initHandler() {
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
        if (randomGroupValue.value?.isEmpty() == true) {
            randomTips.value = obtainString(R.string.random_start_not_data_tips)
            return
        }
        if (!appViewModel.randomNameRunRepeat.value!!) {
            notSelects.let {
                if (it.isEmpty()) {
                    randomTips.value = obtainString(R.string.random_start_error_tips)
                    return
                }
            }
        }
        runRandomStatus.value = RUN_STATUS_START
        if (appViewModel.randomNameRunType.value!! == RANDOM_MANUAL) {
            mHandler?.post(runManualRuntime)
        }
        if (appViewModel.randomNameRunType.value!! == RANDOM_NOW) {
            stopNowRandom()
        }
        if (appViewModel.randomNameRunType.value!! == RANDOM_AUTO) {
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
            targetRandomName.postValue(data[nextInt].randomName)
        }
        runRandomStatus.value = RUN_STATUS_STOP
    }

    /**
     * 暂停手动点名模式
     */
    fun stopManualRandom() {
        runRandomStatus.value = RUN_STATUS_STOP
        targetRandomName.postValue(targetRunRandomName.value)
        mHandler?.removeCallbacks(runManualRuntime)
    }

    /**
     * 暂停自动点名模式
     */
    fun stopAutoRandom() {
        mHandler?.removeCallbacks(runAutoRuntime)
        mRunUIHandler?.post {
            autoTypeRunNum.value = 0
            targetRandomName.postValue(targetRunRandomName.value)
            runRandomStatus.value = RUN_STATUS_STOP
        }
    }

    /**
     * 结束状态
     */
    fun endRandom() {
        initRestartData()
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