package com.maunc.toolbox.devicemsg.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemDeviceMessageBinding
import com.maunc.toolbox.devicemsg.data.DeviceMessageData

class DeviceMessageAdapter :
    BaseQuickAdapter<DeviceMessageData, BaseDataBindingHolder<ItemDeviceMessageBinding>>(
        R.layout.item_device_message
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemDeviceMessageBinding>,
        item: DeviceMessageData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemDeviceMessageTitle.text = item.title
            mDataBind.itemDeviceMessageContent.text = item.content
        }
    }
}