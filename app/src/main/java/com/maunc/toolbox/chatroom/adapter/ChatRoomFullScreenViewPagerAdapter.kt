package com.maunc.toolbox.chatroom.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ChatRoomFullScreenViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private lateinit var fragmentList: MutableList<Fragment>

    constructor(fm: FragmentManager, fragmentList: MutableList<Fragment>) : this(fm) {
        this.fragmentList = fragmentList
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(index: Int): Fragment {
        return fragmentList[index]
    }
}