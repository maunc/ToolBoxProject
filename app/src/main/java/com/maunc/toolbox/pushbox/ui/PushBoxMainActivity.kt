package com.maunc.toolbox.pushbox.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.databinding.ActivityPushBoxMainBinding
import com.maunc.toolbox.pushbox.view.PushBoxGameView
import com.maunc.toolbox.pushbox.viewmodel.PushBoxMainViewModel

@SuppressLint("SetTextI18n")
class PushBoxMainActivity : BaseActivity<PushBoxMainViewModel, ActivityPushBoxMainBinding>() {

    private val onPushBoxEventListener = object : PushBoxGameView.OnPushBoxEventListener {

        override fun onCurrentGradeMoveNumber(currentNumber: Int) {
            mDatabind.pushBoxMainCurrentMoveNumTv.text = "当前关卡移动次数:$currentNumber"
        }

        override fun onNextGrade(mapIndex: Int, map: ArrayList<ArrayList<Int>>) {
            handlePushBoxMainUI(mapIndex)
        }

        override fun onNotGrade() {

        }
    }

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
        handlePushBoxMainUI(0)
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
        mDatabind.pushBoxGameView.setOnPushBoxEventListener(onPushBoxEventListener)
    }

    private fun handlePushBoxMainUI(currentIndex: Int) {
        mDatabind.pushBoxMainCurrentIndexTv.text = "第${currentIndex + 1}关"
        mDatabind.pushBoxGameView.setGateIndex(currentIndex)
        mDatabind.pushBoxMainCurrentMoveNumTv.text = "当前关卡移动次数:0"
    }

    override fun createObserver() {

    }
}