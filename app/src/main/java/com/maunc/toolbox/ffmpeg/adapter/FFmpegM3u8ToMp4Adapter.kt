package com.maunc.toolbox.ffmpeg.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.utils.formatFileSize
import com.maunc.toolbox.databinding.ItemFfmpegM3u8ToMp4Binding
import com.maunc.toolbox.ffmpeg.data.FFmpegM3u8ResultData

class FFmpegM3u8ToMp4Adapter :
    BaseQuickAdapter<FFmpegM3u8ResultData, BaseDataBindingHolder<ItemFfmpegM3u8ToMp4Binding>>(
        R.layout.item_ffmpeg_m3u8_to_mp4
    ) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemFfmpegM3u8ToMp4Binding>,
        item: FFmpegM3u8ResultData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemFfmpegMp4ToMp3FileTitleTv.text = item.m3u8FileName
            mDataBind.itemFfmpegMp4ToMp3TsSegmentSizeTv.text = "${item.segmentList.size}个片段"
            if (item.coverBitmap == null) {
                mDataBind.itemFfmpegMp4ToMp3FileImage.setImageResource(R.drawable.ic_launcher)
            } else {
                mDataBind.itemFfmpegMp4ToMp3FileImage.setImageBitmap(item.coverBitmap)
            }
            var fileSize = 0L
            item.segmentList.forEach { segment ->
                fileSize += segment.fileSize
            }
            mDataBind.itemFfmpegMp4ToMp3FileSizeTv.text = "大小:${formatFileSize(fileSize)}"
            mDataBind.itemFfmpegMp4ToMp3RootLayout.setOnClickListener {
                onM3u8ItemClickListener?.viewDetailsItemClick(item, holder.layoutPosition)
            }
            mDataBind.itemFfmpegMp4ToMp3ConvertLayout.clickScale {
                onM3u8ItemClickListener?.convertItemClick(item, holder.layoutPosition)
            }
        }
    }

    private var onM3u8ItemClickListener: OnM3u8ItemClickListener? = null

    interface OnM3u8ItemClickListener {
        fun convertItemClick(m3u8Data: FFmpegM3u8ResultData, pos: Int)
        fun viewDetailsItemClick(m3u8Data: FFmpegM3u8ResultData, pos: Int)
    }

    fun setOnM3u8ItemClickListener(onM3u8ItemClickListener: OnM3u8ItemClickListener) {
        this.onM3u8ItemClickListener = onM3u8ItemClickListener
    }
}