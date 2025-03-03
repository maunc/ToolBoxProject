package com.maunc.toolbox.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import com.maunc.toolbox.R
import com.maunc.toolbox.base.BaseActivity
import com.maunc.toolbox.constant.GROUP_WITH_NAME_EXTRA
import com.maunc.toolbox.constant.RUN_STATUS_NONE
import com.maunc.toolbox.constant.RUN_STATUS_START
import com.maunc.toolbox.constant.RUN_STATUS_STOP
import com.maunc.toolbox.database.table.RandomNameWithGroup
import com.maunc.toolbox.databinding.ActivityMainBinding
import com.maunc.toolbox.ext.clickScale
import com.maunc.toolbox.ext.finishCurrentActivity
import com.maunc.toolbox.utils.ViewOffsetHelper
import com.maunc.toolbox.viewmodel.MainViewModel

/**
 * 随机名称页面
 */
@SuppressLint("NewApi")
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    private val backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (mDatabind.randomNameDrawerLayout.isDrawerOpen(mDatabind.randomNameMainSwipeContentLayout)) {
                mDatabind.randomNameDrawerLayout.closeDrawers()
            } else {
                finishCurrentActivity()
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.mainViewModel = mViewModel
        intent?.extras?.getSerializable(
            GROUP_WITH_NAME_EXTRA,
            RandomNameWithGroup::class.java
        )?.let {
            mViewModel.randomGroupValue.value = it.randomNameDataList
        }
        mViewModel.initHandler()
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_main_tool)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            mViewModel.endRandom()
            mDatabind.randomNameDrawerLayout.openDrawer(Gravity.END)
        }
        mDatabind.randomControlTv.clickScale {
            when (mViewModel.runRandomStatus.value) {
                RUN_STATUS_NONE, RUN_STATUS_STOP -> {
                    mViewModel.startRandom()
                }

                RUN_STATUS_START -> {
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
        onBackPressedDispatcher.addCallback(backPressCallback)
    }

    override fun createObserver() {
    }
}