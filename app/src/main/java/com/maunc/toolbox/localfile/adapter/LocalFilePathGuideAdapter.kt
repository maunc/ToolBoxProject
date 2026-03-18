package com.maunc.toolbox.localfile.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.obtainColor
import com.maunc.toolbox.commonbase.ext.obtainDrawable
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.databinding.ItemLocalFilePathGuideBinding
import com.maunc.toolbox.localfile.data.LocalFilePathGuideData

class LocalFilePathGuideAdapter :
    BaseQuickAdapter<LocalFilePathGuideData, BaseDataBindingHolder<ItemLocalFilePathGuideBinding>>(
        R.layout.item_local_file_path_guide
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemLocalFilePathGuideBinding>,
        item: LocalFilePathGuideData,
    ) {
        holder.dataBinding?.apply {
            itemLocalFilePathGuideTv.text = item.lastPathName
            val isLast = holder.bindingAdapterPosition == data.size - 1
            itemLocalFilePathGuideArrow.visibleOrGone(!isLast)
            itemLocalFilePathGuideTv.setTextColor(
                if (isLast) obtainColor(R.color.white) else obtainColor(R.color.black)
            )
            itemLocalFilePathGuideTv.setBackgroundDrawable(
                if (isLast) obtainDrawable(R.drawable.bg_radius_24_blue)
                else obtainDrawable(R.drawable.bg_radius_24_f2f2f2)
            )
        }
    }
}