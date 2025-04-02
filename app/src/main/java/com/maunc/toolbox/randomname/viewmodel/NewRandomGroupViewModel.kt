package com.maunc.toolbox.randomname.viewmodel

import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.database.randomGroupDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.randomname.constant.DELAY_UPDATE_LAYOUT
import com.maunc.toolbox.randomname.database.table.RandomNameGroup

class NewRandomGroupViewModel : BaseRandomNameViewModel<BaseModel>() {

    var showDeleteEditIcon = MutableLiveData(false)
    var showNameLimitTips = MutableLiveData(false)

    //是否更改过数据库
    var whetherDataHasChange = MutableLiveData(false)
    var nameLimitTips = MutableLiveData(GLOBAL_NONE_STRING)

    var newGroupName = MutableLiveData(GLOBAL_NONE_STRING)

    //EditText最多输入字数
    val newGroupEditMaxNum = 10

    fun initiateCreateNewGroupEvent() {
        if (newGroupName.value!!.isEmpty()) {
            handleShowTipsEvent(obtainString(R.string.new_group_edit_none_tips_text))
            return
        }
        launch({
            randomGroupDao.queryRandomNameGroup(newGroupName.value!!)
        }, {
            it?.let {
                handleShowTipsEvent(obtainString(R.string.new_group_edit_exist_tips_text))
            } ?: createNewGroup()
        })
    }

    /**
     * 创建新分组
     */
    private fun createNewGroup() {
        launch({
            randomGroupDao.insertRandomNameGroup(RandomNameGroup(newGroupName.value!!))
        }, {
            whetherDataHasChange.value = true
        }, {
            "createNewGroup error:${it.message}  ${it.stackTrace}".loge()
        })
    }

    //仅处理需要展示时的状态
    private fun handleShowTipsEvent(message: String) {
        nameLimitTips.value = message
        showNameLimitTips.value = true
    }

    fun updateNewGroupLayout(keyBoardHeight: Int, newGroup: ViewGroup) {
        newGroup.postDelayed({
            newGroup.updateLayoutParams<RelativeLayout.LayoutParams> {
                bottomMargin = keyBoardHeight
            }
        }, DELAY_UPDATE_LAYOUT)
    }
}