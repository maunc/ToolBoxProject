package com.maunc.toolbox.ffmpeg.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.luck.picture.lib.entity.LocalMedia
import com.maunc.base.ext.gone
import com.maunc.base.ext.marquee
import com.maunc.base.ext.visible
import com.maunc.toolbox.R
import com.maunc.base.utils.formatFileSize
import com.maunc.toolbox.databinding.ItemFfmpegMergeMp4Binding
import com.maunc.toolbox.ffmpeg.constant.stringForTime
import java.text.SimpleDateFormat
import java.util.Locale

class FFmpegMergeMp4Adapter :
    BaseQuickAdapter<LocalMedia, BaseDataBindingHolder<ItemFfmpegMergeMp4Binding>>(
        R.layout.item_ffmpeg_merge_mp4
    ) {

    private var selectMp4Pos = -1

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    @SuppressLint("NotifyDataSetChanged")
    fun selectMp4(pos: Int) {
        this.selectMp4Pos = pos
        notifyDataSetChanged()
    }

    fun obtainCurrentSelectPos() = selectMp4Pos

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemFfmpegMergeMp4Binding>,
        item: LocalMedia,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemFfmpegMergeMp4FileTitleTv.text = item.fileName
            mDataBind.itemFfmpegMergeMp4FileTitleTv.marquee()
            mDataBind.itemFfmpegMergeMp4FileSizeTv.text = "大小:${formatFileSize(item.size)}"
            mDataBind.itemFfmpegMergeMp4FileTimeTv.text = "时长:${stringForTime(item.duration)}"
            mDataBind.itemFfmpegMergeMp4FileAddTimeTv.text =
                "修改日期:${dateFormat.format(item.dateAddedTime * 1000)}"
            if (holder.bindingAdapterPosition == selectMp4Pos) {
                mDataBind.itemFfmpegMergeMp4FilePlayFlag.play().visible()
            } else {
                mDataBind.itemFfmpegMergeMp4FilePlayFlag.stop().gone()
            }
        }
    }
}