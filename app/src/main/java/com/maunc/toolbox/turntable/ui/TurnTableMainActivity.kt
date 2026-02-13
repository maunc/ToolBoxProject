package com.maunc.toolbox.turntable.ui

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainActivityIntentPutData
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityTurnTableMainBinding
import com.maunc.toolbox.turntable.adapter.TurnTableLoggerAdapter
import com.maunc.toolbox.turntable.constant.RESULT_SOURCE_FROM_TURN_TABLE_SETTING_PAGE
import com.maunc.toolbox.turntable.ui.TurnTableSettingActivity.Companion.TURN_TABLE_ENABLE_TOUCH
import com.maunc.toolbox.turntable.viewmodel.TurnTableMainViewModel
import com.us.mauncview.TurnTableView

class TurnTableMainActivity : BaseActivity<TurnTableMainViewModel, ActivityTurnTableMainBinding>() {

    private val turnTableActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_SOURCE_FROM_TURN_TABLE_SETTING_PAGE) {
            val intent = it.data!!
            mViewModel.initSettingConfig(
                enableTouch = intent.getBooleanExtra(TURN_TABLE_ENABLE_TOUCH, false)
            )
        }
    }

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
        mDatabind.turnTableMainViewModel = mViewModel
        // 初始化
        mViewModel.initViewModelConfig()
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title)
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            turnTableActivityResult.launch(obtainActivityIntentPutData(
                TurnTableSettingActivity::class.java,
                mutableMapOf<String, Any>().apply {
                    put(TURN_TABLE_ENABLE_TOUCH, mViewModel.turnTableIsEnableTouch.value!!)
                }
            ))
        }
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.startTurnTable.clickScale {
            mDatabind.turnTableView.startTurnTable()
        }
        mDatabind.turnTableView.setOnTurnTableListener(mTurnTableListener)
        mDatabind.turnTableLoggerRec.layoutManager = linearLayoutManager()
        mDatabind.turnTableLoggerRec.adapter = turnTableLoggerAdapter
        showTipsLayout()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.initSelectTurnTableData()
    }

    private fun showTipsLayout() {
        val buildString = buildString {
            mDatabind.turnTableView.getTurnTableContents().forEach {
                append("$it  ")
            }
        }
        turnTableLoggerAdapter.addTipsLogger("转盘数据如下: $buildString")
    }

    override fun createObserver() {
        mViewModel.currentSelectData.observe(this) {
            mDatabind.turnTableView.setTurnTableContents(it)
        }
    }
}