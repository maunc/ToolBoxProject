package com.maunc.toolbox.ftp.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.base.ext.visibleOrGone
import com.maunc.toolbox.databinding.ItemFtpRemoteFileDataBinding
import com.maunc.toolbox.ftp.data.FtpRemoteFileData

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
                FtpRemoteFileData.LOCAL_FILE_TYPE_DIR -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_dir)
                }

                FtpRemoteFileData.LOCAL_FILE_TYPE_IMAGE -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_image)
                }

                FtpRemoteFileData.LOCAL_FILE_TYPE_VIDEO -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_video)
                }

                FtpRemoteFileData.LOCAL_FILE_TYPE_AUDIO -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_audio)
                }

                FtpRemoteFileData.LOCAL_FILE_TYPE_DOCUMENT -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_doc)
                }

                FtpRemoteFileData.LOCAL_FILE_TYPE_ARCHIVE -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_zip)
                }

                FtpRemoteFileData.LOCAL_FILE_TYPE_APK -> {
                }

                FtpRemoteFileData.LOCAL_FILE_TYPE_TEXT -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_text)
                }

                FtpRemoteFileData.LOCAL_FILE_TYPE_UNKNOWN -> {
                    itemFtpRemoteFileTypeImage.setImageResource(R.drawable.icon_local_file_unknown)
                }
            }
        }
    }
}

