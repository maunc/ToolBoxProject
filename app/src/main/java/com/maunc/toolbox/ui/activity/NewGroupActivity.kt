package com.maunc.toolbox.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.maunc.toolbox.R
import com.maunc.toolbox.base.BaseActivity
import com.maunc.toolbox.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.constant.RESULT_SOURCE_FROM_NEW_GROUP_PAGE
import com.maunc.toolbox.constant.WHETHER_DATA_HAS_CHANGE
import com.maunc.toolbox.databinding.ActivityNewGroupBinding
import com.maunc.toolbox.ext.afterTextChange
import com.maunc.toolbox.ext.clickNoRepeat
import com.maunc.toolbox.ext.enterActivityAnim
import com.maunc.toolbox.ext.finishCurrentResultToActivity
import com.maunc.toolbox.ext.obtainIntentPutData
import com.maunc.toolbox.utils.KeyBroadUtils
import com.maunc.toolbox.viewmodel.NewGroupViewModel

/**
 * 新建分组页面
 */
class NewGroupActivity : BaseActivity<NewGroupViewModel, ActivityNewGroupBinding>() {

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
        mViewModel.showSoftInputKeyBoard(mDatabind.newGroupEdit)
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
        mDatabind.newGroupEdit.afterTextChange { editStr ->
            mViewModel.showDeleteEditIcon.value = editStr.isNotEmpty()
            mViewModel.nameLimitTips.value = getString(R.string.new_group_edit_max_tips_text)
            mViewModel.showNameLimitTips.value = editStr.length >= mViewModel.newGroupEditMaxNum
            mViewModel.newGroupName.value = editStr
        }
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