package com.maunc.toolbox.ffmpeg.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemFfmpegTypeDataBinding
import com.maunc.toolbox.ffmpeg.data.FFmpegConfigData

class FFmpegConfigAdapter:BaseQuickAdapter<FFmpegConfigData,BaseDataBindingHolder<ItemFfmpegTypeDataBinding>>(
    R.layout.item_ffmpeg_type_data
) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemFfmpegTypeDataBinding>,
        item: FFmpegConfigData,
    ) {
        holder.dataBinding?.let {
            it.itemFfmpegTypeTypeTv.text = item.title
        }
    }
}