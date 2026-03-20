package com.maunc.toolbox.commonbase.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.data.ToolBoxItemData
import com.maunc.toolbox.databinding.ItemToolBoxManagerBinding

class ToolBoxManagerAdapter :
    BaseQuickAdapter<ToolBoxItemData, BaseDataBindingHolder<ItemToolBoxManagerBinding>>(R.layout.item_tool_box_manager) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemToolBoxManagerBinding>,
        item: ToolBoxItemData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemToolBoxBackgroundIcon.setImageResource(
                when (item.itemType) {
                    ToolBoxItemData.TOOL_BOX_ITEM_CHRONOGRAPH -> R.drawable.icon_tool_box_chronograph
                    ToolBoxItemData.TOOL_BOX_ITEM_RANDOM_NAME -> R.drawable.icon_tool_box_random_name
                    ToolBoxItemData.TOOL_BOX_ITEM_CHAT_ROOM -> R.drawable.icon_tool_box_chat_room
                    ToolBoxItemData.TOOL_BOX_ITEM_SIGNATURE_CANVAS -> R.drawable.icon_tool_box_signature_canvas
                    ToolBoxItemData.TOOL_BOX_ITEM_TURN_TABLE -> R.drawable.icon_tool_box_turn_table
                    ToolBoxItemData.TOOL_BOX_ITEM_FFMPEG -> R.drawable.icon_tool_box_ffmpeg
                    ToolBoxItemData.TOOL_BOX_ITEM_PUSH_BOX -> R.drawable.icon_tool_box_push_box
                    ToolBoxItemData.TOOL_BOX_ITEM_FTP -> R.drawable.icon_tool_box_ftp
                    ToolBoxItemData.TOOL_BOX_ITEM_DEVICE_MSG -> R.drawable.icon_tool_box_device_info
                    ToolBoxItemData.TOOL_BOX_ITEM_TORRENT_PARSE -> R.drawable.icon_tool_box_device_info
                    else -> R.drawable.ic_launcher
                }
            )
            mDataBind.itemToolBoxTitleTv.text = item.itemTitle
        }
    }
}