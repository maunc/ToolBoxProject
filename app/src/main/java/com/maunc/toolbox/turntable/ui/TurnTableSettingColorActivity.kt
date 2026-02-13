package com.maunc.toolbox.turntable.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.turnTableConfigColor
import com.maunc.toolbox.databinding.ActivityTurnTableSettingColorBinding
import com.maunc.toolbox.turntable.adapter.TurnTableConfigColorAdapter
import com.maunc.toolbox.turntable.constant.configColorList
import com.maunc.toolbox.turntable.viewmodel.TurnTableSettingColorViewModel

@SuppressLint("NotifyDataSetChanged")
class TurnTableSettingColorActivity :
    BaseActivity<TurnTableSettingColorViewModel, ActivityTurnTableSettingColorBinding>() {
    private val turnTableConfigColorAdapter by lazy {
        TurnTableConfigColorAdapter().apply {
            setCurrentSelectIndex(obtainMMKV.getInt(turnTableConfigColor))
            setOnItemClickListener { adapter, view, index ->
                obtainMMKV.putInt(turnTableConfigColor, index)
                setCurrentSelectIndex(index)
                notifyDataSetChanged()
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title_setting_color)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.turnTableSettingColorRecycler.layoutManager = linearLayoutManager()
        mDatabind.turnTableSettingColorRecycler.adapter = turnTableConfigColorAdapter
        turnTableConfigColorAdapter.setList(configColorList)
    }

    override fun createObserver() {

    }
}