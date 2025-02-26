package com.maunc.randomcallname.viewmodel

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.MutableLiveData
import com.maunc.randomcallname.R
import com.maunc.randomcallname.RandomNameApplication
import com.maunc.randomcallname.base.BaseModel
import com.maunc.randomcallname.base.BaseViewModel
import com.maunc.randomcallname.constant.DELAY_KEY_BROAD
import com.maunc.randomcallname.constant.DELAY_UPDATE_LAYOUT
import com.maunc.randomcallname.constant.GLOBAL_NONE_STRING
import com.maunc.randomcallname.database.randomNameDao
import com.maunc.randomcallname.database.table.RandomNameData
import com.maunc.randomcallname.ext.getColor
import com.maunc.randomcallname.ext.getString
import com.maunc.randomcallname.ext.inputMethodManager
import com.maunc.randomcallname.ext.launch
import com.maunc.randomcallname.ext.loge

class NewNameWithGroupViewModel : BaseViewModel<BaseModel>() {

    private val handleEdit: Handler = Handler(Looper.getMainLooper())

    var showDeleteEditIcon = MutableLiveData(false)
    var showNameLimitTips = MutableLiveData(false)
    var nameLimitTips = MutableLiveData(GLOBAL_NONE_STRING)
    var nameLimitTipsTextColor = MutableLiveData(getColor(R.color.red))

    var newRandomName = MutableLiveData(GLOBAL_NONE_STRING)
    var newRandomNameToGroup = MutableLiveData(GLOBAL_NONE_STRING)
    var newRandomNameSuccess = MutableLiveData<Boolean>()
    var newNameWithLoading = MutableLiveData(false)
    private var newNameWithNameLoadingTime = 300L

    //EditText最多输入字数
    val newNameEditMaxNum = 8

    fun initiateCreateNewNameWithGroupEvent() {
        if (newRandomName.value!!.isEmpty()) {
            handleShowTipsEvent(
                getString(R.string.new_name_with_group_edit_none_tips_text),
                false
            )
            return
        }
        launch({
            randomNameDao.queryGroupNameAndRandomName(
                newRandomNameToGroup.value!!,
                newRandomName.value!!
            )
        }, {
            if (it == null) {
                createNewNameWithGroup()
            } else {
                handleShowTipsEvent(
                    getString(R.string.new_name_with_group_edit_exist_tips_text),
                    false
                )
            }
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
            "createNewNameWithGroup Success:$it".loge()
            newRandomNameSuccess.value = true
            handleNewNameLoadingEnd()
        }, {
            "createNewNameWithGroup Error:${it.message},${it.stackTrace}".loge()
            newNameWithLoading.value = false
            handleNewNameLoadingEnd()
        })
    }

    private fun handleNewNameLoadingEnd() {
        handleEdit.postDelayed({
            newNameWithLoading.value = false
            handleShowTipsEvent(
                if (newRandomNameSuccess.value!!) {
                    getString(R.string.new_name_with_group_edit_success_tips_text)
                } else {
                    getString(R.string.new_name_with_group_edit_error_tips_text)
                },
                newRandomNameSuccess.value!!
            )
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
            getColor(R.color.green_75)
        } else {
            getColor(R.color.red)
        }
    }

    fun showSoftInputKeyBoard(editText: EditText) {
        handleEdit.postDelayed({
            val inputManger = RandomNameApplication.app.inputMethodManager
            inputManger?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, DELAY_KEY_BROAD)
    }

    fun hideSoftInputKeyBoard(editText: EditText) {
        handleEdit.postDelayed({
            val inputManger = RandomNameApplication.app.inputMethodManager
            inputManger?.hideSoftInputFromWindow(editText.windowToken, 0)
        }, DELAY_KEY_BROAD)
    }

    fun updateNewGroupLayout(keyBoardHeight: Int, newGroup: ViewGroup) {
        handleEdit.postDelayed({
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