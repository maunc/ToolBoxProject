package com.maunc.toolbox.turntable.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.finishCurrentResultToActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainIntentPutData
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.databinding.ActivityTurnTableSettingBinding
import com.maunc.toolbox.turntable.adapter.TurnTableSettingAdapter
import com.maunc.toolbox.turntable.constant.RESULT_SOURCE_FROM_TURN_TABLE_SETTING_PAGE
import com.maunc.toolbox.turntable.viewmodel.TurnTableSettingViewModel

class TurnTableSettingActivity :
    BaseActivity<TurnTableSettingViewModel, ActivityTurnTableSettingBinding>() {

    companion object {
        const val TURN_TABLE_ENABLE_TOUCH = "enableTouch"
    }

    private val turnTableSettingAdapter by lazy {
        TurnTableSettingAdapter().apply {
            setOnTurnTableSettingListener(object :
                TurnTableSettingAdapter.OnTurnTableSettingEventListener {
                override fun showTurnTableDataPage() {

                }

                override fun startDataMangerPage() {
                    startTargetActivity(TurnTableDataManagerActivity::class.java)
                }

                override fun deleteAllTurnTableData() {

                }

            })
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title_setting_tv)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.turnTableSettingRec.layoutManager = linearLayoutManager()
        mDatabind.turnTableSettingRec.adapter = turnTableSettingAdapter
        turnTableSettingAdapter.setList(mViewModel.initRecyclerData())
        turnTableSettingAdapter.setConfig(
            intent.getBooleanExtra(TURN_TABLE_ENABLE_TOUCH, false)
        )
    }

    override fun onBackPressCallBack() {
        baseFinishCurrentActivity()
    }

    private fun baseFinishCurrentActivity(action: () -> Unit = {}) {
        action()
        finishCurrentResultToActivity(
            resultCode = RESULT_SOURCE_FROM_TURN_TABLE_SETTING_PAGE,
            intent = obtainIntentPutData(mutableMapOf<String, Any>().apply {
                put(TURN_TABLE_ENABLE_TOUCH, turnTableSettingAdapter.obtainEnableTouch())
            })
        )
    }

    override fun createObserver() {

    }
}