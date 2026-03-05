package com.maunc.toolbox.ffmpeg.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemFfmpegMp4ToMp3MsgBinding
import com.maunc.toolbox.ffmpeg.data.FFmpegMp4ToMp3MsgData

class FFmpegMp4ToMp3MsgAdapter :
    BaseQuickAdapter<FFmpegMp4ToMp3MsgData, BaseDataBindingHolder<ItemFfmpegMp4ToMp3MsgBinding>>(
        R.layout.item_ffmpeg_mp4_to_mp3_msg
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemFfmpegMp4ToMp3MsgBinding>,
        item: FFmpegMp4ToMp3MsgData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemFfmpegMp4ToMp3MsgTitleTv.text = item.title
            mDataBind.itemFfmpegMp4ToMp3MsgContentTv.text = item.content
        }
    }
}