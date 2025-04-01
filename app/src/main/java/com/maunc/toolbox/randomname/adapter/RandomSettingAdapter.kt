package com.maunc.toolbox.randomname.adapter

import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.launchVibrator
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.commonbase.utils.RANDOM_AUTO
import com.maunc.toolbox.commonbase.utils.RANDOM_MANUAL
import com.maunc.toolbox.commonbase.utils.RANDOM_NOW
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomButtonClickVibrator
import com.maunc.toolbox.commonbase.utils.randomEggs
import com.maunc.toolbox.commonbase.utils.randomSelectRecyclerVisible
import com.maunc.toolbox.commonbase.utils.randomSpeed
import com.maunc.toolbox.commonbase.utils.randomType
import com.maunc.toolbox.randomname.data.RandomSettingData
import com.us.mauncview.SwitchButtonView

class RandomSettingAdapter : BaseMultiItemQuickAdapter<RandomSettingData, BaseViewHolder>() {

    init {
        addItemType(RandomSettingData.RANDOM_SLEEP_TYPE, R.layout.item_random_setting_sleep)
        addItemType(
            RandomSettingData.RANDOM_BUTTON_VIBRATOR_TYPE,
            R.layout.item_random_setting_vibrator
        )
        addItemType(
            RandomSettingData.RANDOM_NOT_IS_SELECT_TYPE,
            R.layout.item_random_setting_select
        )
        addItemType(
            RandomSettingData.RANDOM_NAME_TYPE_TYPE,
            R.layout.item_random_setting_random_type
        )
        addItemType(RandomSettingData.RANDOM_BUTTON_EGGS_TYPE, R.layout.item_random_setting_eggs)
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
            RandomSettingData.RANDOM_BUTTON_VIBRATOR_TYPE -> {
                val vibratorSwitch =
                    haveView.findViewById<SwitchButtonView>(R.id.item_random_vibrator_switch)
                vibratorSwitch.isChecked = obtainMMKV.getBoolean(randomButtonClickVibrator)
                vibratorSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    vibratorSwitch.isChecked = isChecked
                    obtainMMKV.putBoolean(randomButtonClickVibrator, isChecked)
                    if (obtainMMKV.getBoolean(randomButtonClickVibrator)) launchVibrator()
                }
            }

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
                    320L -> radioGroup.check(radioButtonMin.id)
                    120L -> radioGroup.check(radioButtonMedium.id)
                    20L -> radioGroup.check(radioButtonMax.id)
                }
                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    group.check(checkedId)
                    when (checkedId) {
                        radioButtonMin.id -> obtainMMKV.putLong(randomSpeed, 320L)
                        radioButtonMedium.id -> obtainMMKV.putLong(randomSpeed, 120L)
                        radioButtonMax.id -> obtainMMKV.putLong(randomSpeed, 20L)
                    }
                }
            }

            RandomSettingData.RANDOM_NOT_IS_SELECT_TYPE -> {
                val selectRecyclerVisibleSwitch =
                    haveView.findViewById<SwitchButtonView>(R.id.item_random_select_recycler_visible_switch)
                selectRecyclerVisibleSwitch.isChecked =
                    obtainMMKV.getBoolean(randomSelectRecyclerVisible)
                selectRecyclerVisibleSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    selectRecyclerVisibleSwitch.isChecked = isChecked
                    obtainMMKV.putBoolean(randomSelectRecyclerVisible, isChecked)
                    if (obtainMMKV.getBoolean(randomButtonClickVibrator)) launchVibrator()
                }
            }

            RandomSettingData.RANDOM_NAME_TYPE_TYPE -> {
                val radioGroup =
                    haveView.findViewById<RadioGroup>(R.id.item_random_setting_random_type_radio_group)
                val radioButtonNow =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_random_type_now)
                val radioButtonAuto =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_random_type_auto)
                val radioButtonManual =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_random_type_manual)
                when (obtainMMKV.getInt(randomType)) {
                    RANDOM_NOW -> radioGroup.check(radioButtonNow.id)
                    RANDOM_AUTO -> radioGroup.check(radioButtonAuto.id)
                    RANDOM_MANUAL -> radioGroup.check(radioButtonManual.id)
                }
                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    group.check(checkedId)
                    when (checkedId) {
                        radioButtonNow.id -> obtainMMKV.putInt(randomType, RANDOM_NOW)
                        radioButtonAuto.id -> obtainMMKV.putInt(randomType, RANDOM_AUTO)
                        radioButtonManual.id -> obtainMMKV.putInt(randomType, RANDOM_MANUAL)
                    }
                }
            }

            RandomSettingData.RANDOM_BUTTON_EGGS_TYPE -> {
                val eggsSwitch =
                    haveView.findViewById<SwitchButtonView>(R.id.item_random_eggs_switch)
                eggsSwitch.isChecked = obtainMMKV.getBoolean(randomEggs)
                eggsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    eggsSwitch.isChecked = isChecked
                    obtainMMKV.putBoolean(randomEggs, isChecked)
                    if (obtainMMKV.getBoolean(randomButtonClickVibrator)) launchVibrator()
                }
            }
        }
    }
}