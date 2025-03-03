package com.maunc.toolbox.ui.activity

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.base.BaseActivity
import com.maunc.toolbox.constant.SELECT_GROUP_TO_MAIN_DIALOG
import com.maunc.toolbox.databinding.ActivityWelcomeBinding
import com.maunc.toolbox.ext.clickScale
import com.maunc.toolbox.ext.developmentToast
import com.maunc.toolbox.ext.finishCurrentActivity
import com.maunc.toolbox.ext.startTargetActivity
import com.maunc.toolbox.ui.dialog.SelectGroupToMainDialog
import com.maunc.toolbox.viewmodel.WelcomeViewModel

/**
 * 首页欢迎页面
 */
class WelcomeActivity : BaseActivity<WelcomeViewModel, ActivityWelcomeBinding>() {

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