package com.maunc.toolbox.ffmpeg.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemFfmpegMp4ToGifMsgBinding
import com.maunc.toolbox.ffmpeg.data.FFmpegMp4ToGifMsgData

class FFmpegMp4ToGifMsgAdapter :
    BaseQuickAdapter<FFmpegMp4ToGifMsgData, BaseDataBindingHolder<ItemFfmpegMp4ToGifMsgBinding>>(
        R.layout.item_ffmpeg_mp4_to_gif_msg
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemFfmpegMp4ToGifMsgBinding>,
        item: FFmpegMp4ToGifMsgData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemFfmpegMp4ToGifMsgTitleTv.text = item.title
            mDataBind.itemFfmpegMp4ToGifMsgContentTv.text = item.content
        }
    }
}

