package com.maunc.toolbox.signaturecanvas.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.loadImage
import com.maunc.toolbox.commonbase.ext.obtainScreenWidth
import com.maunc.toolbox.databinding.ItemCanvasSaveFileBinding
import com.maunc.toolbox.signaturecanvas.data.CanvasSaveFileData

class SignatureCanvasSaveFileAdapter :
    BaseQuickAdapter<CanvasSaveFileData, BaseDataBindingHolder<ItemCanvasSaveFileBinding>>(
        R.layout.item_canvas_save_file
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCanvasSaveFileBinding>,
        item: CanvasSaveFileData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            val layoutParams = mDataBind.itemCanvasSaveFileImage.layoutParams
            layoutParams.width = context.obtainScreenWidth() / 2
            mDataBind.itemCanvasSaveFileImage.layoutParams = layoutParams
            mDataBind.itemCanvasSaveFileImage.loadImage(item.uri)
        }
    }
}