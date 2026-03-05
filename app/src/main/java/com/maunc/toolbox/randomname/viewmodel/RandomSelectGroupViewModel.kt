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
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_DESC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_NAME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_NAME_DESC
import com.maunc.toolbox.randomname.database.table.RandomNameWithGroup

class RandomSelectGroupViewModel : BaseRandomNameViewModel<BaseModel>() {

    private var dbSortType = MutableLiveData(obtainMMKV.getInt(randomListSortType))
    var groupData = MutableLiveData<MutableList<RandomNameWithGroup>>()
    var groupDataIsExist = MutableLiveData<Boolean>()//是否有数据
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
            groupData.value = it
            handleTips(it.isEmpty())
        }, {
            "queryGroupData Error ${it.message}  ${it.stackTrace}".loge()
        })
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