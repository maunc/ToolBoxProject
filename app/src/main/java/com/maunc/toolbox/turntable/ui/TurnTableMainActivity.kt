package com.maunc.toolbox.turntable.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityTurnTableMainBinding
import com.maunc.toolbox.turntable.viewmodel.TurnTableMainViewModel
import com.us.mauncview.TurnTableView

class TurnTableMainActivity : BaseActivity<TurnTableMainViewModel, ActivityTurnTableMainBinding>() {

    private val mTurnTableListener = object : TurnTableView.OnTurnTableEventListener {
        override fun onRotateStart() {

        }

        override fun onRotateEnd(content: String) {

        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.startTurnTable.clickScale {
            mDatabind.turnTableView.startMoveTurnTable()
        }
        mDatabind.turnTableView.setOnTurnTableListener(mTurnTableListener)
    }

    override fun createObserver() {

    }
}