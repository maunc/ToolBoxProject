package com.maunc.toolbox.turntable.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
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

        override fun onRotateIng(content: String, posIndex: Int) {
            if (content.isNotEmpty()) {
                mViewModel.playTurnTableSoundSafely(posIndex)
                mViewModel.turnTableAnimSelectContent.value = content
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.turnTableMainViewModel = mViewModel
        // 初始化
        mViewModel.initViewModelConfig()
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
            mDatabind.turnTableView.startTurnTable()
        }
        mDatabind.turnTableListIcon.clickScale {
            startTargetActivity(TurnTableDataManagerActivity::class.java)
        }
        mDatabind.turnTableView.setOnTurnTableListener(mTurnTableListener)
        mDatabind.turnTableLoggerRec.layoutManager = linearLayoutManager()
        mDatabind.turnTableLoggerRec.adapter = turnTableLoggerAdapter
        turnTableLoggerAdapter.addTipsLogger("欢迎体验转盘")
    }

    override fun createObserver() {
        appViewModel.turnTableColorIndex.observe(this) {
            mDatabind.turnTableView.setTurnTableColor(
                appViewModel.turnTableBuiltinColorData.value!![it].colorList
            )
        }
        appViewModel.turnTableTouch.observe(this) {
            mDatabind.turnTableView.setEnableTouch(it)
        }
        mViewModel.currentSelectData.observe(this) {
            mDatabind.turnTableView.setTurnTableContents(it)
        }
        appViewModel.initTurnTableConfig()
    }

    override fun onPause() {
        super.onPause()
        mDatabind.turnTableView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mDatabind.turnTableView.onResume()
        mViewModel.initSelectTurnTableData()
    }
}