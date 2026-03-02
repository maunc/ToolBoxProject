package com.maunc.toolbox.randomname.ui.activity

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_DIALOG
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.commonbase.ext.toastShort
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomRepeat
import com.maunc.toolbox.commonbase.utils.randomSelectRecyclerVisible
import com.maunc.toolbox.commonbase.utils.randomSpeed
import com.maunc.toolbox.commonbase.utils.randomTextBold
import com.maunc.toolbox.commonbase.utils.randomTextSize
import com.maunc.toolbox.commonbase.utils.randomType
import com.maunc.toolbox.databinding.ActivityRandomSettingBinding
import com.maunc.toolbox.randomname.adapter.RandomSettingAdapter
import com.maunc.toolbox.randomname.constant.RANDOM_AUTO
import com.maunc.toolbox.randomname.constant.RANDOM_RESULT_TEXT_SIZE_DEFAULT_VALUE
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MAX
import com.maunc.toolbox.randomname.constant.SELECT_GROUP_TO_MAIN_DIALOG
import com.maunc.toolbox.randomname.ui.dialog.RandomSelectGroupDialog
import com.maunc.toolbox.randomname.viewmodel.RandomSettingViewModel

/**
 * 设置页面
 */
class RandomSettingActivity : BaseActivity<RandomSettingViewModel, ActivityRandomSettingBinding>() {

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
                override fun configRunRandomType(type: Int) {
                    obtainMMKV.putInt(randomType, type)
                    appViewModel.randomNameRunType.value = type
                }

                override fun configRunRandomSpeed(speed: Long) {
                    obtainMMKV.putLong(randomSpeed, speed)
                    appViewModel.randomNameRunSpeed.value = speed
                }

                override fun configShowSelectRecycler(isShow: Boolean) {
                    obtainMMKV.putBoolean(randomSelectRecyclerVisible, isShow)
                    appViewModel.randomNameShowSelectRecycler.value = isShow
                }

                override fun configRunRandomRepeat(isRepeat: Boolean) {
                    obtainMMKV.putBoolean(randomRepeat, isRepeat)
                    appViewModel.randomNameRunRepeat.value = isRepeat
                }

                override fun configResultBold(isBold: Boolean) {
                    obtainMMKV.putBoolean(randomTextBold, isBold)
                    appViewModel.randomNameResultIsBold.value = isBold
                }

                override fun configResultTextSize(size: Int) {
                    obtainMMKV.putInt(randomTextSize, size)
                    appViewModel.randomNameResultTextSize.value = size
                }

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
            finishCurrentActivity()
        }
        mDatabind.randomSettingRecycler.layoutManager = linearLayoutManager()
        mDatabind.randomSettingRecycler.adapter = randomSettingAdapter
        randomSettingAdapter.setList(mViewModel.initRecyclerData())
        randomSettingAdapter.setConfig(
            appViewModel.randomNameRunType.value ?: RANDOM_AUTO,
            appViewModel.randomNameRunSpeed.value ?: RANDOM_SPEED_MAX,
            appViewModel.randomNameShowSelectRecycler.value ?: false,
            appViewModel.randomNameResultIsBold.value ?: false,
            appViewModel.randomNameRunRepeat.value ?: false,
            appViewModel.randomNameResultTextSize.value ?: RANDOM_RESULT_TEXT_SIZE_DEFAULT_VALUE
        )
    }

    override fun createObserver() {

    }
}