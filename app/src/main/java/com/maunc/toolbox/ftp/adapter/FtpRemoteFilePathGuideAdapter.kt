package com.maunc.toolbox.ftp.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.base.ext.obtainColor
import com.maunc.base.ext.obtainDrawable
import com.maunc.base.ext.visibleOrGone
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemFtpRemoteFilePathGuideBinding
import com.maunc.toolbox.ftp.data.FtpRemoteFilePathGuideData

class FtpRemoteFilePathGuideAdapter :
    BaseQuickAdapter<FtpRemoteFilePathGuideData, BaseDataBindingHolder<ItemFtpRemoteFilePathGuideBinding>>(
        R.layout.item_ftp_remote_file_path_guide
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemFtpRemoteFilePathGuideBinding>,
        item: FtpRemoteFilePathGuideData,
    ) {
        holder.dataBinding?.apply {
            itemFtpRemoteFilePathGuideTv.text = item.lastPathName
            val isLast = holder.bindingAdapterPosition == data.size - 1
            itemFtpRemoteFilePathGuideArrow.visibleOrGone(!isLast)
            itemFtpRemoteFilePathGuideTv.setTextColor(
                if (isLast) obtainColor(R.color.white) else obtainColor(R.color.black)
            )
            itemFtpRemoteFilePathGuideTv.setBackgroundDrawable(
                if (isLast) obtainDrawable(R.drawable.bg_radius_24_blue)
                else obtainDrawable(R.drawable.bg_radius_24_f2f2f2)
            )
        }
    }
}