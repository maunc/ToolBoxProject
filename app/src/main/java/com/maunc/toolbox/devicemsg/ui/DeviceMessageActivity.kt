package com.maunc.toolbox.devicemsg.ui

import android.os.Bundle
import android.util.Log
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityDeviceMessageBinding
import com.maunc.toolbox.devicemsg.adapter.DeviceMessageAdapter
import com.maunc.toolbox.devicemsg.data.DeviceMessageData
import com.maunc.toolbox.devicemsg.viewmodel.DeviceMessageViewModel

class DeviceMessageActivity : BaseActivity<DeviceMessageViewModel, ActivityDeviceMessageBinding>() {

    private val deviceMessageAdapter by lazy {
        DeviceMessageAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                //todo copy message
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.tool_box_item_device_msg_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.deviceMessageRecycler.layoutManager = linearLayoutManager()
        mDatabind.deviceMessageRecycler.adapter = deviceMessageAdapter
        Log.e("ww", "厂商:${mViewModel.obtainManufacturer()}")
        Log.e("ww", "硬件型号:${mViewModel.obtainDeviceModel()}")
        Log.e("ww", "安卓版本号:${mViewModel.obtainAndroidApiVersion()}")
        Log.e("ww", "安卓版本号等级:${mViewModel.obtainAndroidApiLevelVersion()}")
        Log.e("ww", "主板型号:${mViewModel.obtainBoardModel()}")
        Log.e("ww", "主板硬件版本号:${mViewModel.obtainBoardHardwareVersion()}")
        Log.e("ww", "Linux内核版本:${mViewModel.obtainKernelVersion()}")
        deviceMessageAdapter.setList(
            mutableListOf<DeviceMessageData>().mutableListInsert(
                DeviceMessageData(title = "厂商:", content = mViewModel.obtainManufacturer()),
                DeviceMessageData(title = "硬件型号:", content = mViewModel.obtainDeviceModel()),
                DeviceMessageData(
                    title = "安卓版本号:",
                    content = mViewModel.obtainAndroidApiVersion()
                ),
                DeviceMessageData(
                    title = "安卓版本号等级:",
                    content = "${mViewModel.obtainAndroidApiLevelVersion()}"
                ),
                DeviceMessageData(
                    title = "Build标签:",
                    content = mViewModel.obtainBuildTags()
                ),
                DeviceMessageData(
                    title = "Build ID:",
                    content = mViewModel.obtainBuildId()
                ),
                DeviceMessageData(
                    title = "CodeName:",
                    content = mViewModel.obtainCodeName()
                ),
                DeviceMessageData(
                    title = "硬件识别码:",
                    content = mViewModel.obtainAndroidId()
                ),
                DeviceMessageData(
                    title = "系统构建时间:",
                    content = mViewModel.obtainBuildTime()
                ),
                DeviceMessageData(
                    title = "CPU指令集:",
                    content = mViewModel.obtainCpuAbis()
                ),
                DeviceMessageData(
                    title = "主板型号:",
                    content = mViewModel.obtainBoardModel()
                ),
                DeviceMessageData(
                    title = "主板硬件版本号:",
                    content = mViewModel.obtainBoardHardwareVersion()
                ),
                DeviceMessageData(
                    title = "Linux内核版本:",
                    content = mViewModel.obtainKernelVersion()
                ),
            )
        )
    }

    override fun createObserver() {}
}