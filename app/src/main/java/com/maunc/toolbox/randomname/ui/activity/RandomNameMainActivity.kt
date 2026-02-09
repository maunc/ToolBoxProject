package com.maunc.toolbox.randomname.ui.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.addDrawLayoutListener
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.obtainActivityIntentPutData
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.utils.ViewOffsetHelper
import com.maunc.toolbox.databinding.ActivityRandomNameMainBinding
import com.maunc.toolbox.randomname.adapter.RandomMainNotSelectAdapter
import com.maunc.toolbox.randomname.adapter.RandomMainSelectAdapter
import com.maunc.toolbox.randomname.adapter.RandomMainSwipeNameAdapter
import com.maunc.toolbox.randomname.constant.GROUP_WITH_NAME_EXTRA
import com.maunc.toolbox.randomname.constant.RANDOM_AUTO
import com.maunc.toolbox.randomname.constant.RANDOM_MANUAL
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MAX
import com.maunc.toolbox.randomname.constant.RESULT_SOURCE_FROM_RANDOM_SETTING_PAGE
import com.maunc.toolbox.randomname.constant.RUN_STATUS_NONE
import com.maunc.toolbox.randomname.constant.RUN_STATUS_START
import com.maunc.toolbox.randomname.constant.RUN_STATUS_STOP
import com.maunc.toolbox.randomname.database.table.RandomNameWithGroup
import com.maunc.toolbox.randomname.ui.activity.RandomSettingActivity.Companion.RUN_DELAY_TIME
import com.maunc.toolbox.randomname.ui.activity.RandomSettingActivity.Companion.RUN_RANDOM_TYPE
import com.maunc.toolbox.randomname.ui.activity.RandomSettingActivity.Companion.SHOW_SELECT_RECYCLER
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
        "${it.resultCode},${it.data}".loge()
        if (it.resultCode == RESULT_SOURCE_FROM_RANDOM_SETTING_PAGE) {
            val intent = it?.data!!
            mViewModel.initSettingConfig(
                type = intent.getIntExtra(RUN_RANDOM_TYPE, RANDOM_AUTO),
                speed = intent.getLongExtra(RUN_DELAY_TIME, RANDOM_SPEED_MAX),
                showSelectRec = intent.getBooleanExtra(SHOW_SELECT_RECYCLER, false)
            )
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
        obtainRandomNameSerializable()?.let {
            mViewModel.toGroupName.value = it.randomNameGroup.groupName
            mViewModel.randomGroupValue.value = it.randomNameDataList
            mViewModel.notSelectNameList.value = it.randomNameDataList
        }
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
                obtainActivityIntentPutData(
                    RandomSettingActivity::class.java,
                    mutableMapOf<String, Any>().apply {
                        put(RUN_RANDOM_TYPE, mViewModel.runRandomType.value!!)
                        put(RUN_DELAY_TIME, mViewModel.runDelayTime.value!!)
                        put(SHOW_SELECT_RECYCLER, mViewModel.showSelectRecycler.value!!)
                    }
                ))
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
                    when (mViewModel.runRandomType.value!!) {
                        RANDOM_MANUAL -> mViewModel.stopManualRandom()
                        RANDOM_AUTO -> mViewModel.stopAutoRandom()
                    }
                }
            }
        }
        mDatabind.randomControlResetSelectTv.clickScale {
            mViewModel.buttonClickLaunchVibrator()
            mViewModel.showDoneRandomTips.value = false
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

    private fun obtainRandomNameSerializable(): RandomNameWithGroup? {
        val randomData: RandomNameWithGroup? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.extras?.getSerializable(
                    GROUP_WITH_NAME_EXTRA,
                    RandomNameWithGroup::class.java
                )
            } else {
                intent?.extras?.getSerializable(
                    GROUP_WITH_NAME_EXTRA
                ) as RandomNameWithGroup
            }
        return randomData
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
    }
}