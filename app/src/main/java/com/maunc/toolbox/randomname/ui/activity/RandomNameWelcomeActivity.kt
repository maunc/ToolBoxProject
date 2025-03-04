package com.maunc.toolbox.randomname.ui.activity

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.developmentToast
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.databinding.ActivityRandomNameWelcomeBinding
import com.maunc.toolbox.randomname.constant.SELECT_GROUP_TO_MAIN_DIALOG
import com.maunc.toolbox.randomname.ui.dialog.SelectGroupToMainDialog
import com.maunc.toolbox.randomname.viewmodel.RandomNameWelcomeViewModel

/**
 * 首页欢迎页面
 */
class RandomNameWelcomeActivity :
    BaseActivity<RandomNameWelcomeViewModel, ActivityRandomNameWelcomeBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.welcomeViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.welcomeStartRandomTv.clickScale {
            SelectGroupToMainDialog().show(supportFragmentManager, SELECT_GROUP_TO_MAIN_DIALOG)
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            developmentToast()
        }
        mDatabind.welcomeStartManageGroupTv.clickScale {
            startTargetActivity(ManageGroupActivity::class.java)
        }
        mDatabind.welcomeStartTestTv.clickScale {
            developmentToast()
        }
    }

    override fun createObserver() {

    }
}