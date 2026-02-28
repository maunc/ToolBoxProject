package com.maunc.toolbox.chronograph.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.maunc.toolbox.R
import com.maunc.toolbox.chronograph.adpater.ChronographAdapter
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_NONE
import com.maunc.toolbox.chronograph.constant.DEF_TIME_TEXT
import com.maunc.toolbox.chronograph.constant.timeUnitMillion
import com.maunc.toolbox.chronograph.viewmodel.ChronographMainViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityChronographMainBinding
import kotlinx.coroutines.launch

class ChronographMainActivity :
    BaseActivity<ChronographMainViewModel, ActivityChronographMainBinding>() {

    private val chronographAdapter by lazy {
        ChronographAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        // Chronometer
        mDatabind.chronographViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.chronograph_tool_bar_title)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.chronographStartTimeButton.clickScale {
            mViewModel.middleControllerChronograph()
        }
        mDatabind.chronographControllerLeftButton.clickScale {
            if (mViewModel.isChronograph() && !mViewModel.mTimeTvIsScaleAnim.value!!) {
                mViewModel.startRankAfterUI(
                    mDatabind.chronographTimeTv,
                    mDatabind.chronographTimingRecycler
                )
            }
            if (!mViewModel.isChronograph() && mViewModel.mTimeTvIsScaleAnim.value!!) {
                mViewModel.restoreUI(
                    mDatabind.chronographTimeTv,
                    mDatabind.chronographTimingRecycler
                )
            }
            mViewModel.leftControllerChronograph()
        }
        mDatabind.chronographControllerRightButton.clickScale {
            mViewModel.rightControllerChronograph()
        }
        mDatabind.chronographTimingRecycler.layoutManager = linearLayoutManager()
        mDatabind.chronographTimingRecycler.adapter = chronographAdapter
    }

    override fun createObserver() {
        mViewModel.mRankChronographData.observe(this) { chronographData ->
            chronographData?.let {
                chronographAdapter.addChronograph(it)
            } ?: let {
                chronographAdapter.clearChronograph()
            }
        }
        mViewModel.mRunChronographStatus.observe(this) {
            if (it == CHRONOGRAPH_STATUS_NONE) mDatabind.chronographTimeTv.text = DEF_TIME_TEXT
        }
        // 监听值的变化
        lifecycleScope.launch {
            mViewModel.mChronographTimeValue.collect { totalMs ->
                val newText = totalMs.timeUnitMillion()
                if (mDatabind.chronographTimeTv.text.toString() != newText) {
                    mDatabind.chronographTimeTv.text = newText
                }
            }
        }
    }
}