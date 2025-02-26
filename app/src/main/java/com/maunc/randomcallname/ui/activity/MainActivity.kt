package com.maunc.randomcallname.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.maunc.randomcallname.R
import com.maunc.randomcallname.base.BaseActivity
import com.maunc.randomcallname.constant.GROUP_WITH_NAME_EXTRA
import com.maunc.randomcallname.constant.RUN_STATUS_NONE
import com.maunc.randomcallname.constant.RUN_STATUS_START
import com.maunc.randomcallname.constant.RUN_STATUS_STOP
import com.maunc.randomcallname.database.table.RandomNameWithGroup
import com.maunc.randomcallname.databinding.ActivityMainBinding
import com.maunc.randomcallname.ext.clickScale
import com.maunc.randomcallname.ext.developmentToast
import com.maunc.randomcallname.ext.startTargetActivity
import com.maunc.randomcallname.viewmodel.MainViewModel

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
            finish()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_main_tool)
        mDatabind.commonToolBar.commonToolBarCompatButton.setOnClickListener {
            developmentToast()
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
    }

    override fun createObserver() {
    }
}