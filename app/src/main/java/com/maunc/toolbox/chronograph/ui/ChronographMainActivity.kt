package com.maunc.toolbox.chronograph.ui

import android.os.Bundle
import com.maunc.toolbox.chronograph.adpater.ChronographAdapter
import com.maunc.toolbox.chronograph.viewmodel.ChronographMainViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.databinding.ActivityChronographMainBinding

class ChronographMainActivity :
    BaseActivity<ChronographMainViewModel, ActivityChronographMainBinding>() {

    private var testString: String? = "ddd"

    private val chronographAdapter by lazy {
        ChronographAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.initHandler()
        mDatabind.main.setOnClickListener {
        }
        testString?.let {
            "不为空".loge()
        } ?: let {
            "为空".loge()
        }
    }

    override fun createObserver() {

    }
}