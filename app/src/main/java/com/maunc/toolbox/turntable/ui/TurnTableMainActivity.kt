package com.maunc.toolbox.turntable.ui

import android.os.Bundle
import android.util.Log
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.obtainScreenHeight
import com.maunc.toolbox.commonbase.ext.obtainScreenWidth
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityTurnTableMainBinding
import com.maunc.toolbox.turntable.viewmodel.TurnTableMainViewModel

class TurnTableMainActivity : BaseActivity<TurnTableMainViewModel, ActivityTurnTableMainBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
//        Log.e("ww","手机宽度${obtainScreenWidth()},手机高度:${obtainScreenHeight()}")
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
    }

    override fun createObserver() {

    }
}