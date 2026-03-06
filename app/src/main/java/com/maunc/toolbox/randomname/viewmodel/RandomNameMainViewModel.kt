package com.maunc.toolbox.randomname.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.constant.ARRAY_INDEX_ONE
import com.maunc.toolbox.commonbase.constant.ARRAY_INDEX_ZERO
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.database.randomNameTransactionDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.randomname.constant.RANDOM_AUTO
import com.maunc.toolbox.randomname.constant.RANDOM_MANUAL
import com.maunc.toolbox.randomname.constant.RANDOM_NOW
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MAX
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MEDIUM
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MIN
import com.maunc.toolbox.randomname.constant.RUN_STATUS_NONE
import com.maunc.toolbox.randomname.constant.RUN_STATUS_START
import com.maunc.toolbox.randomname.constant.RUN_STATUS_STOP
import com.maunc.toolbox.randomname.database.table.RandomNameData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random

class RandomNameMainViewModel : BaseViewModel<BaseModel>() {

    private var runManualJob: Job? = null //手动点名任务
    private var runAutoJob: Job? = null //自动点名任务

    var reStartSelectDataEvent = MutableLiveData<Boolean>() //是否重置数据
    var targetRunRandomName = MutableLiveData(obtainString(R.string.random_none_text))//随机中选中了哪一个
    var targetRandomName = MutableLiveData<String>()//随机结果选中了哪一个
    var randomTips = MutableLiveData(GLOBAL_NONE_STRING) //tips
    var runRandomStatus = MutableLiveData(RUN_STATUS_NONE)//当前随机时状态
    private var autoTypeRunNum = MutableLiveData(0)//在自动模式下经过多少时间结束一次点名(中转值)
    private var autoTypeRunNumThreshold = MutableLiveData(20)//在自动模式下经过多少时间结束一次点名(总值)
    var toGroupName = MutableLiveData(GLOBAL_NONE_STRING)//当前数据属于哪个分组
    private var transitRandomName = MutableLiveData(GLOBAL_NONE_STRING) //避免随机相同的数据造成UI上的卡顿错觉
    var randomGroupValue = MutableLiveData<MutableList<RandomNameData>>(mutableListOf())//总数据 不会变
    var countTargetRandomName = HashMap<String, Int>()//统计每个人都点了几次

    /**启动已点名单后这些值才有用*/
    var selectListChange = MutableLiveData<Boolean>() //名单是否变化了
    var notSelects: MutableList<RandomNameData> = mutableListOf()//待点的数据
    var selects: MutableList<RandomNameData> = mutableListOf()//已点过的数据

    /**
     * 启动立即点名模式
     */
    private fun startNowTask() {
        val randomList = obtainRandomList()
        if (randomList.isEmpty()) return
        val randomName = randomList[Random().nextInt(randomList.size)].randomName
        viewModelScope.launch(Dispatchers.Main) {
            runRandomStatus.postValue(RUN_STATUS_STOP)
            targetRunRandomName.postValue(randomName)
            targetRandomName.postValue(randomName)
        }
    }

    /**
     * 启动手动点名模式
     */
    private fun startManualTask() {
        runManualJob?.cancel()
        runManualJob = viewModelScope.launch(Dispatchers.IO) {
            val delayTime = appViewModel.randomNameRunSpeed.value ?: return@launch
            val data = obtainRandomList()
            if (data.isEmpty()) return@launch
            while (isActive) {
                if (data.size == ARRAY_INDEX_ONE) {
                    updateTargetName(data[ARRAY_INDEX_ZERO].randomName)
                    break
                }
                val nextInt = Random().nextInt(data.size)
                if (data[nextInt].randomName != transitRandomName.value) {
                    updateTargetName(data[nextInt].randomName)
                    delay(delayTime)
                }
            }
        }
    }

    /**
     * 启动自动点名模式
     */
    private fun startAutoTask() {
        runAutoJob?.cancel()
        runAutoJob = viewModelScope.launch(Dispatchers.IO) {
            val delayTime = appViewModel.randomNameRunSpeed.value ?: return@launch
            val data = obtainRandomList()
            if (data.isEmpty()) return@launch
            while (isActive) {
                if (data.size == ARRAY_INDEX_ONE) {
                    updateTargetName(data[ARRAY_INDEX_ZERO].randomName)
                    stopAutoRandom()
                    break
                }
                val nextInt = Random().nextInt(data.size)
                if (data[nextInt].randomName != transitRandomName.value) {
                    updateTargetName(data[nextInt].randomName)
                    autoTypeRunNum.postValue(autoTypeRunNum.value!! + 1)
                    if (autoTypeRunNum.value!! >= autoTypeRunNumThreshold.value!!) {
                        stopAutoRandom()
                        break
                    } else {
                        delay(delayTime)
                    }
                }
            }
        }
    }

    /**
     * 获取随机列表
     */
    private fun obtainRandomList(): MutableList<RandomNameData> {
        return if (!appViewModel.randomNameRunRepeat.value!!) notSelects else randomGroupValue.value!!
    }

    /**
     * 更新当前点名
     */
    private fun updateTargetName(targetName: String) {
        targetRunRandomName.postValue(targetName)
        transitRandomName.postValue(targetName)
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
            initRestartData()
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
            startManualTask()
        }
        if (appViewModel.randomNameRunType.value!! == RANDOM_NOW) {
            startNowTask()
        }
        if (appViewModel.randomNameRunType.value!! == RANDOM_AUTO) {
            startAutoTask()
        }
    }

    /**
     * 暂停手动点名模式
     */
    fun stopManualRandom() {
        runManualJob?.cancel()
        viewModelScope.launch(Dispatchers.Main) {
            runRandomStatus.postValue(RUN_STATUS_STOP)
            targetRandomName.postValue(targetRunRandomName.value)
        }
    }

    /**
     * 暂停自动点名模式
     */
    fun stopAutoRandom() {
        runAutoJob?.cancel()
        viewModelScope.launch(Dispatchers.Main) {
            runRandomStatus.postValue(RUN_STATUS_STOP)
            autoTypeRunNum.postValue(0)
            targetRandomName.postValue(targetRunRandomName.value)
        }
    }

    /**
     * 结束状态
     */
    fun endRandom() {
        initRestartData()
        runRandomStatus.value = RUN_STATUS_NONE
        cancelRunJob()
    }

    private fun cancelRunJob() {
        runAutoJob?.cancel()
        runAutoJob = null
        runManualJob?.cancel()
        runManualJob = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelRunJob()
    }
}