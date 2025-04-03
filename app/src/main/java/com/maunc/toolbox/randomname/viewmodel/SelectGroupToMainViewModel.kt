package com.maunc.toolbox.randomname.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.database.randomNameTransactionDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomListSortType
import com.maunc.toolbox.randomname.constant.GROUP_REMOVE_THRESHOLD
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_DESC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_NAME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_NAME_DESC
import com.maunc.toolbox.randomname.database.table.RandomNameWithGroup

class SelectGroupToMainViewModel : BaseRandomNameViewModel<BaseModel>() {

    private var dbSortType = MutableLiveData(obtainMMKV.getInt(randomListSortType))

    var groupData = MutableLiveData<MutableList<RandomNameWithGroup>>(mutableListOf())

    var groupDataIsNull = MutableLiveData<Boolean>()

    var showTipsBool = MutableLiveData<Boolean>()
    var tipsStrVar = MutableLiveData<String>()

    fun queryGroupData() {
        launch({
            when (dbSortType.value!!) {
                RANDOM_DB_SORT_BY_INSERT_TIME_ASC, RANDOM_DB_SORT_BY_INSERT_TIME_DESC ->
                    randomNameTransactionDao.queryNameWithGroupByInsertTime(dbSortType.value!!)

                RANDOM_DB_SORT_BY_NAME_ASC, RANDOM_DB_SORT_BY_NAME_DESC ->
                    randomNameTransactionDao.queryNameWithGroupByName(dbSortType.value!!)

                else -> randomNameTransactionDao.queryNameWithGroupByInsertTime(
                    RANDOM_DB_SORT_BY_INSERT_TIME_ASC
                )
            }
        }, {
            handleGroupData(it)
        }, {
            "queryGroupData Error ${it.message}  ${it.stackTrace}".loge()
        })
    }

    private fun handleGroupData(groupList: MutableList<RandomNameWithGroup>) {
        val iterator = groupList.listIterator()
        while (iterator.hasNext()) {
            val group = iterator.next()
            if (group.randomNameDataList.isEmpty()) {
                iterator.remove()
            }
            if (group.randomNameDataList.size == GROUP_REMOVE_THRESHOLD) {
                iterator.remove()
            }
        }
        groupData.value = groupList
        groupDataIsNull.value = groupList.isEmpty()
        handleTips(groupList.isEmpty())
    }

    private fun handleTips(groupIsEmpty: Boolean) {
        if (groupIsEmpty) {
            showTipsBool.value = false
        } else {
            showTipsBool.value = true
            tipsStrVar.value = obtainString(R.string.select_group_to_main_tips_text)
        }
    }
}