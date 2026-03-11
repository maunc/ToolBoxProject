package com.maunc.toolbox.pushbox.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.databinding.ActivityPushBoxMainBinding
import com.maunc.toolbox.pushbox.viewmodel.PushBoxMainViewModel

class PushBoxMainActivity : BaseActivity<PushBoxMainViewModel, ActivityPushBoxMainBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.tool_box_item_push_box_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            startTargetActivity(PushBoxSettingActivity::class.java)
        }
        mDatabind.pushBoxControllerUp.clickScale {
            mDatabind.pushBoxGameView.moveUp()
        }
        mDatabind.pushBoxControllerDown.clickScale {
            mDatabind.pushBoxGameView.moveDown()
        }
        mDatabind.pushBoxControllerLeft.clickScale {
            mDatabind.pushBoxGameView.moveLeft()
        }
        mDatabind.pushBoxControllerRight.clickScale {
            mDatabind.pushBoxGameView.moveRight()
        }
    }

    override fun createObserver() {

    }
}