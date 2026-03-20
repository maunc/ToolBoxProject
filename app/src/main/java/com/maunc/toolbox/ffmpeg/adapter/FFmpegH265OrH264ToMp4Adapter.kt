package com.maunc.toolbox.ffmpeg.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.base.ext.clickScale
import com.maunc.toolbox.R
import com.maunc.base.utils.formatFileSize
import com.maunc.toolbox.databinding.ItemFfmpegH265OrH264ToMp4Binding
import com.maunc.toolbox.ffmpeg.data.FFmpegH265OrH264ToMp4ResultData

class FFmpegH265OrH264ToMp4Adapter :
    BaseQuickAdapter<FFmpegH265OrH264ToMp4ResultData, BaseDataBindingHolder<ItemFfmpegH265OrH264ToMp4Binding>>(
        R.layout.item_ffmpeg_h265_or_h264_to_mp4
    ) {

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemFfmpegH265OrH264ToMp4Binding>,
        item: FFmpegH265OrH264ToMp4ResultData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemFfmpegH265OrH264ToMp4FileTitleTv.text = item.fileName
            mDataBind.itemFfmpegH265OrH264ToMp4FileSizeTv.text =
                "大小:${formatFileSize(item.fileSize)}"
            mDataBind.itemFfmpegH265OrH264ToMp4FileTimeTv.text = "修改日期:${item.timeString}"
            mDataBind.itemFfmpegH265OrH264ToMp4RootLayout.setOnClickListener {
                onH265OrH264ItemClickListener?.viewDetailsItemClick(
                    item, holder.bindingAdapterPosition
                )
            }
            mDataBind.itemFfmpegH265OrH264ToMp4ConvertLayout.clickScale {
                onH265OrH264ItemClickListener?.convertItemClick(item, holder.bindingAdapterPosition)
            }
        }
    }

    private var onH265OrH264ItemClickListener: OnH265OrH264ItemClickListener? = null

    interface OnH265OrH264ItemClickListener {
        fun convertItemClick(fileData: FFmpegH265OrH264ToMp4ResultData, pos: Int)
        fun viewDetailsItemClick(fileData: FFmpegH265OrH264ToMp4ResultData, pos: Int)
    }

    fun setOnH265OrH264ItemClickListener(onH265OrH264ItemClickListener: OnH265OrH264ItemClickListener) {
        this.onH265OrH264ItemClickListener = onH265OrH264ItemClickListener
    }
}