package com.maunc.toolbox.torrent.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.utils.formatFileSize
import com.maunc.toolbox.databinding.ItemTorrentFileBinding
import com.maunc.toolbox.torrent.data.TorrentFileData

class TorrentFileAdapter :
    BaseQuickAdapter<TorrentFileData, BaseDataBindingHolder<ItemTorrentFileBinding>>(
        R.layout.item_torrent_file,
    ) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemTorrentFileBinding>,
        item: TorrentFileData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemTorrentFileRowTitleTv.text = item.fileName
            mDataBind.itemTorrentFileRowSizeTv.text =
                String.format(
                    obtainString(R.string.torrent_main_file_size_label),
                    formatFileSize(item.fileSizeBytes),
                )
        }
    }
}
