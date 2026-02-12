package com.maunc.toolbox.turntable.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.database.turnTableDataDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.turntable.adapter.TurnTableEditDataAdapter
import com.maunc.toolbox.turntable.data.TurnTableEditData
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup
import com.maunc.toolbox.turntable.ui.TurnTableEditDataActivity.Companion.TURN_TABLE_ADD_STATUS
import com.maunc.toolbox.turntable.ui.TurnTableEditDataActivity.Companion.TURN_TABLE_UPDATE_STATUS

class TurnTableEditDataViewModel : BaseViewModel<BaseModel>() {

    var hasEdited = MutableLiveData(false)//是否编辑过
    var currentStatus: Int = TURN_TABLE_ADD_STATUS // 当前列表是编辑还是新增
    var showEditErrorTips = MutableLiveData(false)
    var editErrorTips = MutableLiveData(GLOBAL_NONE_STRING)
    var softKeyBroadHeight = MutableLiveData<Int>() //软键盘高度
    var currentSaveDataSize = MutableLiveData(0)//当前保存的数据数量

    //初始列表标题数据，只有是修改的时候才需要记录
    private var initialTitleData = mutableListOf<TurnTableEditData>()

    /**
     * 全新的数据
     */
    fun initEditAdapterNotData(adapter: TurnTableEditDataAdapter) {
        adapter.addEditTitleItem(GLOBAL_NONE_STRING)
        adapter.addEditNameItem(GLOBAL_NONE_STRING)
        adapter.addEditNameItem(GLOBAL_NONE_STRING)
        currentStatus = TURN_TABLE_ADD_STATUS
        currentSaveDataSize.value = 2
    }

    fun initEditAdapterData(
        adapter: TurnTableEditDataAdapter,
        turnTableWithNameGroup: TurnTableNameWithGroup,
    ) {
        val groupName = turnTableWithNameGroup.turnTableGroupData.groupName
        initialTitleData.add(TurnTableEditData(groupName, TurnTableEditData.EDIT_TURN_TABLE_TITLE))
        adapter.addEditTitleItem(groupName)
        turnTableWithNameGroup.turnTableNameDataList.map {
            initialTitleData.add(TurnTableEditData(it.name, TurnTableEditData.EDIT_TURN_TABLE_NAME))
            adapter.addEditNameItem(it.name)
        }
        currentStatus = TURN_TABLE_UPDATE_STATUS
        currentSaveDataSize.value = turnTableWithNameGroup.turnTableNameDataList.size
    }

    fun insertTurnTableEditData(list: MutableList<TurnTableEditData>) {
        hideErrorTips()
        launch({
            turnTableDataDao.insertTurnTableEditData(list)
        }, {
            "insertTurnTableEditData Success".loge()
        }, {
            "insertTurnTableEditData Error:${it.message},${it.stackTrace}".loge()
        })
    }

    fun updateTurnTableEditData(list: MutableList<TurnTableEditData>) {
        hideErrorTips()
        launch({
            turnTableDataDao.updateTurnTableEditData(
                oldTitle = initialTitleData[0].content,
                newTitle = list[0].content,
                oldList = initialTitleData.editDataToStringList(),
                newList = list.editDataToStringList()
            )
        }, {
            "updateTurnTableEditData Success".loge()
        }, {
            "updateTurnTableEditData Error:${it.message},${it.stackTrace}".loge()
        })
    }

    fun handleCurrentSaveSize(size: Int) {
        hideErrorTips()
        currentSaveDataSize.value = size
    }

    fun showErrorTips(errorTips: String) {
        showEditErrorTips.value = true
        editErrorTips.value = errorTips
    }

    fun hideErrorTips() {
        if (!showEditErrorTips.value!!) return
        showEditErrorTips.value = false
    }

    /**
     * 过滤标题，生成内容列表
     */
    fun MutableList<TurnTableEditData>.editDataToStringList(): MutableList<String> {
        return subList(1, size).map { it.content }.toMutableList()
    }
}