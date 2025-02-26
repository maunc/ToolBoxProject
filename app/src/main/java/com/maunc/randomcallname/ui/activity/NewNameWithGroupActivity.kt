package com.maunc.randomcallname.ui.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.maunc.randomcallname.R
import com.maunc.randomcallname.base.BaseActivity
import com.maunc.randomcallname.constant.GROUP_NAME_EXTRA
import com.maunc.randomcallname.constant.RESULT_SOURCE_FROM_NEW_NAME_WITH_GROUP_PAGE
import com.maunc.randomcallname.databinding.ActivityNewNameWithGroupBinding
import com.maunc.randomcallname.ext.afterTextChange
import com.maunc.randomcallname.ext.clickNoRepeat
import com.maunc.randomcallname.ext.enterActivityAnim
import com.maunc.randomcallname.ext.finishCurrentActivity
import com.maunc.randomcallname.ext.finishCurrentResultToActivity
import com.maunc.randomcallname.ext.loge
import com.maunc.randomcallname.ext.startRotation
import com.maunc.randomcallname.ext.stopRotation
import com.maunc.randomcallname.utils.KeyBroadUtils
import com.maunc.randomcallname.viewmodel.NewNameWithGroupViewModel

/**
 * 新建分组下名称页面
 */
class NewNameWithGroupActivity :
    BaseActivity<NewNameWithGroupViewModel, ActivityNewNameWithGroupBinding>() {

    private var mGroupName: String? = null

    private val backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishCurrentResultToActivity(
                resultCode = RESULT_SOURCE_FROM_NEW_NAME_WITH_GROUP_PAGE,
                exitAnim = R.anim.exit_new_group_anim
            )
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        enterActivityAnim(R.anim.enter_new_group_anim)
        mGroupName = intent?.extras?.getString(GROUP_NAME_EXTRA)
        mDatabind.newNameWithGroupViewModel = mViewModel
        mViewModel.newRandomNameToGroup.value = mGroupName
        mDatabind.newNameWithGroupEdit.isFocusable = true
        mDatabind.newNameWithGroupEdit.isFocusableInTouchMode = true
        mDatabind.newNameWithGroupEdit.requestFocus()
        mViewModel.showSoftInputKeyBoard(mDatabind.newNameWithGroupEdit)
        KeyBroadUtils.registerKeyBoardHeightListener(this) { keyBoardHeight ->
            mViewModel.updateNewGroupLayout(keyBoardHeight, mDatabind.newNameWithGroupMain)
        }
        mDatabind.newNameWithGroupCancelButton.setOnClickListener {
            if (mViewModel.whetherNewNameHasCreate.value!!) {
                finishCurrentResultToActivity(
                    resultCode = RESULT_SOURCE_FROM_NEW_NAME_WITH_GROUP_PAGE,
                    exitAnim = R.anim.exit_new_group_anim
                )
            } else {
                finishCurrentActivity()
            }
        }
        mDatabind.newNameWithGroupDeleteIv.setOnClickListener {
            mViewModel.clearEditText(mDatabind.newNameWithGroupEdit)
        }
        mDatabind.newNameWithGroupCreateButton.clickNoRepeat {
            mViewModel.initiateCreateNewNameWithGroupEvent()
        }
        mDatabind.newNameWithGroupEdit.afterTextChange { editStr ->
            mViewModel.showDeleteEditIcon.value = editStr.isNotEmpty()
            mViewModel.nameLimitTips.value =
                getString(R.string.new_name_with_group_edit_max_tips_text)
            mViewModel.showNameLimitTips.value = editStr.length >= mViewModel.newNameEditMaxNum
            mViewModel.handleTipsTextColor(editStr.length < mViewModel.newNameEditMaxNum)
            mViewModel.newRandomName.value = editStr
        }
        onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    override fun createObserver() {
        mViewModel.newNameWithLoading.observe(this) {
            if (it) {
                mDatabind.newNameWithGroupLoadingIv.startRotation()
            } else {
                mDatabind.newNameWithGroupLoadingIv.stopRotation()
            }
        }
        mViewModel.newRandomNameSuccess.observe(this) {
            if (it) {
                mViewModel.clearEditText(mDatabind.newNameWithGroupEdit)
            }
        }
    }

    override fun onDestroy() {
        KeyBroadUtils.unRegisterKeyBoardHeightListener(this)
        super.onDestroy()
    }
}