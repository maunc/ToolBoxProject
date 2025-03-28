package com.maunc.toolbox.chatroom.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.constant.THIRTY_SECOND
import com.maunc.toolbox.chatroom.constant.YYYY_MM_DD_HH_MM_SS
import com.maunc.toolbox.chatroom.data.ChatData
import com.maunc.toolbox.commonbase.ext.isChineseChar
import com.maunc.toolbox.commonbase.ext.loadImageCircleCrop
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
        addItemType(ChatData.CHAT_BOT_TEXT_TYPE, R.layout.item_chat_bot_reply_text)
    }

    override fun convert(holder: BaseViewHolder, item: ChatData) {
        val haveView = holder.itemView
        haveView.findViewById<TextView>(R.id.item_chat_room_time_tv).apply {
            visibleOrGone(showTimeTextView())
            text = convertTime(item.chatSendTime)
        }
        when (item.itemType) {
            ChatData.CHAT_NONE_TYPE -> {
                haveView.findViewById<TextView>(R.id.item_chat_room_content_tv).text =
                    item.chatText
            }

            ChatData.CHAT_TEXT_TYPE -> {
                val contentTv: TextView = haveView.findViewById(R.id.item_chat_room_content_tv)
                contentTv.text = item.chatText?.insertLineBreaks()
                haveView.findViewById<ImageView>(R.id.item_chat_room_user_iv)
                    .loadImageCircleCrop(R.drawable.icon_default_iv)
            }

            ChatData.CHAT_IMAGE_TYPE -> {

            }

            ChatData.CHAT_VOICE_TYPE -> {

            }

            ChatData.CHAT_BOT_TEXT_TYPE -> {
                val contentTv: TextView = haveView.findViewById(R.id.item_chat_room_content_tv)
                contentTv.text = item.chatText?.insertLineBreaks()
                haveView.findViewById<ImageView>(R.id.item_chat_room_user_iv)
                    .loadImageCircleCrop(R.drawable.icon_lucia)
            }
        }
    }

    /**
     * 展示时间布局
     */
    private fun showTimeTextView(): Boolean {
        if (data.size < 2) {
            return true
        }
        val last = data[data.size - 1].chatSendTime
        val lastTwo = data[data.size - 2].chatSendTime
        return last - lastTwo > THIRTY_SECOND
    }

    /**
     * 文本过长处理
     */
    private fun String.insertLineBreaks(maxChars: Int = 28): String {
        val result = StringBuilder()
        var currentCount = 0
        for (i in this.indices) {
            val char = this[i]
            val charCount = if (char.isChineseChar()) 2 else 1
            if (currentCount + charCount > maxChars) {
                result.append('\n')
                currentCount = 0
            }
            result.append(char)
            currentCount += charCount
        }
        return result.toString()
    }

    fun addChatBotTextItem(content: String) {
        addChatItem(
            ChatData(
                itemType = ChatData.CHAT_BOT_TEXT_TYPE,
                chatText = content,
                chatSendTime = System.currentTimeMillis()
            )
        )
    }

    fun addChatNoneItem(content: String) {
        addChatItem(
            ChatData(
                itemType = ChatData.CHAT_NONE_TYPE,
                chatText = content,
                chatSendTime = System.currentTimeMillis()
            )
        )
    }

    fun addChatTextItem(sendContent: String) {
        addChatItem(
            ChatData(
                itemType = ChatData.CHAT_TEXT_TYPE,
                chatText = sendContent,
                chatSendTime = System.currentTimeMillis()
            )
        )
    }

    private fun addChatItem(chatData: ChatData) {
        data.add(chatData)
        notifyItemInserted(this.data.size - 1)
        recyclerView.scrollToPosition(this.data.size - 1)
    }

    private fun convertTime(time: Long): String {
        return SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(Date(time))
    }
}