package com.maunc.toolbox.chatroom.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.data.ChatData
import com.maunc.toolbox.databinding.ItemChatBinding

class ChatDataAdapter : BaseQuickAdapter<ChatData, BaseDataBindingHolder<ItemChatBinding>>(
    R.layout.item_chat
) {

    override fun convert(holder: BaseDataBindingHolder<ItemChatBinding>, item: ChatData) {

    }

    fun addChronograph(timeData: ChatData) {
        data.add(0, timeData)
        notifyItemInserted(this.data.size - 1)
        recyclerView.scrollToPosition(this.data.size - 1)
    }
}