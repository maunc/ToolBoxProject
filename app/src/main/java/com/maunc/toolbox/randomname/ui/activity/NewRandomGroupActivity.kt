package com.maunc.toolbox.randomname.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.addEditTextListener
import com.maunc.toolbox.commonbase.ext.clickNoRepeat
import com.maunc.toolbox.commonbase.ext.enterActivityAnim
import com.maunc.toolbox.commonbase.ext.finishCurrentResultToActivity
import com.maunc.toolbox.commonbase.ext.obtainIntentPutData
import com.maunc.toolbox.commonbase.ext.showSoftInputKeyBoard
import com.maunc.toolbox.commonbase.utils.KeyBroadUtils
import com.maunc.toolbox.databinding.ActivityNewRandomGroupBinding
import com.maunc.toolbox.randomname.constant.RESULT_SOURCE_FROM_NEW_GROUP_PAGE
import com.maunc.toolbox.randomname.constant.WHETHER_DATA_HAS_CHANGE
import com.maunc.toolbox.randomname.viewmodel.NewRandomGroupViewModel

/**
 * 新建分组页面
 */
class NewRandomGroupActivity :
    BaseActivity<NewRandomGroupViewModel, ActivityNewRandomGroupBinding>() {

    private val backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            baseFinishCurrentActivity()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        enterActivityAnim(R.anim.enter_new_data_page_anim)
        mDatabind.newGroupViewModel = mViewModel
        mDatabind.newGroupEdit.isFocusable = true
        mDatabind.newGroupEdit.isFocusableInTouchMode = true
        mDatabind.newGroupEdit.requestFocus()
        showSoftInputKeyBoard(mDatabind.newGroupEdit)
        KeyBroadUtils.registerKeyBoardHeightListener(this) { keyBoardHeight ->
            mViewModel.updateNewGroupLayout(keyBoardHeight, mDatabind.newGroupMain)
        }
        mDatabind.newGroupCancelButton.setOnClickListener {
            baseFinishCurrentActivity()
        }
        mDatabind.newGroupDeleteIv.setOnClickListener {
            mDatabind.newGroupEdit.setText(GLOBAL_NONE_STRING)
        }
        mDatabind.newGroupCreateButton.clickNoRepeat {
            mViewModel.initiateCreateNewGroupEvent()
        }
        mDatabind.newGroupEdit.addEditTextListener(afterTextChanged = { editStr ->
            mViewModel.showDeleteEditIcon.value = editStr.isNotEmpty()
            mViewModel.nameLimitTips.value = getString(R.string.new_group_edit_max_tips_text)
            mViewModel.showNameLimitTips.value = editStr.length >= mViewModel.newGroupEditMaxNum
            mViewModel.newGroupName.value = editStr
        })
        onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    override fun createObserver() {
        mViewModel.whetherDataHasChange.observe(this) {
            if (it) {
                baseFinishCurrentActivity()
            }
        }
    }

    private fun baseFinishCurrentActivity(action: () -> Unit = {}) {
        action()
        finishCurrentResultToActivity(
            resultCode = RESULT_SOURCE_FROM_NEW_GROUP_PAGE,
            exitAnim = R.anim.exit_new_data_page_anim,
            intent = obtainIntentPutData(mutableMapOf<String, Any>().apply {
                put(WHETHER_DATA_HAS_CHANGE, mViewModel.whetherDataHasChange.value!!)
            })
        )
    }

    override fun onDestroy() {
        KeyBroadUtils.unRegisterKeyBoardHeightListener(this)
        super.onDestroy()
    }
}