package com.maunc.toolbox

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maunc.toolbox.commonbase.ext.assetFileParseJson
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomEnumCountEnableType
import com.maunc.toolbox.commonbase.utils.randomRepeat
import com.maunc.toolbox.commonbase.utils.randomSelectRecyclerVisible
import com.maunc.toolbox.commonbase.utils.randomSpeed
import com.maunc.toolbox.commonbase.utils.randomTextBold
import com.maunc.toolbox.commonbase.utils.randomTextSize
import com.maunc.toolbox.commonbase.utils.randomType
import com.maunc.toolbox.commonbase.utils.turnTableAnimSoundEffect
import com.maunc.toolbox.commonbase.utils.turnTableConfigColor
import com.maunc.toolbox.commonbase.utils.turnTableEnableTouch
import com.maunc.toolbox.randomname.database.table.RandomNameWithGroup
import com.maunc.toolbox.turntable.data.TurnTableConfigColorData
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup
import com.maunc.unpeeklivedata.ui.callback.UnPeekLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * 一、unpeekLive 解决数据倒灌(粘性时间的根本)的，首次初始化的数据将不会生效，如何生效
 *                         1.使用observeSticky会监听liveData上一次的数据(恢复原来使用方式)
 *                         2.先注册观察值，然后初始化值可以收到这个初始化值
 */
class ToolBoxApplicationViewModel(application: Application) : AndroidViewModel(application) {

    private companion object {
        const val TAG = "ToolBoxApplicationViewModel"
    }

    private val appContext: Context by lazy {
        (getApplication<Application>() as ToolBoxApplication).applicationContext
    }

    fun initMMKV() {
        Log.d(TAG, "initMMKV")
        obtainMMKV.init()
    }

    /**===============================================   点名配置相关   ===============================================*/
    var randomNameResultTextSize = UnPeekLiveData<Int>()//结果文本大小
    var randomNameResultIsBold = UnPeekLiveData<Boolean>()//结果文本是否加粗
    var randomNameRunRepeat = UnPeekLiveData<Boolean>()//是否允许重复点名
    var randomNameShowSelectRecycler = UnPeekLiveData<Boolean>()//是否启用查看已点名单
    var randomNameRunSpeed = UnPeekLiveData<Long>()//相差多少时间随机一次
    var randomNameRunType = UnPeekLiveData<Int>()//点名模式
    var randomEnumCountEnable = UnPeekLiveData<Boolean>()//是否启用统计
    var randomBuiltinContentData = UnPeekLiveData<MutableList<RandomNameWithGroup>>()//转盘预制数据

    /**
     * 初始化点名配置和预制数据
     */
    fun initRandomNameConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            val contentListAsync = async {
                assetFileParseJson<MutableList<RandomNameWithGroup>>(
                    fileName = "random_name_data/random_name_content.json"
                )
            }
            val contentList = contentListAsync.await()
            randomBuiltinContentData.postValue(contentList)
            randomNameResultTextSize.postValue(obtainMMKV.getInt(randomTextSize))
            randomNameResultIsBold.postValue(obtainMMKV.getBoolean(randomTextBold))
            randomNameRunRepeat.postValue(obtainMMKV.getBoolean(randomRepeat))
            randomNameShowSelectRecycler.postValue(obtainMMKV.getBoolean(randomSelectRecyclerVisible))
            randomNameRunSpeed.postValue(obtainMMKV.getLong(randomSpeed))
            randomNameRunType.postValue(obtainMMKV.getInt(randomType))
            randomEnumCountEnable.postValue(obtainMMKV.getBoolean(randomEnumCountEnableType))
        }
    }

    /**===============================================   转盘配置相关   ===============================================*/

    var turnTableColorIndex = UnPeekLiveData<Int>()// 转盘当前颜色
    var turnTableTouch = UnPeekLiveData<Boolean>()//转盘当前是否可以触摸
    var turnTableSoundEffect = UnPeekLiveData<Boolean>() //转盘是否开启音效
    var turnTableBuiltinColorData = UnPeekLiveData<MutableList<TurnTableConfigColorData>>()//转盘预制颜色
    var turnTableBuiltinContentData = UnPeekLiveData<MutableList<TurnTableNameWithGroup>>()//转盘预制数据

    /**
     * 初始化转盘配置和预制数据
     */
    fun initTurnTableConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            val colorListAsync = async {
                assetFileParseJson<MutableList<TurnTableConfigColorData>>(
                    fileName = "turn_table_data/turn_table_color.json"
                )
            }
            val contentListAsync = async {
                assetFileParseJson<MutableList<TurnTableNameWithGroup>>(
                    fileName = "turn_table_data/turn_table_content.json"
                )
            }
            val colorList = colorListAsync.await()
            val contentList = contentListAsync.await()
            turnTableBuiltinColorData.postValue(colorList)
            turnTableBuiltinContentData.postValue(contentList)
            turnTableColorIndex.postValue(obtainMMKV.getInt(turnTableConfigColor))
            turnTableTouch.postValue(obtainMMKV.getBoolean(turnTableEnableTouch))
            turnTableSoundEffect.postValue(obtainMMKV.getBoolean(turnTableAnimSoundEffect))
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }
}