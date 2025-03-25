package com.maunc.toolbox.chatroom.data

data class ChatData(
    val chatType: ChatType,
    val filePath: String,
    val recordTime: Long,
)

enum class ChatType {
    TEXT, VOICE
}
