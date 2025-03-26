package com.maunc.toolbox.chatroom.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.constant.THIRTY_SECOND
import com.maunc.toolbox.chatroom.data.ChatData
import com.maunc.toolbox.commonbase.ext.getString
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
class ChatDataAdapter :
    BaseMultiItemQuickAdapter<ChatData, BaseViewHolder>() {

    init {
        addItemType(ChatData.CHAT_NONE_TYPE, R.layout.item_chat_none)
        addItemType(ChatData.CHAT_TEXT_TYPE, R.layout.item_chat_text)
        addItemType(ChatData.CHAT_IMAGE_TYPE, R.layout.item_chat_image)
        addItemType(ChatData.CHAT_VOICE_TYPE, R.layout.item_chat_voice)
    }

    override fun convert(holder: BaseViewHolder, item: ChatData) {
        holder.itemView.findViewById<TextView>(R.id.item_chat_room_time_tv).apply {
            visibleOrGone(showTimeTextView())
            text = convertTime(item.chatSendTime)
        }
        when (item.itemType) {
            ChatData.CHAT_NONE_TYPE -> {
                holder.itemView.findViewById<TextView>(R.id.item_chat_room_content_tv).text =
                    item.chatContent
            }

            ChatData.CHAT_TEXT_TYPE -> {
                holder.itemView.findViewById<TextView>(R.id.item_chat_room_content_tv).text =
                    item.chatContent
            }

            ChatData.CHAT_IMAGE_TYPE -> {

            }

            ChatData.CHAT_VOICE_TYPE -> {

            }
        }
    }

    private fun showTimeTextView(): Boolean {
        if (data.size < 2) {
            return true
        }
        val last = data[data.size - 1].chatSendTime
        val lastTwo = data[data.size - 2].chatSendTime
        return last - lastTwo > THIRTY_SECOND
    }

    fun addChatItem(timeData: ChatData) {
        data.add(timeData)
        notifyItemInserted(this.data.size - 1)
        recyclerView.scrollToPosition(this.data.size - 1)
    }

    private fun convertTime(time: Long): String {
        return SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(Date(time))
    }
}