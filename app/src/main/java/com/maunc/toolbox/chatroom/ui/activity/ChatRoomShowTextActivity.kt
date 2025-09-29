package com.maunc.toolbox.chatroom.ui.activity

import android.os.Bundle
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.constant.TEXT_CONTENT_DATA_EXTRA
import com.maunc.toolbox.chatroom.constant.TEXT_SEND_TIME_DATA_EXTRA
import com.maunc.toolbox.chatroom.data.convertTime
import com.maunc.toolbox.chatroom.viewmodel.ChatRoomShowTextViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.enterActivityAnim
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.databinding.ActivityChatRoomShowTextBinding

class ChatRoomShowTextActivity :
    BaseActivity<ChatRoomShowTextViewModel, ActivityChatRoomShowTextBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        enterActivityAnim(R.anim.enter_new_data_page_anim)
        mDatabind.chatRoomShowTextViewModel = mViewModel
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR)
            .transparentBar().init()
        intent?.extras?.getString(TEXT_CONTENT_DATA_EXTRA)?.let {
            it.loge()
            mDatabind.showTextTv.text = it
        }
        intent?.extras?.getLong(TEXT_SEND_TIME_DATA_EXTRA)?.let {
            mDatabind.showTextSendTime.text = it.convertTime()
        }
        mDatabind.main.setOnClickListener {
            baseFinishCurrentActivity()
        }
    }

    override fun onBackPressCallBack() {
        baseFinishCurrentActivity()
    }

    private fun baseFinishCurrentActivity() {
        finishCurrentActivity(exitAnim = R.anim.exit_new_data_page_anim)
    }

    override fun createObserver() {}
}