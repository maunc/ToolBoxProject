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
import com.maunc.randomcallname.database.randomGroupDao
import com.maunc.randomcallname.database.table.RandomNameGroup
import com.maunc.randomcallname.ext.getString
import com.maunc.randomcallname.ext.inputMethodManager
import com.maunc.randomcallname.ext.launch
import com.maunc.randomcallname.ext.loge

class NewGroupViewModel : BaseViewModel<BaseModel>() {

    private val handleEdit: Handler = Handler(Looper.getMainLooper())

    var showDeleteEditIcon = MutableLiveData(false)
    var showNameLimitTips = MutableLiveData(false)
    var newGroupSuccess = MutableLiveData<Boolean>()
    var nameLimitTips = MutableLiveData(GLOBAL_NONE_STRING)

    var newGroupName = MutableLiveData(GLOBAL_NONE_STRING)

    //EditText最多输入字数
    val newGroupEditMaxNum = 12

    fun initiateCreateNewGroupEvent() {
        if (newGroupName.value!!.isEmpty()) {
            handleShowTipsEvent(getString(R.string.new_group_edit_none_tips_text))
            return
        }
        launch({
            randomGroupDao.queryRandomNameGroup(newGroupName.value!!)
        }, {
            if (it == null) {
                createNewGroup()
            } else {
                handleShowTipsEvent(getString(R.string.new_group_edit_exist_tips_text))
            }
        })
    }

    /**
     * 创建新分组
     */
    private fun createNewGroup() {
        launch({
            randomGroupDao.insertRandomNameGroup(RandomNameGroup(newGroupName.value!!))
        }, {
            "createNewGroup Success:$it".loge()
            newGroupSuccess.value = true
        }, {
            "createNewGroup error:${it.message}  ${it.stackTrace}".loge()
        })
    }

    //仅处理需要展示时的状态
    private fun handleShowTipsEvent(message: String) {
        nameLimitTips.value = message
        showNameLimitTips.value = true
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