package com.maunc.toolbox.pushbox.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MAX
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MAX_TWO
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MEDIUM
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MIN
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MIN_TWO
import com.maunc.toolbox.pushbox.data.PushBoxSettingData

class PushBoxSettingAdapter : BaseMultiItemQuickAdapter<PushBoxSettingData, BaseViewHolder>() {

    init {
        addItemType(
            PushBoxSettingData.PUSH_BOX_MAP_PREVIEW_TYPE,
            R.layout.item_push_box_setting_data
        )
        addItemType(
            PushBoxSettingData.PUSH_BOX_CONTROLLER_SIZE,
            R.layout.item_push_box_setting_controller_size
        )
    }

    private var controllerSize = PUSH_BOX_CONTROLLER_SIZE_MEDIUM

    override fun convert(holder: BaseViewHolder, item: PushBoxSettingData) {
        val itemPosition = holder.layoutPosition
        val haveView = holder.itemView
        haveView.findViewById<TextView>(R.id.item_push_box_setting_type_tv).apply {
            text = item.title
        }
        val moreLayout =
            haveView.findViewById<RelativeLayout>(R.id.item_push_box_setting_more_layout)
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
        when (item.itemType) {
            PushBoxSettingData.PUSH_BOX_MAP_PREVIEW_TYPE -> {
                baseSettingView.setOnClickListener {
                    onPushBoxSettingEventListener?.startPreviewPage()
                }
            }

            PushBoxSettingData.PUSH_BOX_CONTROLLER_SIZE -> {
                val radioGroup =
                    haveView.findViewById<RadioGroup>(R.id.item_push_box_setting_controller_size_group)
                val radioButtonMin =
                    haveView.findViewById<RadioButton>(R.id.item_push_box_setting_controller_size_min)
                val radioButtonMinTwo =
                    haveView.findViewById<RadioButton>(R.id.item_push_box_setting_controller_size_min_two)
                val radioButtonMedium =
                    haveView.findViewById<RadioButton>(R.id.item_push_box_setting_controller_size_medium)
                val radioButtonMaxTwo =
                    haveView.findViewById<RadioButton>(R.id.item_push_box_setting_controller_size_max_two)
                val radioButtonMax =
                    haveView.findViewById<RadioButton>(R.id.item_push_box_setting_controller_size_max)
                when (controllerSize) {
                    PUSH_BOX_CONTROLLER_SIZE_MIN -> radioGroup.check(radioButtonMin.id)
                    PUSH_BOX_CONTROLLER_SIZE_MIN_TWO -> radioGroup.check(radioButtonMinTwo.id)
                    PUSH_BOX_CONTROLLER_SIZE_MEDIUM -> radioGroup.check(radioButtonMedium.id)
                    PUSH_BOX_CONTROLLER_SIZE_MAX_TWO -> radioGroup.check(radioButtonMaxTwo.id)
                    PUSH_BOX_CONTROLLER_SIZE_MAX -> radioGroup.check(radioButtonMax.id)
                }
                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    group.check(checkedId)
                    when (checkedId) {
                        radioButtonMin.id -> onPushBoxSettingEventListener?.configControllerSize(
                            PUSH_BOX_CONTROLLER_SIZE_MIN
                        )

                        radioButtonMinTwo.id -> onPushBoxSettingEventListener?.configControllerSize(
                            PUSH_BOX_CONTROLLER_SIZE_MIN_TWO
                        )

                        radioButtonMedium.id -> onPushBoxSettingEventListener?.configControllerSize(
                            PUSH_BOX_CONTROLLER_SIZE_MEDIUM
                        )

                        radioButtonMaxTwo.id -> onPushBoxSettingEventListener?.configControllerSize(
                            PUSH_BOX_CONTROLLER_SIZE_MAX_TWO
                        )

                        radioButtonMax.id -> onPushBoxSettingEventListener?.configControllerSize(
                            PUSH_BOX_CONTROLLER_SIZE_MAX
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setConfig(
        controllerSize: Int,
    ) {
        this.controllerSize = controllerSize
        notifyDataSetChanged()
    }

    private var onPushBoxSettingEventListener: OnPushBoxSettingEventListener? = null

    fun setOnPushBoxSettingEventListener(onPushBoxSettingEventListener: OnPushBoxSettingEventListener) {
        this.onPushBoxSettingEventListener = onPushBoxSettingEventListener
    }

    interface OnPushBoxSettingEventListener {
        fun startPreviewPage()
        fun configControllerSize(size: Int)
    }
}