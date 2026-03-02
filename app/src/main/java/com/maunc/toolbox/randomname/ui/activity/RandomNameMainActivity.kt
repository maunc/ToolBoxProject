package com.maunc.toolbox.randomname.ui.activity

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.addDrawLayoutListener
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainActivityIntent
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.commonbase.utils.ViewOffsetHelper
import com.maunc.toolbox.databinding.ActivityRandomNameMainBinding
import com.maunc.toolbox.randomname.adapter.RandomMainNotSelectAdapter
import com.maunc.toolbox.randomname.adapter.RandomMainSelectAdapter
import com.maunc.toolbox.randomname.adapter.RandomMainSwipeNameAdapter
import com.maunc.toolbox.randomname.constant.RANDOM_AUTO
import com.maunc.toolbox.randomname.constant.RANDOM_MANUAL
import com.maunc.toolbox.randomname.constant.RESULT_SOURCE_FROM_RANDOM_SETTING_PAGE
import com.maunc.toolbox.randomname.constant.RUN_STATUS_NONE
import com.maunc.toolbox.randomname.constant.RUN_STATUS_START
import com.maunc.toolbox.randomname.constant.RUN_STATUS_STOP
import com.maunc.toolbox.randomname.viewmodel.RandomNameMainViewModel

/**
 * 随机名称页面
 */
@SuppressLint("NewApi")
class RandomNameMainActivity :
    BaseActivity<RandomNameMainViewModel, ActivityRandomNameMainBinding>() {

    private val randomMainActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_SOURCE_FROM_RANDOM_SETTING_PAGE) {
            mViewModel.initRandomList()
        }
    }

    private val randomNameMainSwipeAdapter: RandomMainSwipeNameAdapter by lazy {
        RandomMainSwipeNameAdapter()
    }

    private val randomMainNotSelectAdapter: RandomMainNotSelectAdapter by lazy {
        RandomMainNotSelectAdapter()
    }

    private val randomMainSelectAdapter: RandomMainSelectAdapter by lazy {
        RandomMainSelectAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.randomNameMainViewModel = mViewModel
        // 初始化
        mViewModel.initViewModelConfig()
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.random_title_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            mViewModel.buttonClickLaunchVibrator()
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            mViewModel.buttonClickLaunchVibrator()
            randomMainActivityResult.launch(
                obtainActivityIntent(RandomSettingActivity::class.java)
            )
            mViewModel.endRandom()
        }
        mDatabind.commonToolBar.commonToolBarTitleTv.setOnLongClickListener {
            mViewModel.buttonClickLaunchVibrator()
            mDatabind.randomNameDrawerLayout.openDrawer(GravityCompat.END)
            return@setOnLongClickListener true
        }
        mDatabind.randomControlTv.clickScale {
            mViewModel.buttonClickLaunchVibrator()
            when (mViewModel.runRandomStatus.value) {
                RUN_STATUS_NONE, RUN_STATUS_STOP -> mViewModel.startRandom()
                RUN_STATUS_START -> {
                    when (appViewModel.randomNameRunType.value!!) {
                        RANDOM_MANUAL -> mViewModel.stopManualRandom()
                        RANDOM_AUTO -> mViewModel.stopAutoRandom()
                    }
                }
            }
        }
        mDatabind.randomControlResetSelectTv.clickScale {
            mViewModel.buttonClickLaunchVibrator()
            mViewModel.endRandom()
        }
        val viewOffsetHelper = ViewOffsetHelper(mDatabind.randomNameMainContentLayout)
        mDatabind.randomNameDrawerLayout.addDrawLayoutListener(onDrawerSlide = { view, slideOffset ->
            val offset = (view.measuredWidth * slideOffset).toInt()
            viewOffsetHelper.setLeftAndRightOffset(-offset)
        })
        mDatabind.randomNameMainSwipeRecycler.layoutManager =
            linearLayoutManager(LinearLayoutManager.VERTICAL)
        mDatabind.randomNameMainSwipeRecycler.adapter = randomNameMainSwipeAdapter
        mDatabind.randomMainNotSelectRecycler.setAdapter(randomMainNotSelectAdapter)
        mDatabind.randomMainSelectRecycler.setAdapter(randomMainSelectAdapter)
    }

    override fun onBackPressCallBack() {
        if (mDatabind.randomNameDrawerLayout.isDrawerOpen(mDatabind.randomNameMainSwipeContentLayout)) {
            mDatabind.randomNameDrawerLayout.closeDrawers()
        } else {
            finishCurrentActivity()
        }
    }

    override fun createObserver() {
        mViewModel.randomGroupValue.observe(this) {
            randomNameMainSwipeAdapter.setList(it)
        }
        mViewModel.selectNameList.observe(this) {
            randomMainSelectAdapter.clearAddAll(it)
        }
        mViewModel.notSelectNameList.observe(this) {
            randomMainNotSelectAdapter.clearAddAll(it)
        }
        appViewModel.randomNameResultTextSize.observe(this) {
            mDatabind.randomNameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, it.toFloat())
        }
        appViewModel.randomNameResultIsBold.observe(this) {
            if (it && mDatabind.randomNameTv.typeface != Typeface.DEFAULT_BOLD) {
                mDatabind.randomNameTv.typeface = Typeface.DEFAULT_BOLD
            } else {
                mDatabind.randomNameTv.typeface = Typeface.DEFAULT
            }
        }
        appViewModel.randomNameRunRepeat.observe(this) {
            mDatabind.randomControlResetSelectTv.visibleOrGone(!it)
        }
        appViewModel.randomNameShowSelectRecycler.observe(this) {
            mDatabind.randomMainSelectLayout.visibleOrGone(it)
            mDatabind.randomMainNotSelectLayout.visibleOrGone(it)
        }
        appViewModel.initRandomNameConfig()
    }
}