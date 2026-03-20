package com.maunc.toolbox.commonbase.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maunc.base.ext.obtainString
import com.maunc.toolbox.R

@Entity(tableName = "tool_box_item")
data class ToolBoxItemData(
    @PrimaryKey
    val itemType: Int = -1,
    val itemTitle: String = obtainString(R.string.functions_are_under_development),
    var itemSort: Int = 0,
) {
    companion object {
        /**对应的是图片*/
        const val TOOL_BOX_ITEM_CHRONOGRAPH = 0//计时器
        const val TOOL_BOX_ITEM_RANDOM_NAME = 1//点名
        const val TOOL_BOX_ITEM_CHAT_ROOM = 2//聊天室
        const val TOOL_BOX_ITEM_SIGNATURE_CANVAS = 3//画板
        const val TOOL_BOX_ITEM_TURN_TABLE = 4//转盘
        const val TOOL_BOX_ITEM_FFMPEG = 5//音视频编辑
        const val TOOL_BOX_ITEM_PUSH_BOX = 6//推箱子
        const val TOOL_BOX_ITEM_FTP = 7//FTP
        const val TOOL_BOX_ITEM_DEVICE_MSG = 8//设备信息
        const val TOOL_BOX_ITEM_TORRENT_PARSE = 9//种子解析（.torrent 文件）
    }
}

@Dao
interface ToolBoxItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToolBoxItem(toolBoxItemData: ToolBoxItemData)

    @Transaction
    fun initToolBoxMainItem(toolBoxItems: MutableList<ToolBoxItemData>) {
        toolBoxItems.forEach { insertToolBoxItem(it) }
    }

    @Query("SELECT * FROM tool_box_item order by itemSort ASC")
    fun queryToolBoxList(): MutableList<ToolBoxItemData>

    @Update
    fun updateToolBoxItem(toolBoxItemData: MutableList<ToolBoxItemData>)


    @Query("DELETE FROM tool_box_item")
    fun deleteAllToolBoxItem()
}