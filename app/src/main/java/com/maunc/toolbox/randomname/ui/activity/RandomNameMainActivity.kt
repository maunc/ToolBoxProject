package com.maunc.toolbox.randomname.ui.activity

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.base.ext.addDrawLayoutListener
import com.maunc.base.ext.clickScale
import com.maunc.base.ext.finishCurrentActivity
import com.maunc.base.ext.flexboxLayoutManager
import com.maunc.base.ext.linearLayoutManager
import com.maunc.base.ext.obtainActivityIntent
import com.maunc.base.ext.obtainString
import com.maunc.base.ext.visibleOrGone
import com.maunc.base.ui.BaseActivity
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.commonbase.utils.ViewOffsetHelper
import com.maunc.toolbox.databinding.ActivityRandomNameMainBinding
import com.maunc.toolbox.randomname.adapter.RandomMainSelectAdapter
import com.maunc.toolbox.randomname.adapter.RandomSelectGroupWithNameAdapter
import com.maunc.toolbox.randomname.constant.RANDOM_AUTO
import com.maunc.toolbox.randomname.constant.RANDOM_MANUAL
import com.maunc.toolbox.randomname.constant.RESULT_SOURCE_FROM_RANDOM_SETTING_PAGE
import com.maunc.toolbox.randomname.constant.RUN_STATUS_NONE
import com.maunc.toolbox.randomname.constant.RUN_STATUS_START
import com.maunc.toolbox.randomname.constant.RUN_STATUS_STOP
import com.maunc.toolbox.randomname.database.table.RandomNameData
import com.maunc.toolbox.randomname.viewmodel.RandomNameMainViewModel

/**
 * 随机名称页面
 */
class RandomNameMainActivity :
    BaseActivity<RandomNameMainViewModel, ActivityRandomNameMainBinding>() {

    private val randomMainActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_SOURCE_FROM_RANDOM_SETTING_PAGE) {
            mViewModel.initRandomList()
        }
    }

    private val randomNameMainSwipeAdapter: RandomSelectGroupWithNameAdapter by lazy {
        RandomSelectGroupWithNameAdapter()
    }

    private val randomMainNotSelectAdapter: RandomMainSelectAdapter by lazy {
        RandomMainSelectAdapter()
    }

    private val randomMainSelectAdapter: RandomMainSelectAdapter by lazy {
        RandomMainSelectAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.randomNameMainViewModel = mViewModel
        // 初始化
        mViewModel.initRandomList()
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.random_title_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            randomMainActivityResult.launch(
                obtainActivityIntent(RandomSettingActivity::class.java)
            )
            mViewModel.endRandom()
        }
        mDatabind.commonToolBar.commonToolBarTitleTv.setOnLongClickListener {
            mDatabind.randomNameDrawerLayout.openDrawer(GravityCompat.END)
            return@setOnLongClickListener true
        }
        mDatabind.randomControlTv.clickScale {
            when (mViewModel.runRandomStatus.value) {
                RUN_STATUS_NONE, RUN_STATUS_STOP -> {
                    mViewModel.startRandom()
                }

                RUN_STATUS_START -> {
                    when (appViewModel.randomNameRunType.value!!) {
                        RANDOM_MANUAL -> mViewModel.stopManualRandom()
                        RANDOM_AUTO -> mViewModel.stopAutoRandom()
                    }
                }
            }
        }
        mDatabind.randomControlResetSelectTv.clickScale {
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

        mDatabind.randomMainNotSelectRecycler.itemAnimator = null
        mDatabind.randomMainNotSelectRecycler.layoutManager = flexboxLayoutManager()
        mDatabind.randomMainNotSelectRecycler.adapter = randomMainNotSelectAdapter

        mDatabind.randomMainSelectRecycler.layoutManager = flexboxLayoutManager()
        mDatabind.randomMainSelectRecycler.adapter = randomMainSelectAdapter
    }

    override fun onBackPressCallBack() {
        if (mDatabind.randomNameDrawerLayout.isDrawerOpen(mDatabind.randomNameMainSwipeContentLayout)) {
            mDatabind.randomNameDrawerLayout.closeDrawers()
        } else {
            finishCurrentActivity()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun createObserver() {
        mViewModel.targetRandomName.observe(this) {
            // 每点一次结束时,且不允许重复点名,那么待点名单需要删除
            if (!appViewModel.randomNameRunRepeat.value!!) {
                for (index in mViewModel.notSelects.indices) {
                    val randomNameData = mViewModel.notSelects[index]
                    if (randomNameData.randomName == it) {
                        mViewModel.notSelects.remove(randomNameData)
                        randomMainNotSelectAdapter.removeAt(index)
                        break
                    }
                }
                mViewModel.selects.add(RandomNameData(mViewModel.toGroupName.value!!, it))
                randomMainSelectAdapter.addData(
                    RandomNameData(mViewModel.toGroupName.value!!, it)
                )
            }
            //统计
            if (appViewModel.randomEnumCountEnable.value == true) {
                val value = mViewModel.countTargetRandomName[it]
                if (value == null) {
                    mViewModel.countTargetRandomName[it] = 1
                    mDatabind.randomMainNameCountTv.text =
                        String.format(obtainString(R.string.random_main_enum_count_text), 1)
                } else {
                    mViewModel.countTargetRandomName[it] = value + 1
                    mDatabind.randomMainNameCountTv.text =
                        String.format(obtainString(R.string.random_main_enum_count_text), value + 1)
                }
            }
            mViewModel.selectListChange.value = true
        }
        mViewModel.reStartSelectDataEvent.observe(this) {
            if (it) {
                randomMainSelectAdapter.setList(mutableListOf())
                randomMainNotSelectAdapter.setList(mViewModel.notSelects)
                randomNameMainSwipeAdapter.setList(mViewModel.randomGroupValue.value)
                mDatabind.randomMainSelectSizeTv.text = "(${randomMainSelectAdapter.data.size}):"
                mDatabind.randomMainNotSelectSizeTv.text =
                    "(${randomMainNotSelectAdapter.data.size}):"
                mViewModel.countTargetRandomName.clear()
                mViewModel.selectListChange.value = true
            }
        }
        mViewModel.selectListChange.observe(this) {
            if (it) {
                val selectSize = randomMainSelectAdapter.data.size
                val notSelectSize = randomMainNotSelectAdapter.data.size
                mDatabind.randomMainSelectSizeTv.text = "(${selectSize}):"
                mDatabind.randomMainNotSelectSizeTv.text = "(${notSelectSize}):"
            }
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