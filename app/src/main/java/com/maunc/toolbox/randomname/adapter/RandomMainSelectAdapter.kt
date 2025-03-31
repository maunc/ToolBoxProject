package com.maunc.toolbox.randomname.adapter

import android.widget.TextView
import com.maunc.toolbox.R
import com.maunc.toolbox.randomname.database.table.RandomNameData
import com.us.mauncview.FlowLayoutAdapter

class RandomMainSelectAdapter : FlowLayoutAdapter<RandomNameData>() {
    override fun bindDataToView(holder: ViewHolder?, position: Int, bean: RandomNameData) {
        holder?.getView<TextView>(R.id.item_random_main_select_name)?.text = bean.randomName
    }

    override fun bindItemLayoutId(position: Int, bean: RandomNameData): Int =
        R.layout.item_random_main_select
}

