package com.maunc.toolbox.chatroom.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.adapter.ChatRoomShowPicPagerAdapter
import com.maunc.toolbox.chatroom.constant.FULL_SCREEN_IMAGE_DATA_EXTRA
import com.maunc.toolbox.chatroom.constant.FULL_SCREEN_IMAGE_POS_EXTRA
import com.maunc.toolbox.chatroom.data.ChatImageData
import com.maunc.toolbox.chatroom.ui.fragment.ChatRoomImageFragment
import com.maunc.toolbox.chatroom.viewmodel.ChatRoomShowPicViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.enterActivityAnim
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.databinding.ActivityChatRoomShowPicBinding
import java.lang.reflect.Type

@SuppressLint("NewApi")
class ChatRoomShowPicActivity :
    BaseActivity<ChatRoomShowPicViewModel, ActivityChatRoomShowPicBinding>() {

    private var showImageFragments: MutableList<Fragment> = mutableListOf()

    override fun initView(savedInstanceState: Bundle?) {
        enterActivityAnim(R.anim.enter_new_data_page_anim)
        mDatabind.chatRoomShowPicViewModel = mViewModel
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR)
            .transparentBar().init()
        val currentPos = intent?.extras?.getInt(FULL_SCREEN_IMAGE_POS_EXTRA)
        intent?.extras?.getString(FULL_SCREEN_IMAGE_DATA_EXTRA)?.let {
            val type: Type = object : TypeToken<MutableList<ChatImageData>>() {}.type
            val imageDataMutableList = Gson().fromJson<MutableList<ChatImageData>>(it, type)
            imageDataMutableList.forEach { chatImageData ->
                showImageFragments.add(ChatRoomImageFragment.newInstance(chatImageData))
            }
            mDatabind.chatRoomShowImageViewPager.adapter = ChatRoomShowPicPagerAdapter(
                supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, showImageFragments
            )
            mDatabind.chatRoomShowImageViewPager.currentItem = currentPos!!
        }
        mDatabind.chatRoomShowImageBackButton.setOnClickListener {
            baseFinishCurrentActivity()
        }
        mDatabind.chatRoomShowImageToolButton.setOnClickListener {}
    }

    override fun onBackPressCallBack() {
        baseFinishCurrentActivity()
    }

    private fun baseFinishCurrentActivity() {
        finishCurrentActivity(exitAnim = R.anim.exit_new_data_page_anim)
    }

    override fun createObserver() {}
}