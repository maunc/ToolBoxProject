package com.maunc.toolbox.randomname.ui.activity

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_DIALOG
import com.maunc.toolbox.commonbase.constant.ONE_DELAY_S
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.commonbase.ui.dialog.CommonLoadingDialog
import com.maunc.toolbox.databinding.ActivityRandomSettingBinding
import com.maunc.toolbox.randomname.adapter.RandomSettingAdapter
import com.maunc.toolbox.randomname.viewmodel.RandomSettingViewModel

/**
 * 设置页面
 */
class RandomSettingActivity : BaseActivity<RandomSettingViewModel, ActivityRandomSettingBinding>() {

    private val commonLoadingDialog by lazy {
        CommonLoadingDialog()
    }

    private val commonDialog by lazy {
        CommonDialog().setTitle(obtainString(R.string.random_setting_random_delete_all_data_text))
    }

    private val randomSettingAdapter: RandomSettingAdapter by lazy {
        RandomSettingAdapter().apply {
            setSettingAdapterDeleteAllListener {
                commonDialog.setSureListener {
                    commonDialog.dismissAllowingStateLoss()
                    commonLoadingDialog.show(
                        supportFragmentManager,
                        COMMON_DIALOG
                    )
                    mViewModel.deleteAllData {
                        mViewModel.handler?.postDelayed({
                            commonLoadingDialog.dismissAllowingStateLoss()
                        }, ONE_DELAY_S)
                    }
                }.show(supportFragmentManager, COMMON_DIALOG)
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.randomSettingViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.random_setting_text)
        mDatabind.commonToolBar.commonToolBarBackButton.setOnClickListener {
            mViewModel.buttonClickLaunchVibrator()
            finishCurrentActivity()
        }
        mDatabind.randomSettingRecycler.layoutManager = linearLayoutManager()
        mDatabind.randomSettingRecycler.adapter = randomSettingAdapter
        randomSettingAdapter.setList(mViewModel.initRecyclerData())
    }

    override fun createObserver() {

    }
}