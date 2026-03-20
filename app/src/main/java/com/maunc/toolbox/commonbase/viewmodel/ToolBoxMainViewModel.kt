package com.maunc.toolbox.commonbase.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.base.ext.mutableListInsert
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.base.ui.launch
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.data.ToolBoxItemData
import com.maunc.toolbox.commonbase.database.toolBoxItemDao
import com.maunc.toolbox.commonbase.utils.commonNotFirstLaunchApp
import com.maunc.toolbox.commonbase.utils.obtainMMKV

class ToolBoxMainViewModel : BaseViewModel<BaseModel>() {

    var toolBoxListLiveData = MutableLiveData<MutableList<ToolBoxItemData>>(mutableListOf())

    fun initToolBoxItemList() {
        if (obtainMMKV.getBoolean(commonNotFirstLaunchApp)) {
            queryToolBoxList()
            return
        }
        obtainMMKV.putBoolean(commonNotFirstLaunchApp, true)
        launch({
            toolBoxItemDao.initToolBoxMainItem(obtainStartToolBoxData())
        }, { queryToolBoxList() })
    }

    private fun queryToolBoxList() {
        launch({ toolBoxItemDao.queryToolBoxList() }, { list ->
            if (list.none { it.itemType == ToolBoxItemData.TOOL_BOX_ITEM_TORRENT_PARSE }) {
                val nextSort = (list.maxOfOrNull { it.itemSort } ?: -1) + 1
                launch({
                    toolBoxItemDao.insertToolBoxItem(
                        ToolBoxItemData(
                            itemType = ToolBoxItemData.TOOL_BOX_ITEM_TORRENT_PARSE,
                            itemTitle = obtainString(R.string.tool_box_item_torrent_parse_text),
                            itemSort = nextSort
                        )
                    )
                    toolBoxItemDao.queryToolBoxList()
                }, { toolBoxListLiveData.postValue(it) })
            } else {
                toolBoxListLiveData.postValue(list)
            }
        })
    }

    fun updateToolBoxList(newList: MutableList<ToolBoxItemData>) {
        launch({ toolBoxItemDao.updateToolBoxItem(newList) })
    }

    private fun obtainStartToolBoxData() = mutableListOf<ToolBoxItemData>().mutableListInsert(
        ToolBoxItemData(
            itemType = ToolBoxItemData.TOOL_BOX_ITEM_CHRONOGRAPH,
            itemTitle = obtainString(R.string.tool_box_item_chronograph_text),
            itemSort = 0
        ),
        ToolBoxItemData(
            itemType = ToolBoxItemData.TOOL_BOX_ITEM_RANDOM_NAME,
            itemTitle = obtainString(R.string.tool_box_item_random_name_text),
            itemSort = 1
        ),
        ToolBoxItemData(
            itemType = ToolBoxItemData.TOOL_BOX_ITEM_CHAT_ROOM,
            itemTitle = obtainString(R.string.tool_box_item_chat_room_text),
            itemSort = 2
        ),
        ToolBoxItemData(
            itemType = ToolBoxItemData.TOOL_BOX_ITEM_SIGNATURE_CANVAS,
            itemTitle = obtainString(R.string.tool_box_item_signature_canvas_text),
            itemSort = 3
        ),
        ToolBoxItemData(
            itemType = ToolBoxItemData.TOOL_BOX_ITEM_TURN_TABLE,
            itemTitle = obtainString(R.string.tool_box_item_turn_table_text),
            itemSort = 4
        ),
        ToolBoxItemData(
            itemType = ToolBoxItemData.TOOL_BOX_ITEM_FFMPEG,
            itemTitle = obtainString(R.string.tool_box_item_ffmpeg_text),
            itemSort = 5
        ),
        ToolBoxItemData(
            itemType = ToolBoxItemData.TOOL_BOX_ITEM_PUSH_BOX,
            itemTitle = obtainString(R.string.tool_box_item_push_box_text),
            itemSort = 6
        ),
        ToolBoxItemData(
            itemType = ToolBoxItemData.TOOL_BOX_ITEM_FTP,
            itemTitle = obtainString(R.string.tool_box_item_ftp_text),
            itemSort = 7
        ),
        ToolBoxItemData(
            itemType = ToolBoxItemData.TOOL_BOX_ITEM_DEVICE_MSG,
            itemTitle = obtainString(R.string.tool_box_item_device_msg_text),
            itemSort = 8
        ),
        ToolBoxItemData(
            itemType = ToolBoxItemData.TOOL_BOX_ITEM_TORRENT_PARSE,
            itemTitle = obtainString(R.string.tool_box_item_torrent_parse_text),
            itemSort = 9
        ),
    )
}
