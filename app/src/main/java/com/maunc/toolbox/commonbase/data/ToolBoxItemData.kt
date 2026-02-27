package com.maunc.toolbox.commonbase.data

import androidx.annotation.DrawableRes
import com.maunc.toolbox.R

data class ToolBoxItemData(
    @DrawableRes
    val itemIcon: Int = R.drawable.ic_launcher,
    val itemTitle: String = "Default",
)
