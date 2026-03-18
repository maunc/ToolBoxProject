package com.maunc.toolbox.ftp.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.databinding.ItemFtpRemoteFileDataBinding
import com.maunc.toolbox.ftp.data.FtpRemoteFileData
import com.maunc.toolbox.localfile.data.LocalFileData

/**
 * FTP 远程文件列表适配器
 */
class FtpRemoteFileAdapter :
    BaseQuickAdapter<FtpRemoteFileData, BaseDataBindingHolder<ItemFtpRemoteFileDataBinding>>(
        R.layout.item_ftp_remote_file_data,
    ) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemFtpRemoteFileDataBinding>,
        item: FtpRemoteFileData,
    ) {
        holder.dataBinding?.apply {
            itemFtpRemoteFileNameTv.text = item.name
            itemFtpRemoteFileExpandIv.visibleOrGone(item.isDir)
            when (item.fileType) {
                LocalFileData.LOCAL_FILE_TYPE_DIR -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_dir)
                }

                LocalFileData.LOCAL_FILE_TYPE_IMAGE -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_image)
                }

                LocalFileData.LOCAL_FILE_TYPE_VIDEO -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_video)
                }

                LocalFileData.LOCAL_FILE_TYPE_AUDIO -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_audio)
                }

                LocalFileData.LOCAL_FILE_TYPE_DOCUMENT -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_doc)
                }

                LocalFileData.LOCAL_FILE_TYPE_ARCHIVE -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_zip)
                }

                LocalFileData.LOCAL_FILE_TYPE_APK -> {
                }

                LocalFileData.LOCAL_FILE_TYPE_TEXT -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_text)
                }

                LocalFileData.LOCAL_FILE_TYPE_UNKNOWN -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_unknown)
                }
            }
        }
    }
}

