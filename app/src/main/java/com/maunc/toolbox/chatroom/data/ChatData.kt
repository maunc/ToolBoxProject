package com.maunc.toolbox.chatroom.data

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class ChatData(
    override val itemType: Int,
    val chatSendTime: Long,
    val chatContent: String? = null,
    val filePath: String? = null,
    val recordTime: Long? = null,
) : Serializable, MultiItemEntity {
    companion object {
        const val CHAT_NONE_TYPE = 0
        const val CHAT_TEXT_TYPE = 1
        const val CHAT_IMAGE_TYPE = 2
        const val CHAT_VOICE_TYPE = 3
    }
}
