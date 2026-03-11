package com.maunc.toolbox.pushbox.adapter

import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.pushbox.data.PushBoxSettingData

class PushBoxSettingAdapter : BaseMultiItemQuickAdapter<PushBoxSettingData, BaseViewHolder>() {

    init {
        addItemType(
            PushBoxSettingData.PUSH_BOX_MAP_PREVIEW,
            R.layout.item_push_box_setting_data
        )
    }

    override fun convert(holder: BaseViewHolder, item: PushBoxSettingData) {
        val itemPosition = holder.layoutPosition
        val haveView = holder.itemView
        haveView.findViewById<TextView>(R.id.item_push_box_setting_type_tv).apply {
            text = item.title
        }
        val moreLayout = haveView.findViewById<RelativeLayout>(R.id.item_push_box_setting_more_layout)
        val expandIv = haveView.findViewById<ImageView>(R.id.item_push_box_setting_expand_iv)
        moreLayout.visibleOrGone(item.isExpand)
        expandIv.setImageResource(
            if (item.isExpand) R.drawable.icon_group_expand_yes else R.drawable.icon_group_expand_no
        )
        val baseSettingView = haveView.findViewById<RelativeLayout>(R.id.item_push_box_setting_tab)
        baseSettingView.setOnClickListener {
            val settingData = data[itemPosition]
            settingData.isExpand = !settingData.isExpand
            notifyItemChanged(itemPosition)
        }
        when(item.itemType) {
            PushBoxSettingData.PUSH_BOX_MAP_PREVIEW->{
                baseSettingView.setOnClickListener {
                    onPushBoxSettingEventListener?.startPreviewPage()
                }
            }
        }
    }

    private var onPushBoxSettingEventListener: OnPushBoxSettingEventListener? = null

    fun setOnPushBoxSettingEventListener(onPushBoxSettingEventListener: OnPushBoxSettingEventListener) {
        this.onPushBoxSettingEventListener = onPushBoxSettingEventListener
    }

    interface OnPushBoxSettingEventListener {
        fun startPreviewPage()
    }
}