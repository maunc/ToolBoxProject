package com.maunc.randomcallname.ui.activity

import android.os.Bundle
import com.maunc.randomcallname.R
import com.maunc.randomcallname.base.BaseActivity
import com.maunc.randomcallname.constant.SELECT_GROUP_TO_MAIN_DIALOG
import com.maunc.randomcallname.databinding.ActivityWelcomeBinding
import com.maunc.randomcallname.ext.clickScale
import com.maunc.randomcallname.ext.developmentToast
import com.maunc.randomcallname.ext.finishCurrentActivity
import com.maunc.randomcallname.ext.startTargetActivity
import com.maunc.randomcallname.ui.dialog.SelectGroupToMainDialog
import com.maunc.randomcallname.viewmodel.WelcomeViewModel

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
//            startTargetActivity(SettingActivity::class.java)
            developmentToast()
        }
        mDatabind.welcomeStartManageGroupTv.clickScale {
            startTargetActivity(ManageGroupActivity::class.java)
        }
    }

    override fun createObserver() {

    }
}