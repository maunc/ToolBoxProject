package com.maunc.toolbox.pushbox.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.obtainDrawable
import com.maunc.toolbox.databinding.ItemPushBoxMainFunctionBinding
import com.maunc.toolbox.pushbox.data.PushBoxMainFunctionData

class PushBoxMainFunctionAdapter :
    BaseQuickAdapter<PushBoxMainFunctionData, BaseDataBindingHolder<ItemPushBoxMainFunctionBinding>>(
        R.layout.item_push_box_main_function
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemPushBoxMainFunctionBinding>,
        item: PushBoxMainFunctionData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemPushBoxMainFunctionRoot.background = obtainDrawable(item.backgroundRes)
            mDataBind.itemPushBoxMainFunctionImg.setImageResource(item.img)
            mDataBind.itemPushBoxMainFunctionTv.text = item.title
            mDataBind.itemPushBoxMainFunctionRoot.clickScale {
                onPushBoxFunctionListener?.onClick(item.functionType)
            }
        }
    }

    private var onPushBoxFunctionListener: OnPushBoxFunctionListener? = null

    fun setOnPushBoxFunctionListener(onPushBoxFunctionListener: OnPushBoxFunctionListener) {
        this.onPushBoxFunctionListener = onPushBoxFunctionListener
    }

    fun interface OnPushBoxFunctionListener {
        fun onClick(functionType: Int)
    }
}