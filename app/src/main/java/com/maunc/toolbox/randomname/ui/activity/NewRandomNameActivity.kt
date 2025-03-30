package com.maunc.toolbox.randomname.ui.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.addEditTextListener
import com.maunc.toolbox.commonbase.ext.clickNoRepeat
import com.maunc.toolbox.commonbase.ext.enterActivityAnim
import com.maunc.toolbox.commonbase.ext.finishCurrentResultToActivity
import com.maunc.toolbox.commonbase.ext.obtainIntentPutData
import com.maunc.toolbox.commonbase.ext.showSoftInputKeyBoard
import com.maunc.toolbox.commonbase.utils.KeyBroadUtils
import com.maunc.toolbox.databinding.ActivityNewRandomNameBinding
import com.maunc.toolbox.randomname.constant.GROUP_NAME_EXTRA
import com.maunc.toolbox.randomname.constant.RESULT_SOURCE_FROM_NEW_NAME_WITH_GROUP_PAGE
import com.maunc.toolbox.randomname.constant.WHETHER_DATA_HAS_CHANGE
import com.maunc.toolbox.randomname.viewmodel.NewRandomNameViewModel

/**
 * 新建分组下名称页面
 */
class NewRandomNameActivity :
    BaseActivity<NewRandomNameViewModel, ActivityNewRandomNameBinding>() {

    private var mGroupName: String? = null

    private val backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            baseFinishCurrentActivity()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        enterActivityAnim(R.anim.enter_new_data_page_anim)
        mGroupName = intent?.extras?.getString(GROUP_NAME_EXTRA)
        mDatabind.newNameWithGroupViewModel = mViewModel
        mViewModel.newRandomNameToGroup.value = mGroupName
        mDatabind.newNameWithGroupEdit.isFocusable = true
        mDatabind.newNameWithGroupEdit.isFocusableInTouchMode = true
        mDatabind.newNameWithGroupEdit.requestFocus()
        showSoftInputKeyBoard(mDatabind.newNameWithGroupEdit)
        KeyBroadUtils.registerKeyBoardHeightListener(this) { keyBoardHeight ->
            mViewModel.updateNewGroupLayout(keyBoardHeight, mDatabind.newNameWithGroupMain)
        }
        mDatabind.newNameWithGroupCancelButton.setOnClickListener {
            mViewModel.buttonClickSoundEffect()
            baseFinishCurrentActivity()
        }
        mDatabind.newNameWithGroupDeleteIv.setOnClickListener {
            mViewModel.buttonClickSoundEffect()
            mViewModel.clearEditText(mDatabind.newNameWithGroupEdit)
        }
        mDatabind.newNameWithGroupCreateButton.clickNoRepeat {
            mViewModel.buttonClickSoundEffect()
            mViewModel.initiateCreateNewNameWithGroupEvent()
        }
        mDatabind.newNameWithGroupEdit.addEditTextListener(afterTextChanged = { editStr ->
            mViewModel.showDeleteEditIcon.value = editStr.isNotEmpty()
            mViewModel.nameLimitTips.value =
                getString(R.string.new_name_with_group_edit_max_tips_text)
            mViewModel.showNameLimitTips.value = editStr.length >= mViewModel.newNameEditMaxNum
            mViewModel.handleTipsTextColor(editStr.length < mViewModel.newNameEditMaxNum)
            mViewModel.newRandomName.value = editStr
        })
        onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    override fun createObserver() {
        mViewModel.newRandomNameSuccess.observe(this) {
            if (it) {
                mViewModel.clearEditText(mDatabind.newNameWithGroupEdit)
            }
        }
    }

    private fun baseFinishCurrentActivity() {
        finishCurrentResultToActivity(
            resultCode = RESULT_SOURCE_FROM_NEW_NAME_WITH_GROUP_PAGE,
            intent = obtainIntentPutData(mutableMapOf<String, Any>().apply {
                put(WHETHER_DATA_HAS_CHANGE, mViewModel.whetherDataHasChange.value!!)
            }),
            exitAnim = R.anim.exit_new_data_page_anim
        )
    }

    override fun onDestroy() {
        KeyBroadUtils.unRegisterKeyBoardHeightListener(this)
        super.onDestroy()
    }
}