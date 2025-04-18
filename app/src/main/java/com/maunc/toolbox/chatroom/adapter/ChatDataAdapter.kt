package com.maunc.toolbox.chatroom.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.data.ChatData
import com.maunc.toolbox.chatroom.data.ChatImageData
import com.maunc.toolbox.chatroom.data.ChatRecordData
import com.maunc.toolbox.chatroom.data.LoadImageType
import com.maunc.toolbox.chatroom.data.LoadRecordType
import com.maunc.toolbox.chatroom.data.convertTime
import com.maunc.toolbox.commonbase.constant.THIRTY_SECOND
import com.maunc.toolbox.commonbase.ext.addGestureDetector
import com.maunc.toolbox.commonbase.ext.isChineseChar
import com.maunc.toolbox.commonbase.ext.loadImage
import com.maunc.toolbox.commonbase.ext.loadImageCircleCrop
import com.maunc.toolbox.commonbase.ext.visibleOrGone

@SuppressLint("SetTextI18n")
class ChatDataAdapter : BaseMultiItemQuickAdapter<ChatData, BaseViewHolder>() {

    private var clickUserItemListener: ChatRoomOnUserItemListener? = null
    private var clickBotItemListener: ChatRoomOnBotItemListener? = null

    init {
        addItemType(ChatData.CHAT_NONE_TYPE, R.layout.item_chat_none)
        addItemType(ChatData.CHAT_TEXT_TYPE, R.layout.item_chat_text)
        addItemType(ChatData.CHAT_IMAGE_TYPE, R.layout.item_chat_image)
        addItemType(ChatData.CHAT_VOICE_TYPE, R.layout.item_chat_voice)
        addItemType(ChatData.CHAT_BOT_TEXT_TYPE, R.layout.item_chat_bot_reply_text)
    }

    override fun convert(holder: BaseViewHolder, item: ChatData) {
        val itemPosition = getItemPosition(item)
        val haveView = holder.itemView
        haveView.findViewById<TextView>(R.id.item_chat_room_time_tv).apply {
            visibleOrGone(showTimeTextView())
            text = item.chatSendTime.convertTime()
        }
        //其他Tips布局不做处理  后续可叠加
        if (item.itemType != ChatData.CHAT_NONE_TYPE) {
            handlerUserIcon(item.itemType, haveView.findViewById(R.id.item_chat_room_user_iv))
        }
        when (item.itemType) {
            ChatData.CHAT_NONE_TYPE -> {
                haveView.findViewById<TextView>(R.id.item_chat_room_content_tv).text =
                    item.chatText
            }

            ChatData.CHAT_TEXT_TYPE -> {
                val contentTv: TextView = haveView.findViewById(R.id.item_chat_room_content_tv)
                contentTv.text = item.chatText?.insertLineBreaks()
                contentTv.addGestureDetector(onDoubleTap = {
                    item.chatText?.let { content ->
                        clickUserItemListener?.onDoubleTapUserTextItem(content, item.chatSendTime)
                    }
                })
            }

            ChatData.CHAT_IMAGE_TYPE -> {
                val contentIv: ImageView = haveView.findViewById(R.id.item_chat_room_content_iv)
                val chatImage = item.chatImage
                when (chatImage?.loadImageType) {
                    LoadImageType.RES -> chatImage.imageRes?.let { contentIv.loadImage(it) }
                    LoadImageType.FILE -> chatImage.filePath?.let { contentIv.loadImage(it) }
                    LoadImageType.URL -> chatImage.url?.let { contentIv.loadImage(it) }
                    null -> {}
                }
                contentIv.setOnClickListener {
                    if (chatImage != null) {
                        val chatImageList = mutableListOf<ChatImageData>()
                        data.forEach { chatData ->
                            chatData.chatImage?.let { chatImageList.add(it) }
                        }
                        var index = 0
                        chatImageList.forEachIndexed { pos, data ->
                            if (item.chatImage == data) index = pos
                        }
                        clickUserItemListener?.onClickUserImageItem(
                            chatImage, chatImageList, itemPosition, index
                        )
                    }
                }
            }

            ChatData.CHAT_VOICE_TYPE -> {
                val audioTimeTv =
                    haveView.findViewById<TextView>(R.id.item_chat_room_audio_time_tv)
                audioTimeTv.text = "${item.chatRecord?.recordTime.toString()}'"
                val audioControllerButton =
                    haveView.findViewById<RelativeLayout>(R.id.item_chat_room_audio_layout)
                audioControllerButton.setOnClickListener {
                    item.chatRecord?.let { chatRecordData ->
                        clickUserItemListener?.onClickUserAudioItem(chatRecordData, itemPosition)
                    }
                }
            }

            ChatData.CHAT_BOT_TEXT_TYPE -> {
                val contentTv: TextView = haveView.findViewById(R.id.item_chat_room_content_tv)
                contentTv.text = item.chatText?.insertLineBreaks()
                contentTv.addGestureDetector(onDoubleTap = {
                    item.chatText?.let { content ->
                        clickBotItemListener?.onDoubleTapBotTextItem(content, item.chatSendTime)
                    }
                })
            }
        }
    }

    /**
     * 展示各自头像
     */
    private fun handlerUserIcon(itemType: Int, userIconView: ImageView) {
        when (itemType) {
            //用户头像
            ChatData.CHAT_TEXT_TYPE, ChatData.CHAT_IMAGE_TYPE, ChatData.CHAT_VOICE_TYPE -> {
                userIconView.loadImageCircleCrop(R.drawable.icon_default_iv)
            }
            //机器人头像
            ChatData.CHAT_BOT_TEXT_TYPE -> {
                userIconView.loadImageCircleCrop(R.drawable.icon_lucia)
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

    /**
     * 添加机器人文本布局
     */
    fun addChatBotTextItem(
        content: String,
    ) = addChatItem(
        ChatData(
            itemType = ChatData.CHAT_BOT_TEXT_TYPE,
            chatText = content,
            chatSendTime = System.currentTimeMillis()
        )
    )

    /**
     * 添加Tips文本布局
     */
    fun addChatNoneItem(
        content: String,
    ) = addChatItem(
        ChatData(
            itemType = ChatData.CHAT_NONE_TYPE,
            chatText = content,
            chatSendTime = System.currentTimeMillis()
        )
    )

    /**
     * 添加个人文本布局
     */
    fun addChatTextItem(
        sendContent: String,
    ) = addChatItem(
        ChatData(
            itemType = ChatData.CHAT_TEXT_TYPE,
            chatText = sendContent,
            chatSendTime = System.currentTimeMillis()
        )
    )

    /**
     * 添加个人Image布局  file类型
     */
    fun addChatImageFileItem(
        filePath: String,
    ) = addChatItem(
        ChatData(
            itemType = ChatData.CHAT_IMAGE_TYPE,
            chatImage = ChatImageData(loadImageType = LoadImageType.FILE, filePath = filePath),
            chatSendTime = System.currentTimeMillis()
        )
    )

    /**
     * 添加个人Image布局  url类型
     */
    fun addChatImageUrlItem(
        url: String,
    ) = addChatItem(
        ChatData(
            itemType = ChatData.CHAT_IMAGE_TYPE,
            chatImage = ChatImageData(loadImageType = LoadImageType.URL, url = url),
            chatSendTime = System.currentTimeMillis()
        )
    )

    /**
     * 添加个人Image布局  Res类型
     */
    fun addChatImageResItem(
        @DrawableRes res: Int,
    ) = addChatItem(
        ChatData(
            itemType = ChatData.CHAT_IMAGE_TYPE,
            chatImage = ChatImageData(loadImageType = LoadImageType.RES, imageRes = res),
            chatSendTime = System.currentTimeMillis()
        )
    )

    /**
     * 添加个人录音布局  File类型
     */
    fun addChatAudioItem(
        filePath: String,
        recordTime: Int,
    ) = addChatItem(
        ChatData(
            itemType = ChatData.CHAT_VOICE_TYPE,
            chatSendTime = System.currentTimeMillis(),
            chatRecord = ChatRecordData(
                loadRecordType = LoadRecordType.FILE,
                filePath = filePath,
                recordTime = recordTime
            )
        )
    )

    private fun addChatItem(chatData: ChatData) {
        data.add(chatData)
        notifyItemInserted(this.data.size - 1)
        recyclerView.scrollToPosition(this.data.size - 1)
    }

    fun setClickUserItemListener(clickUserItemListener: ChatRoomOnUserItemListener) {
        this.clickUserItemListener = clickUserItemListener
    }

    fun setClickBotItemListener(clickBotItemListener: ChatRoomOnBotItemListener) {
        this.clickBotItemListener = clickBotItemListener
    }

    interface ChatRoomOnUserItemListener {
        fun onClickUserTextItem()
        fun onDoubleTapUserTextItem(
            content: String,
            sendTime: Long,
        )

        fun onClickUserImageItem(
            chatImageData: ChatImageData,
            chatImageList: MutableList<ChatImageData>,
            itemViewPosition: Int,
            clickImagePosition: Int,
        )

        fun onClickUserAudioItem(
            chatRecordData: ChatRecordData,
            itemViewPosition: Int,
        )
    }

    interface ChatRoomOnBotItemListener {
        fun onDoubleTapBotTextItem(
            content: String,
            sendTime: Long,
        )
    }
}