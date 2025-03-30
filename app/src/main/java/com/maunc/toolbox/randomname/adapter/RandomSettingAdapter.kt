package com.maunc.toolbox.randomname.adapter

import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomButtonClickVibrator
import com.maunc.toolbox.commonbase.utils.randomSpeed
import com.maunc.toolbox.randomname.constant.mediaPlayer
import com.maunc.toolbox.randomname.data.RandomSettingData

class RandomSettingAdapter : BaseMultiItemQuickAdapter<RandomSettingData, BaseViewHolder>() {

    init {
        addItemType(RandomSettingData.RANDOM_SLEEP_TYPE, R.layout.item_random_setting_sleep)
        addItemType(
            RandomSettingData.RANDOM_BUTTON_CLICK_VIBRATOR_TYPE,
            R.layout.item_random_setting_vibrator
        )
    }

    override fun convert(holder: BaseViewHolder, item: RandomSettingData) {
        val itemPosition = getItemPosition(item)
        val haveView = holder.itemView
        haveView.findViewById<TextView>(R.id.item_random_setting_type_tv).apply {
            text = item.settingType
        }
        val moreLayout = haveView.findViewById<RelativeLayout>(R.id.item_random_setting_more_layout)
        val expandIv = haveView.findViewById<ImageView>(R.id.item_random_setting_expand_iv)
        moreLayout.visibleOrGone(item.isExpand)
        expandIv.setImageResource(
            if (item.isExpand) {
                R.drawable.icon_group_expand_yes
            } else {
                R.drawable.icon_group_expand_no
            }
        )
        haveView.findViewById<RelativeLayout>(R.id.item_random_setting_tab).setOnClickListener {
            val settingData = data[itemPosition]
            settingData.isExpand = !settingData.isExpand
            notifyItemChanged(itemPosition)
        }
        when (item.itemType) {
            RandomSettingData.RANDOM_SLEEP_TYPE -> {
                val radioGroup =
                    haveView.findViewById<RadioGroup>(R.id.item_random_setting_sleep_radio_group)
                val radioButtonMin =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_sleep_min)
                val radioButtonMedium =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_sleep_medium)
                val radioButtonMax =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_sleep_max)
                when (obtainMMKV.getLong(randomSpeed)) {
                    50L -> radioGroup.check(radioButtonMin.id)
                    30L -> radioGroup.check(radioButtonMedium.id)
                    10L -> radioGroup.check(radioButtonMax.id)
                }
                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    group.check(checkedId)
                    when (checkedId) {
                        radioButtonMin.id -> obtainMMKV.putLong(randomSpeed, 50L)
                        radioButtonMedium.id -> obtainMMKV.putLong(randomSpeed, 30L)
                        radioButtonMax.id -> obtainMMKV.putLong(randomSpeed, 10L)
                    }
                }
            }

            RandomSettingData.RANDOM_BUTTON_CLICK_VIBRATOR_TYPE -> {
                val vibratorSwitch = haveView.findViewById<Switch>(R.id.item_random_vibrator_switch)
                vibratorSwitch.isChecked = obtainMMKV.getBoolean(randomButtonClickVibrator)
                vibratorSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    vibratorSwitch.isChecked = isChecked
                    obtainMMKV.putBoolean(randomButtonClickVibrator, isChecked)
                    if (obtainMMKV.getBoolean(randomButtonClickVibrator)) mediaPlayer.start()
                }
            }
        }
    }
}