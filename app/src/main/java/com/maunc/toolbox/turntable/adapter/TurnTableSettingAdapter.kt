package com.maunc.toolbox.turntable.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.turnTableEnableTouch
import com.maunc.toolbox.turntable.data.TurnTableSettingData
import com.us.mauncview.SwitchButtonView

class TurnTableSettingAdapter : BaseMultiItemQuickAdapter<TurnTableSettingData, BaseViewHolder>() {

    init {
        addItemType(
            TurnTableSettingData.TURN_TABLE_SELECT_DATA_TYPE,
            R.layout.item_turn_table_setting_data
        )
        addItemType(
            TurnTableSettingData.TURN_TABLE_MANAGER_DATA_TYPE,
            R.layout.item_turn_table_setting_data
        )
        addItemType(
            TurnTableSettingData.TURN_TABLE_ENABLE_TOUCH_TYPE,
            R.layout.item_turn_table_setting_switch_data
        )
        addItemType(
            TurnTableSettingData.TURN_TABLE_DELETE_ALL_DATA_TYPE,
            R.layout.item_turn_table_setting_data
        )
        addItemType(
            TurnTableSettingData.TURN_TABLE_ANIM_INTERPOLATOR_TYPE,
            R.layout.item_turn_table_setting_anim_interpolator
        )
    }

    private var enableTouch = false

    fun obtainEnableTouch() = enableTouch

    override fun convert(holder: BaseViewHolder, item: TurnTableSettingData) {
        val itemPosition = getItemPosition(item)
        val haveView = holder.itemView
        haveView.findViewById<TextView>(R.id.item_turn_table_setting_type_tv).apply {
            text = item.settingType
        }
        val moreLayout =
            haveView.findViewById<RelativeLayout>(R.id.item_turn_table_setting_more_layout)
        val expandIv = haveView.findViewById<ImageView>(R.id.item_turn_table_setting_expand_iv)
        moreLayout.visibleOrGone(item.isExpand)
        expandIv.setImageResource(
            if (item.isExpand) R.drawable.icon_group_expand_yes else R.drawable.icon_group_expand_no
        )
        val baseSettingView =
            haveView.findViewById<RelativeLayout>(R.id.item_turn_table_setting_tab)
        baseSettingView.setOnClickListener {
            val settingData = data[itemPosition]
            settingData.isExpand = !settingData.isExpand
            notifyItemChanged(itemPosition)
        }
        when (item.itemType) {
            TurnTableSettingData.TURN_TABLE_SELECT_DATA_TYPE -> {
                baseSettingView.setOnClickListener {
                    onTurnTableSettingEventListener?.showTurnTableDataPage()
                }
            }

            TurnTableSettingData.TURN_TABLE_MANAGER_DATA_TYPE -> {
                baseSettingView.setOnClickListener {
                    onTurnTableSettingEventListener?.startDataMangerPage()
                }
            }

            TurnTableSettingData.TURN_TABLE_DELETE_ALL_DATA_TYPE -> {
                baseSettingView.setOnClickListener {
                    onTurnTableSettingEventListener?.deleteAllTurnTableData()
                }
            }

            TurnTableSettingData.TURN_TABLE_ENABLE_TOUCH_TYPE -> {
                val enableTouchSwitch =
                    haveView.findViewById<SwitchButtonView>(R.id.item_turn_table_setting_switch)
                enableTouchSwitch.isChecked = enableTouch
                enableTouchSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    enableTouchSwitch.isChecked = isChecked
                    obtainMMKV.putBoolean(turnTableEnableTouch, isChecked)
                    enableTouch = isChecked
                }
            }

            TurnTableSettingData.TURN_TABLE_ANIM_INTERPOLATOR_TYPE -> {

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setConfig(
        enableTouch: Boolean,
    ) {
        this.enableTouch = enableTouch
        notifyDataSetChanged()
    }

    private var onTurnTableSettingEventListener: OnTurnTableSettingEventListener? = null

    interface OnTurnTableSettingEventListener {
        fun showTurnTableDataPage()
        fun startDataMangerPage()
        fun deleteAllTurnTableData()
    }

    fun setOnTurnTableSettingListener(onTurnTableSettingEventListener: OnTurnTableSettingEventListener) {
        this.onTurnTableSettingEventListener = onTurnTableSettingEventListener
    }
}