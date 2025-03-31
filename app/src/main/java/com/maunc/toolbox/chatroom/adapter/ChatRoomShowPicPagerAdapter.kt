package com.maunc.toolbox.chatroom.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ChatRoomShowPicPagerAdapter(
    fm: FragmentManager, behavior: Int,
) : FragmentStatePagerAdapter(fm, behavior) {

    private lateinit var fragmentList: MutableList<Fragment>

    constructor(
        fm: FragmentManager,
        behavior: Int,
        fragmentList: MutableList<Fragment>,
    ) : this(fm, behavior) {
        this.fragmentList = fragmentList
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(index: Int): Fragment {
        return fragmentList[index]
    }
}