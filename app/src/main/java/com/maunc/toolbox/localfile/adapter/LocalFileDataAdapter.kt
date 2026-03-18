package com.maunc.toolbox.localfile.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.loadImage
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.databinding.ItemLocalFileDataBinding
import com.maunc.toolbox.localfile.data.LocalFileData
import java.io.File

/**
 * 本地文件/文件夹列表适配器（目录优先）。
 */
class LocalFileDataAdapter :
    BaseQuickAdapter<LocalFileData, BaseDataBindingHolder<ItemLocalFileDataBinding>>(
        R.layout.item_local_file_data,
    ) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemLocalFileDataBinding>,
        item: LocalFileData,
    ) {
        holder.dataBinding?.apply {
            itemLocalFileNameTv.text = item.fileName
            itemLocalFileExpandIv.visibleOrGone(item.fileType == LocalFileData.LOCAL_FILE_TYPE_DIR)
            when (item.fileType) {
                LocalFileData.LOCAL_FILE_TYPE_DIR -> {
                    itemLocalFileTypeImage.setImageResource(R.drawable.icon_local_file_dir)
                }

                LocalFileData.LOCAL_FILE_TYPE_IMAGE -> {
                    itemLocalFileTypeImage.loadImage(File(item.absolutePath))
                }

                LocalFileData.LOCAL_FILE_TYPE_VIDEO -> {
                    itemLocalFileTypeImage.setImageResource(R.drawable.icon_local_file_video)
                }

                LocalFileData.LOCAL_FILE_TYPE_AUDIO -> {
                    itemLocalFileTypeImage.setImageResource(R.drawable.icon_local_file_audio)
                }

                LocalFileData.LOCAL_FILE_TYPE_DOCUMENT -> {
                    itemLocalFileTypeImage.setImageResource(R.drawable.icon_local_file_doc)
                }

                LocalFileData.LOCAL_FILE_TYPE_ARCHIVE -> {
                    itemLocalFileTypeImage.setImageResource(R.drawable.icon_local_file_zip)
                }

                LocalFileData.LOCAL_FILE_TYPE_APK -> {
                }

                LocalFileData.LOCAL_FILE_TYPE_TEXT -> {
                    itemLocalFileTypeImage.setImageResource(R.drawable.icon_local_file_text)
                }

                LocalFileData.LOCAL_FILE_TYPE_UNKNOWN -> {
                    itemLocalFileTypeImage.setImageResource(R.drawable.icon_local_file_unknown)
                }
            }
        }
    }
}

