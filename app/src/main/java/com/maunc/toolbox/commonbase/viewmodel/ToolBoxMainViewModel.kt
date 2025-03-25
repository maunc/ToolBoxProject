package com.maunc.toolbox.commonbase.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.data.ToolBoxItemData
import com.maunc.toolbox.commonbase.ext.getString
import com.maunc.toolbox.commonbase.ext.mutableListInsert

class ToolBoxMainViewModel : BaseViewModel<BaseModel>() {

    private var localItemData = MutableLiveData<MutableList<ToolBoxItemData>>(mutableListOf())

    fun initRecyclerData(): MutableList<ToolBoxItemData> {
        localItemData.value?.mutableListInsert(
            ToolBoxItemData(
                itemIcon = R.drawable.icon_tool_box_chronograph,
                itemTitle = getString(R.string.tool_box_item_chronograph_text)
            ),
            ToolBoxItemData(
                itemIcon = R.drawable.icon_tool_box_random_name,
                itemTitle = getString(R.string.tool_box_item_random_name_text)
            ),
            ToolBoxItemData(
                itemIcon = R.drawable.icon_tool_box_chat_room,
                itemTitle = getString(R.string.tool_box_item_chat_room_text)
            ),
        )
        return localItemData.value!!
    }
}
