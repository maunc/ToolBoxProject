package com.maunc.toolbox.randomname.viewmodel

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.database.randomNameDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.obtainColor
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.randomname.constant.DELAY_UPDATE_LAYOUT
import com.maunc.toolbox.randomname.database.table.RandomNameData

class NewRandomNameViewModel : BaseRandomNameViewModel<BaseModel>() {

    private val handleEdit: Handler = Handler(Looper.getMainLooper())

    //是否更改过数据库
    var whetherDataHasChange = MutableLiveData(false)
    var showDeleteEditIcon = MutableLiveData(false)
    var showNameLimitTips = MutableLiveData(false)
    var nameLimitTips = MutableLiveData(GLOBAL_NONE_STRING)
    var nameLimitTipsTextColor = MutableLiveData(obtainColor(R.color.red))
    var newRandomName = MutableLiveData(GLOBAL_NONE_STRING)
    var newRandomNameToGroup = MutableLiveData(GLOBAL_NONE_STRING)
    var newRandomNameSuccess = MutableLiveData(false)
    var newNameWithLoading = MutableLiveData(false)
    private var newNameWithNameLoadingTime = 300L

    //EditText最多输入字数
    val newNameEditMaxNum = 5

    fun initiateCreateNewNameWithGroupEvent() {
        if (newRandomName.value!!.isEmpty()) {
            handleShowTipsEvent(
                obtainString(R.string.new_name_with_group_edit_none_tips_text), false
            )
            return
        }
        launch({
            randomNameDao.queryGroupNameAndRandomName(
                newRandomNameToGroup.value!!,
                newRandomName.value!!
            )
        }, {
            it?.let {
                handleShowTipsEvent(
                    obtainString(R.string.new_name_with_group_edit_exist_tips_text), false
                )
            } ?: createNewNameWithGroup()
        })
    }

    /**
     * 创建新名称对应分组
     */
    private fun createNewNameWithGroup() {
        newNameWithLoading.value = true
        launch({
            randomNameDao.insertRandomName(
                RandomNameData(
                    newRandomNameToGroup.value!!,
                    newRandomName.value!!
                )
            )
        }, {
            handleNewNameLoadingEnd(true)
        }, {
            "createNewNameWithGroup Error:${it.message},${it.stackTrace}".loge()
            handleNewNameLoadingEnd(false)
        })
    }

    private fun handleNewNameLoadingEnd(isSuccess: Boolean) {
        handleEdit.postDelayed({
            newRandomNameSuccess.value = isSuccess
            newNameWithLoading.value = false
            newRandomNameSuccess.value?.let { success ->
                handleShowTipsEvent(
                    if (success) {
                        whetherDataHasChange.value = true
                        obtainString(R.string.new_name_with_group_edit_success_tips_text)
                    } else {
                        obtainString(R.string.new_name_with_group_edit_error_tips_text)
                    }, success
                )
            }
        }, newNameWithNameLoadingTime)
    }

    //仅处理需要展示时的状态
    private fun handleShowTipsEvent(message: String, isSuccess: Boolean) {
        nameLimitTips.value = message
        handleTipsTextColor(isSuccess)
        showNameLimitTips.value = true
    }

    fun handleTipsTextColor(isSuccess: Boolean) {
        nameLimitTipsTextColor.value = if (isSuccess) {
            obtainColor(R.color.green_75)
        } else {
            obtainColor(R.color.red)
        }
    }

    fun clearEditText(editText: EditText) {
        editText.setText(GLOBAL_NONE_STRING)
    }

    fun updateNewGroupLayout(keyBoardHeight: Int, newGroup: ViewGroup) {
        newGroup.postDelayed({
            newGroup.updateLayoutParams<RelativeLayout.LayoutParams> {
                bottomMargin = keyBoardHeight
            }
        }, DELAY_UPDATE_LAYOUT)
    }

    override fun onCleared() {
        handleEdit.removeCallbacksAndMessages(null)
        super.onCleared()
    }
}