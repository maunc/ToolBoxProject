package com.maunc.toolbox.chatroom.data

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

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
        const val CHAT_BOT_TEXT_TYPE = 4
    }
}

data class ChatImageData(
    val loadImageType: LoadImageType = LoadImageType.FILE,
    val filePath: String? = null,
    val url: String? = null,
    @DrawableRes val imageRes: Int? = null,
) : Serializable

enum class LoadImageType {
    FILE, URL, RES
}

data class ChatRecordData(
    val loadRecordType: LoadRecordType = LoadRecordType.FILE,
    val filePath: String? = null,
    @RawRes val recordRes: Int,
    val recordTime: Int? = null,
) : Serializable

enum class LoadRecordType {
    FILE, RES
}

@SuppressLint("SimpleDateFormat")
fun Long.convertTime(): String {
    return SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(Date(this))
}
