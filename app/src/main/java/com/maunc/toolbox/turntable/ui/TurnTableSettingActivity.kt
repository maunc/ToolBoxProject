package com.maunc.toolbox.turntable.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_DIALOG
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.commonbase.ext.toastShort
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.turnTableAnimSoundEffect
import com.maunc.toolbox.commonbase.utils.turnTableEnableTouch
import com.maunc.toolbox.databinding.ActivityTurnTableSettingBinding
import com.maunc.toolbox.turntable.adapter.TurnTableSettingAdapter
import com.maunc.toolbox.turntable.viewmodel.TurnTableSettingViewModel

class TurnTableSettingActivity :
    BaseActivity<TurnTableSettingViewModel, ActivityTurnTableSettingBinding>() {

    private val deleteTipsDialog by lazy {
        CommonDialog().setTitle(
            obtainString(R.string.turn_table_setting_delete_all_tv)
        ).setSureListener {
            mViewModel.deleteAllData {
                toastShort(obtainString(R.string.common_tips_delete_success))
            }
        }
    }

    private val turnTableSettingAdapter by lazy {
        TurnTableSettingAdapter().apply {
            setOnTurnTableSettingListener(object :
                TurnTableSettingAdapter.OnTurnTableSettingEventListener {
                override fun configTurnTableTouch(isTouch: Boolean) {
                    obtainMMKV.putBoolean(turnTableEnableTouch, isTouch)
                    appViewModel.turnTableTouch.value = isTouch
                }

                override fun configTurnTableSoundEffect(isSound: Boolean) {
                    obtainMMKV.putBoolean(turnTableAnimSoundEffect, isSound)
                    appViewModel.turnTableSoundEffect.value = isSound
                }

                override fun showTurnTableDataPage() {
                    startTargetActivity(TurnTableBuiltinDataActivity::class.java)
                }

                override fun startDataMangerPage() {
                    startTargetActivity(TurnTableDataManagerActivity::class.java)
                }

                override fun startConfigColorPage() {
                    startTargetActivity(TurnTableSettingColorActivity::class.java)
                }

                override fun deleteAllTurnTableData() {
                    deleteTipsDialog.show(supportFragmentManager, COMMON_DIALOG)
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
            appViewModel.turnTableTouch.value ?: false,
            appViewModel.turnTableSoundEffect.value ?: false
        )
    }

    override fun createObserver() {

    }
}