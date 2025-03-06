package com.maunc.toolbox.chronograph.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.chronograph.adpater.ChronographAdapter
import com.maunc.toolbox.chronograph.viewmodel.ChronographMainViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.animateToAlpha
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.getDimensFloat
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
        mDatabind.chronographTimingRecycler.layoutManager = linearLayoutManager()
        mDatabind.chronographTimingRecycler.adapter = chronographAdapter
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
            mViewModel.leftControllerChronograph()
            if (!mViewModel.isChronograph()) {
                mViewModel.restoreUI(
                    mDatabind.chronographTimeTv,
                    mDatabind.chronographTimingRecycler
                )
            }
        }
        mDatabind.chronographControllerRightButton.clickScale {
            mViewModel.rightControllerChronograph()
        }
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