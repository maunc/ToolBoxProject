package com.maunc.toolbox

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maunc.toolbox.commonbase.ext.assetFileParseJson
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.turnTableAnimSoundEffect
import com.maunc.toolbox.commonbase.utils.turnTableConfigColor
import com.maunc.toolbox.commonbase.utils.turnTableEnableTouch
import com.maunc.toolbox.turntable.data.TurnTableConfigColorData
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup
import com.maunc.unpeeklivedata.ui.callback.UnPeekLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    /**===============================================   点名相关   ===============================================*/

    /**===============================================   转盘相关   ===============================================*/
    // 转盘当前颜色
    var turnTableColorIndex = UnPeekLiveData<Int>()

    //转盘当前是否可以触摸
    var turnTableTouch = UnPeekLiveData<Boolean>()

    //转盘是否开启音效
    var turnTableSoundEffect = UnPeekLiveData<Boolean>()

    // 转盘预制颜色
    var turnTableBuiltinColorData =
        UnPeekLiveData<MutableList<TurnTableConfigColorData>>()

    // 转盘预制数据
    var turnTableBuiltinContentData =
        UnPeekLiveData<MutableList<TurnTableNameWithGroup>>()

    fun initMMKV() {
        Log.d(TAG, "initMMKV")
        obtainMMKV.init()
    }

    /**
     * 初始化点名配置
     */
    fun initRandomNameConfig() {

    }

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
            withContext(Dispatchers.Main) {
                turnTableBuiltinColorData.value = colorList
                turnTableBuiltinContentData.value = contentList
                turnTableColorIndex.value = obtainMMKV.getInt(turnTableConfigColor)
                turnTableTouch.value = obtainMMKV.getBoolean(turnTableEnableTouch)
                turnTableSoundEffect.value = obtainMMKV.getBoolean(turnTableAnimSoundEffect)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }
}