package com.maunc.toolbox.randomname.ui.activity

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_DIALOG
import com.maunc.toolbox.commonbase.ext.finishCurrentResultToActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainIntentPutData
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.commonbase.ext.toastShort
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.databinding.ActivityRandomSettingBinding
import com.maunc.toolbox.randomname.adapter.RandomSettingAdapter
import com.maunc.toolbox.randomname.constant.RANDOM_AUTO
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MAX
import com.maunc.toolbox.randomname.constant.RESULT_SOURCE_FROM_RANDOM_SETTING_PAGE
import com.maunc.toolbox.randomname.constant.RESULT_TEXT_SIZE_MAX
import com.maunc.toolbox.randomname.constant.SELECT_GROUP_TO_MAIN_DIALOG
import com.maunc.toolbox.randomname.ui.dialog.RandomSelectGroupDialog
import com.maunc.toolbox.randomname.viewmodel.RandomSettingViewModel

/**
 * 设置页面
 */
class RandomSettingActivity : BaseActivity<RandomSettingViewModel, ActivityRandomSettingBinding>() {
    companion object {
        const val RUN_RANDOM_TYPE = "runRandomType"
        const val RUN_DELAY_TIME = "runDelayTime"
        const val SHOW_SELECT_RECYCLER = "showSelectRecycler"
        const val RESULT_TEXT_BOLD = "resultTextBold"
        const val RESULT_TEXT_SIZE = "resultTextSize"
        const val RUN_RANDOM_REPEAT = "runRandomRepeat"
    }

    private val deleteTipsDialog by lazy {
        CommonDialog().setTitle(
            obtainString(R.string.random_setting_random_delete_all_data_text)
        ).setSureListener {
            mViewModel.deleteAllData {
                toastShort(obtainString(R.string.common_tips_delete_success))
            }
        }
    }

    private val randomSelectGroupDialog by lazy {
        RandomSelectGroupDialog()
    }

    private val randomSettingAdapter: RandomSettingAdapter by lazy {
        RandomSettingAdapter().apply {
            setOnRandomSettingEventListener(object :
                RandomSettingAdapter.OnRandomSettingEventListener {
                override fun showSelectDataDialog() {
                    randomSelectGroupDialog.show(
                        supportFragmentManager,
                        SELECT_GROUP_TO_MAIN_DIALOG
                    )
                }

                override fun startManagerPage() {
                    startTargetActivity(ManageGroupActivity::class.java)
                }

                override fun deleteAllDataClick() {
                    deleteTipsDialog.show(supportFragmentManager, COMMON_DIALOG)
                }
            })
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.randomSettingViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.random_setting_text)
        mDatabind.commonToolBar.commonToolBarBackButton.setOnClickListener {
            mViewModel.buttonClickLaunchVibrator()
            baseFinishCurrentActivity()
        }
        mDatabind.randomSettingRecycler.layoutManager = linearLayoutManager()
        mDatabind.randomSettingRecycler.adapter = randomSettingAdapter
        randomSettingAdapter.setList(mViewModel.initRecyclerData())
        randomSettingAdapter.setConfig(
            intent.getIntExtra(RUN_RANDOM_TYPE, RANDOM_AUTO),
            intent.getLongExtra(RUN_DELAY_TIME, RANDOM_SPEED_MAX),
            intent.getBooleanExtra(SHOW_SELECT_RECYCLER, false),
            intent.getBooleanExtra(RESULT_TEXT_BOLD, false),
            intent.getBooleanExtra(RUN_RANDOM_REPEAT, false),
            intent.getIntExtra(RESULT_TEXT_SIZE, RESULT_TEXT_SIZE_MAX)
        )
    }

    override fun onBackPressCallBack() {
        baseFinishCurrentActivity()
    }

    private fun baseFinishCurrentActivity(action: () -> Unit = {}) {
        action()
        finishCurrentResultToActivity(
            resultCode = RESULT_SOURCE_FROM_RANDOM_SETTING_PAGE,
            intent = obtainIntentPutData(mutableMapOf<String, Any>().apply {
                put(RUN_RANDOM_TYPE, randomSettingAdapter.obtainRandomType())
                put(RUN_DELAY_TIME, randomSettingAdapter.obtainDelayTime())
                put(SHOW_SELECT_RECYCLER, randomSettingAdapter.obtainShowSelectRecycler())
                put(RESULT_TEXT_BOLD, randomSettingAdapter.obtainResultTextIsBold())
                put(RUN_RANDOM_REPEAT, randomSettingAdapter.obtainRandomRepeat())
                put(RESULT_TEXT_SIZE, randomSettingAdapter.obtainResultTextSize())
            })
        )
    }

    override fun createObserver() {

    }
}