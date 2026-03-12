package com.maunc.toolbox.pushbox.ui.activity

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.flexboxLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityPushBoxPreViewBinding
import com.maunc.toolbox.pushbox.adapter.PushBoxGradleAdapter
import com.maunc.toolbox.pushbox.viewmodel.PushBoxPreViewViewModel

class PushBoxPreViewActivity :
    BaseActivity<PushBoxPreViewViewModel, ActivityPushBoxPreViewBinding>() {
    private val pushBoxGradleAdapter by lazy {
        PushBoxGradleAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                mDatabind.pushBoxPreViewGameView.setGateIndex(pos)
                setSelectIndex(pos)
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.push_box_preview_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.pushBoxPerViewGradleRecycler.layoutManager = flexboxLayoutManager()
        mDatabind.pushBoxPerViewGradleRecycler.adapter = pushBoxGradleAdapter
        pushBoxGradleAdapter.setList(mViewModel.initPreViewResultList())
    }

    override fun createObserver() {}
}