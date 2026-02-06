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
import com.maunc.toolbox.commonbase.ext.marquee
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomButtonClickVibrator
import com.maunc.toolbox.commonbase.utils.randomEggs
import com.maunc.toolbox.commonbase.utils.randomListSortType
import com.maunc.toolbox.commonbase.utils.randomSelectRecyclerVisible
import com.maunc.toolbox.commonbase.utils.randomSpeed
import com.maunc.toolbox.commonbase.utils.randomType
import com.maunc.toolbox.randomname.constant.RANDOM_AUTO
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_DESC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_NAME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_NAME_DESC
import com.maunc.toolbox.randomname.constant.RANDOM_MANUAL
import com.maunc.toolbox.randomname.constant.RANDOM_NOW
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MAX
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MEDIUM
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MIN
import com.maunc.toolbox.randomname.data.RandomSettingData
import com.us.mauncview.SwitchButtonView

class RandomSettingAdapter : BaseMultiItemQuickAdapter<RandomSettingData, BaseViewHolder>() {

    init {
        addItemType(
            RandomSettingData.RANDOM_SPEED_TYPE,
            R.layout.item_random_setting_speed
        )
        addItemType(
            RandomSettingData.RANDOM_BUTTON_VIBRATOR_TYPE,
            R.layout.item_random_setting_vibrator
        )
        addItemType(
            RandomSettingData.RANDOM_SELECT_LIST_TYPE,
            R.layout.item_random_setting_select
        )
        addItemType(
            RandomSettingData.RANDOM_NAME_TYPE_TYPE,
            R.layout.item_random_setting_random_type
        )
        addItemType(
            RandomSettingData.RANDOM_MANAGE_SORT_TYPE,
            R.layout.item_random_setting_db_sort
        )
        addItemType(
            RandomSettingData.RANDOM_DELETE_ALL_DATA_TYPE,
            R.layout.item_random_setting_data
        )
        addItemType(
            RandomSettingData.RANDOM_MANAGER_DATA_TYPE,
            R.layout.item_random_setting_data
        )
        addItemType(
            RandomSettingData.RANDOM_BUTTON_EGGS_TYPE,
            R.layout.item_random_setting_eggs
        )
    }

    override fun convert(
        holder: BaseViewHolder,
        item: RandomSettingData,
    ) {
        val itemPosition = getItemPosition(item)
        val haveView = holder.itemView
        haveView.findViewById<TextView>(R.id.item_random_setting_type_tv).apply {
            text = item.settingType
        }
        val moreLayout = haveView.findViewById<RelativeLayout>(R.id.item_random_setting_more_layout)
        val expandIv = haveView.findViewById<ImageView>(R.id.item_random_setting_expand_iv)
        moreLayout.visibleOrGone(item.isExpand)
        expandIv.setImageResource(
            if (item.isExpand) R.drawable.icon_group_expand_yes else R.drawable.icon_group_expand_no
        )
        val baseSettingView = haveView.findViewById<RelativeLayout>(R.id.item_random_setting_tab)
        baseSettingView.setOnClickListener {
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

            RandomSettingData.RANDOM_SPEED_TYPE -> {
                val radioGroup =
                    haveView.findViewById<RadioGroup>(R.id.item_random_setting_speed_radio_group)
                val radioButtonMin =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_speed_min)
                val radioButtonMedium =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_speed_medium)
                val radioButtonMax =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_speed_max)
                when (obtainMMKV.getLong(randomSpeed)) {
                    RANDOM_SPEED_MIN -> radioGroup.check(radioButtonMin.id)
                    RANDOM_SPEED_MEDIUM -> radioGroup.check(radioButtonMedium.id)
                    RANDOM_SPEED_MAX -> radioGroup.check(radioButtonMax.id)
                }
                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    group.check(checkedId)
                    when (checkedId) {
                        radioButtonMin.id -> obtainMMKV.putLong(randomSpeed, RANDOM_SPEED_MIN)
                        radioButtonMedium.id -> obtainMMKV.putLong(randomSpeed, RANDOM_SPEED_MEDIUM)
                        radioButtonMax.id -> obtainMMKV.putLong(randomSpeed, RANDOM_SPEED_MAX)
                    }
                }
            }

            RandomSettingData.RANDOM_SELECT_LIST_TYPE -> {
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
                val randomTypeTipsTv =
                    haveView.findViewById<TextView>(R.id.item_random_setting_random_type_tips_tv)
                val radioGroup =
                    haveView.findViewById<RadioGroup>(R.id.item_random_setting_random_type_radio_group)
                val radioButtonNow =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_random_type_now)
                val radioButtonAuto =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_random_type_auto)
                val radioButtonManual =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_random_type_manual)
                randomTypeTipsTv.marquee()
                when (obtainMMKV.getInt(randomType)) {
                    RANDOM_NOW -> {
                        radioGroup.check(radioButtonNow.id)
                        randomTypeTipsTv.text = obtainString(
                            R.string.random_setting_select_random_type_now_tips_text
                        )
                    }

                    RANDOM_AUTO -> {
                        radioGroup.check(radioButtonAuto.id)
                        randomTypeTipsTv.text = obtainString(
                            R.string.random_setting_select_random_type_auto_tips_text
                        )
                    }

                    RANDOM_MANUAL -> {
                        radioGroup.check(radioButtonManual.id)
                        randomTypeTipsTv.text = obtainString(
                            R.string.random_setting_select_random_type_manual_tips_text
                        )
                    }
                }
                randomTypeTipsTv.marquee()
                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    group.check(checkedId)
                    when (checkedId) {
                        radioButtonNow.id -> {
                            obtainMMKV.putInt(randomType, RANDOM_NOW)
                            randomTypeTipsTv.text = obtainString(
                                R.string.random_setting_select_random_type_now_tips_text
                            )
                        }

                        radioButtonAuto.id -> {
                            obtainMMKV.putInt(randomType, RANDOM_AUTO)
                            randomTypeTipsTv.text = obtainString(
                                R.string.random_setting_select_random_type_auto_tips_text
                            )
                        }

                        radioButtonManual.id -> {
                            obtainMMKV.putInt(randomType, RANDOM_MANUAL)
                            randomTypeTipsTv.text = obtainString(
                                R.string.random_setting_select_random_type_manual_tips_text
                            )
                        }
                    }
                    randomTypeTipsTv.marquee()
                }
            }

            RandomSettingData.RANDOM_MANAGE_SORT_TYPE -> {
                val radioGroup =
                    haveView.findViewById<RadioGroup>(R.id.item_random_setting_sort_radio_group)
                val radioButtonByTimeAsc =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_sort_by_time_asc)
                val radioButtonByTimeDesc =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_sort_by_time_desc)
                val radioButtonByNameAsc =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_sort_by_name_asc)
                val radioButtonByNameDesc =
                    haveView.findViewById<RadioButton>(R.id.item_random_setting_sort_by_name_desc)
                when (obtainMMKV.getInt(randomListSortType)) {
                    RANDOM_DB_SORT_BY_INSERT_TIME_ASC -> radioGroup.check(radioButtonByTimeAsc.id)
                    RANDOM_DB_SORT_BY_INSERT_TIME_DESC -> radioGroup.check(radioButtonByTimeDesc.id)
                    RANDOM_DB_SORT_BY_NAME_ASC -> radioGroup.check(radioButtonByNameAsc.id)
                    RANDOM_DB_SORT_BY_NAME_DESC -> radioGroup.check(radioButtonByNameDesc.id)
                }
                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    group.check(checkedId)
                    when (checkedId) {
                        radioButtonByTimeAsc.id -> obtainMMKV.putInt(
                            randomListSortType,
                            RANDOM_DB_SORT_BY_INSERT_TIME_ASC
                        )

                        radioButtonByTimeDesc.id -> obtainMMKV.putInt(
                            randomListSortType,
                            RANDOM_DB_SORT_BY_INSERT_TIME_DESC
                        )

                        radioButtonByNameAsc.id -> obtainMMKV.putInt(
                            randomListSortType,
                            RANDOM_DB_SORT_BY_NAME_ASC
                        )

                        radioButtonByNameDesc.id -> obtainMMKV.putInt(
                            randomListSortType,
                            RANDOM_DB_SORT_BY_NAME_DESC
                        )
                    }
                }
            }

            RandomSettingData.RANDOM_DELETE_ALL_DATA_TYPE -> {
                baseSettingView.setOnClickListener {
                    onRandomSettingEventListener?.deleteAllDataClick()
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

            RandomSettingData.RANDOM_MANAGER_DATA_TYPE -> {
                baseSettingView.setOnClickListener {
                    onRandomSettingEventListener?.startManagerPage()
                }
            }
        }
    }

    private var onRandomSettingEventListener: OnRandomSettingEventListener? = null

    fun setOnRandomSettingEventListener(onRandomSettingEventListener: OnRandomSettingEventListener) {
        this.onRandomSettingEventListener = onRandomSettingEventListener
    }

    interface OnRandomSettingEventListener {
        fun startManagerPage()
        fun deleteAllDataClick()
    }
}