package com.maunc.randomcallname.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.maunc.randomcallname.R
import com.maunc.randomcallname.base.BaseActivity
import com.maunc.randomcallname.constant.GROUP_WITH_NAME_EXTRA
import com.maunc.randomcallname.constant.RUN_STATUS_NONE
import com.maunc.randomcallname.constant.RUN_STATUS_START
import com.maunc.randomcallname.constant.RUN_STATUS_STOP
import com.maunc.randomcallname.database.table.RandomNameWithGroup
import com.maunc.randomcallname.databinding.ActivityMainBinding
import com.maunc.randomcallname.ext.clickScale
import com.maunc.randomcallname.ext.finishCurrentActivity
import com.maunc.randomcallname.ext.loge
import com.maunc.randomcallname.utils.ViewOffsetHelper
import com.maunc.randomcallname.viewmodel.MainViewModel

/**
 * 随机名称页面
 */
@SuppressLint("NewApi")
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.mainViewModel = mViewModel
        intent?.extras?.getSerializable(
            GROUP_WITH_NAME_EXTRA,
            RandomNameWithGroup::class.java
        )?.let {
            mViewModel.randomGroupValue.value = it.randomNameDataList
        }
        mViewModel.initHandler()
        mDatabind.commonToolBar.commonToolBarBackButton.setOnClickListener {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_main_tool)
        mDatabind.commonToolBar.commonToolBarCompatButton.setOnClickListener {
            mDatabind.randomNameDrawerLayout.openDrawer(Gravity.END)
        }
        mDatabind.randomControlTv.clickScale {
            when (mViewModel.runRandomStatus.value) {
                RUN_STATUS_NONE, RUN_STATUS_STOP -> {
                    mViewModel.runRandomStatus.value = RUN_STATUS_START
                    mViewModel.startRandom()
                }

                RUN_STATUS_START -> {
                    mViewModel.runRandomStatus.value = RUN_STATUS_STOP
                    mViewModel.stopRandom()
                }
            }
        }
        val viewOffsetHelper = ViewOffsetHelper(mDatabind.randomNameMainContentLayout)
        mDatabind.randomNameDrawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val offset = (drawerView.measuredWidth * slideOffset).toInt()
                viewOffsetHelper.setLeftAndRightOffset(-offset)
            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })
    }

    override fun createObserver() {
    }
}