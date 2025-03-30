package com.maunc.toolbox.randomname.ui.activity

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityRandomSettingBinding
import com.maunc.toolbox.randomname.adapter.RandomSettingAdapter
import com.maunc.toolbox.randomname.viewmodel.RandomSettingViewModel

/**
 * 设置页面
 */
class RandomSettingActivity : BaseActivity<RandomSettingViewModel, ActivityRandomSettingBinding>() {

    private val randomSettingAdapter: RandomSettingAdapter by lazy {
        RandomSettingAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.randomSettingViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.random_setting_text)
        mDatabind.commonToolBar.commonToolBarBackButton.setOnClickListener {
            mViewModel.buttonClickSoundEffect()
            finishCurrentActivity()
        }
        mDatabind.randomSettingRecycler.layoutManager = linearLayoutManager()
        mDatabind.randomSettingRecycler.adapter = randomSettingAdapter
        randomSettingAdapter.setList(mViewModel.initRecyclerData())
    }

    override fun createObserver() {

    }
}