package com.maunc.toolbox.ffmpeg.adapter

import android.annotation.SuppressLint
import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.luck.picture.lib.entity.LocalMedia
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.marquee
import com.maunc.toolbox.commonbase.utils.formatFileSize
import com.maunc.toolbox.databinding.ItemFfmpegMergeMp4Binding
import java.text.SimpleDateFormat
import java.util.Formatter
import java.util.Locale

class FFmpegMergeMp4Adapter :
    BaseQuickAdapter<LocalMedia, BaseDataBindingHolder<ItemFfmpegMergeMp4Binding>>(
        R.layout.item_ffmpeg_merge_mp4
    ) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemFfmpegMergeMp4Binding>,
        item: LocalMedia,
    ) {
        holder.dataBinding?.let { mDataBind ->
            Log.e(
                "ww",
                "duration:${item.duration},${item.dateAddedTime},${item.videoThumbnailPath}"
            )
            mDataBind.itemFfmpegMergeMp4FileTitleTv.text = item.fileName
            mDataBind.itemFfmpegMergeMp4FileTitleTv.marquee()
            mDataBind.itemFfmpegMergeMp4FileSizeTv.text = "大小:${formatFileSize(item.size)}"
            mDataBind.itemFfmpegMergeMp4FileTimeTv.text = "时长:${stringForTime(item.duration)}"
            mDataBind.itemFfmpegMergeMp4FileAddTimeTv.text =
                "修改日期:${dateFormat.format(item.dateAddedTime * 1000)}"
            if(holder.layoutPosition == 0) {
                mDataBind.itemFfmpegMergeMp4RootLayout.setBackgroundColor(R.color.green_seek)
            }
        }
    }

    fun stringForTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000L
        val seconds = totalSeconds % 60L
        val minutes = totalSeconds / 60L % 60L
        val hours = totalSeconds / 3600L
        val stringBuilder = StringBuilder()
        val mFormatter = Formatter(stringBuilder, Locale.getDefault())
        return if (hours > 0L) mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
            .toString() else mFormatter.format("%02d:%02d", minutes, seconds).toString()
    }
}