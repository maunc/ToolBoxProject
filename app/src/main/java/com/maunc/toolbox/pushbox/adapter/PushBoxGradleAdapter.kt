package com.maunc.toolbox.pushbox.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.obtainDrawable
import com.maunc.toolbox.databinding.ItemPushBoxGradleBinding
import com.maunc.toolbox.pushbox.data.PushBoxGradleData

class PushBoxGradleAdapter :
    BaseQuickAdapter<PushBoxGradleData, BaseDataBindingHolder<ItemPushBoxGradleBinding>>(
        R.layout.item_push_box_gradle
    ) {

    private var selectIndex = 0//默认0

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectIndex(index: Int) {
        this.selectIndex = index
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemPushBoxGradleBinding>,
        item: PushBoxGradleData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            if (selectIndex == holder.layoutPosition) {
                mDataBind.itemPushBoxGradleTv.setBackgroundDrawable(
                    obtainDrawable(R.drawable.bg_white_stroke_blue_radius_12)
                )
            } else {
                mDataBind.itemPushBoxGradleTv.setBackgroundDrawable(
                    obtainDrawable(R.drawable.bg_white_stroke_black_radius_12)
                )
            }
            mDataBind.itemPushBoxGradleTv.text = "${item.index + 1}"
        }
    }
}