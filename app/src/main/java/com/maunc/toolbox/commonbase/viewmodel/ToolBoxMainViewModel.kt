package com.maunc.toolbox.commonbase.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.data.ToolBoxItemData
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString

class ToolBoxMainViewModel : BaseViewModel<BaseModel>() {

    private var localItemData = MutableLiveData<MutableList<ToolBoxItemData>>(mutableListOf())

    fun initRecyclerData(): MutableList<ToolBoxItemData> {
        localItemData.value?.mutableListInsert(
            ToolBoxItemData(
                itemTitle = obtainString(R.string.tool_box_item_chronograph_text)
            ),
            ToolBoxItemData(
                itemTitle = obtainString(R.string.tool_box_item_random_name_text)
            ),
            ToolBoxItemData(
                itemTitle = obtainString(R.string.tool_box_item_chat_room_text)
            ),
            ToolBoxItemData(
                itemTitle = obtainString(R.string.tool_box_item_signature_canvas_text)
            ),
            ToolBoxItemData(
                itemTitle = obtainString(R.string.tool_box_item_turn_table_text)
            ),
        )
        return localItemData.value!!
    }
}
