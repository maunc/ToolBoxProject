package com.maunc.randomcallname.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.maunc.randomcallname.R
import com.maunc.randomcallname.base.BaseActivity
import com.maunc.randomcallname.constant.GLOBAL_NONE_STRING
import com.maunc.randomcallname.databinding.ActivityNewGroupBinding
import com.maunc.randomcallname.ext.afterTextChange
import com.maunc.randomcallname.ext.clickNoRepeat
import com.maunc.randomcallname.ext.enterActivityAnim
import com.maunc.randomcallname.ext.exitActivityAnim
import com.maunc.randomcallname.ext.loge
import com.maunc.randomcallname.utils.KeyBroadUtils
import com.maunc.randomcallname.viewmodel.NewGroupViewModel

class NewGroupActivity : BaseActivity<NewGroupViewModel, ActivityNewGroupBinding>() {

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        enterActivityAnim(R.anim.enter_new_group_anim)
        mDatabind.newGroupViewModel = mViewModel
        mDatabind.newGroupEdit.isFocusable = true
        mDatabind.newGroupEdit.isFocusableInTouchMode = true
        mDatabind.newGroupEdit.requestFocus()
        mViewModel.showSoftInputKeyBoard(mDatabind.newGroupEdit)
        KeyBroadUtils.registerKeyBoardHeightListener(this) { keyBoardHeight ->
            "keyBroadHeight:$keyBoardHeight".loge()
            mViewModel.updateNewGroupLayout(keyBoardHeight, mDatabind.newGroupMain)
        }
        mDatabind.newGroupCancelButton.setOnClickListener {
            finish()
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
    }

    override fun createObserver() {
        mViewModel.newGroupSuccess.observe(this) {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exitActivityAnim(R.anim.exit_new_group_anim)
    }

    override fun onDestroy() {
        KeyBroadUtils.unRegisterKeyBoardHeightListener(this)
        super.onDestroy()
    }
}