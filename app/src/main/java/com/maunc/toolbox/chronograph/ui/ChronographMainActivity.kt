package com.maunc.toolbox.chronograph.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.chronograph.adpater.ChronographAdapter
import com.maunc.toolbox.chronograph.viewmodel.ChronographMainViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.databinding.ActivityChronographMainBinding

class ChronographMainActivity :
    BaseActivity<ChronographMainViewModel, ActivityChronographMainBinding>() {

    private val chronographAdapter by lazy {
        ChronographAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.chronographViewModel = mViewModel
        mViewModel.initHandler()
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.chronograph_tool_bar_title)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.chronographStartTimeButton.clickScale {
            mViewModel.startChronograph()
        }
        mDatabind.chronographControllerLeftButton.clickScale {
            if (mViewModel.isChronograph() && !mViewModel.isScaleAnim()) {
                mViewModel.startRankAfterUI(
                    mDatabind.chronographTimeTv,
                    mDatabind.chronographTimingRecycler
                )
            }
            if (!mViewModel.isChronograph() && mViewModel.isScaleAnim()) {
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
    }
}