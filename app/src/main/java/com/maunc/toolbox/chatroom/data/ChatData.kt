package com.maunc.toolbox.chatroom.data

import androidx.annotation.DrawableRes
import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class ChatData(
    override val itemType: Int,
    val chatSendTime: Long,
    val chatText: String? = null,
    val chatImage: ChatImageData? = null,
    val chatRecord: ChatRecordData? = null,
) : Serializable, MultiItemEntity {
    companion object {
        const val CHAT_NONE_TYPE = 0
        const val CHAT_TEXT_TYPE = 1
        const val CHAT_IMAGE_TYPE = 2
        const val CHAT_VOICE_TYPE = 3
    }
}

data class ChatImageData(
    val filePath: String? = null,
    val url: String? = null,
    @DrawableRes
    val imageRes: Int,
)

data class ChatRecordData(
    val filePath: String? = null,
    val recordTime: Long? = null,
)
