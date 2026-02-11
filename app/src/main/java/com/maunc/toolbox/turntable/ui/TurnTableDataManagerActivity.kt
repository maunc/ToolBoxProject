package com.maunc.toolbox.turntable.ui

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.obtainActivityIntent
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityTurnTableDataManagerBinding
import com.maunc.toolbox.turntable.constant.RESULT_SOURCE_FROM_TURN_NEW_TURN_TABLE_PAGE
import com.maunc.toolbox.turntable.viewmodel.TurnTableDataManagerViewModel

class TurnTableDataManagerActivity :
    BaseActivity<TurnTableDataManagerViewModel, ActivityTurnTableDataManagerBinding>() {

    private val turnTableDataMangerActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_SOURCE_FROM_TURN_NEW_TURN_TABLE_PAGE) {

        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title_manager_data_tv)
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_add)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            turnTableDataMangerActivityResult.launch(obtainActivityIntent(TurnTableEditDataActivity::class.java))
        }
        mDatabind.turnTableDataManageNewGroupTv.clickScale {
            turnTableDataMangerActivityResult.launch(obtainActivityIntent(TurnTableEditDataActivity::class.java))
        }
    }

    override fun createObserver() {
        mViewModel.turnTableDataList.observe(this) {

        }
    }
}