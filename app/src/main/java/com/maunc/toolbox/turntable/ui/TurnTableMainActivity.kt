package com.maunc.toolbox.turntable.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.databinding.ActivityTurnTableMainBinding
import com.maunc.toolbox.turntable.adapter.TurnTableLoggerAdapter
import com.maunc.toolbox.turntable.viewmodel.TurnTableMainViewModel
import com.us.mauncview.TurnTableView

class TurnTableMainActivity : BaseActivity<TurnTableMainViewModel, ActivityTurnTableMainBinding>() {

    private val turnTableLoggerAdapter by lazy {
        TurnTableLoggerAdapter()
    }

    private val mTurnTableListener = object : TurnTableView.OnTurnTableEventListener {
        override fun onRotateStart() {

        }

        override fun onRotateEnd(content: String) {
            turnTableLoggerAdapter.addResultLogger(content)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title)
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            startTargetActivity(TurnTableSettingActivity::class.java)
        }
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.startTurnTable.clickScale {
            mDatabind.turnTableView.startMoveTurnTable()
        }
        mDatabind.turnTableView.setOnTurnTableListener(mTurnTableListener)
        mDatabind.turnTableLoggerRec.layoutManager = linearLayoutManager()
        mDatabind.turnTableLoggerRec.adapter = turnTableLoggerAdapter
        showTipsLayout()
    }

    private fun showTipsLayout() {
        val buildString = buildString {
            mDatabind.turnTableView.getContentList().forEach {
                append("$it  ")
            }
        }
        turnTableLoggerAdapter.addTipsLogger("转盘数据如下: $buildString")
    }

    override fun createObserver() {

    }
}