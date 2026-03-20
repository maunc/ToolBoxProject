package com.maunc.toolbox.pushbox.ui.activity

import android.os.Bundle
import com.maunc.base.ext.clickScale
import com.maunc.base.ext.finishCurrentActivity
import com.maunc.base.ext.linearLayoutManager
import com.maunc.base.ext.obtainString
import com.maunc.base.ext.startTargetActivity
import com.maunc.base.ui.BaseActivity
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.pushBoxControllerButtonSize
import com.maunc.toolbox.commonbase.utils.pushBoxTouchView
import com.maunc.toolbox.databinding.ActivityPushBoxSettingBinding
import com.maunc.toolbox.pushbox.adapter.PushBoxSettingAdapter
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MEDIUM
import com.maunc.toolbox.pushbox.viewmodel.PushBoxSettingViewModel

class PushBoxSettingActivity :
    BaseActivity<PushBoxSettingViewModel, ActivityPushBoxSettingBinding>() {
    private val pushBoxSettingAdapter by lazy {
        PushBoxSettingAdapter().apply {
            setOnPushBoxSettingEventListener(object :
                PushBoxSettingAdapter.OnPushBoxSettingEventListener {
                override fun startPreviewPage() {
                    startTargetActivity(PushBoxPreViewActivity::class.java)
                }

                override fun configControllerSize(size: Int) {
                    obtainMMKV.putInt(pushBoxControllerButtonSize, size)
                    appViewModel.pushBoxControllerSize.value = size
                }

                override fun configTouchMove(isTouch: Boolean) {
                    obtainMMKV.putBoolean(pushBoxTouchView, isTouch)
                    appViewModel.pushBoxViewTouch.value = isTouch
                }
            })
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.push_box_setting_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.pushBoxSettingRecycler.layoutManager = linearLayoutManager()
        mDatabind.pushBoxSettingRecycler.adapter = pushBoxSettingAdapter
        pushBoxSettingAdapter.setList(mViewModel.settingDataList)
        pushBoxSettingAdapter.setConfig(
            appViewModel.pushBoxControllerSize.value ?: PUSH_BOX_CONTROLLER_SIZE_MEDIUM,
            appViewModel.pushBoxViewTouch.value ?: false
        )
    }

    override fun createObserver() {

    }
}