package com.maunc.toolbox.signaturecanvas.adapter

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainColorToARAG
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.commonbase.utils.canvasPenColorA
import com.maunc.toolbox.commonbase.utils.canvasPenColorB
import com.maunc.toolbox.commonbase.utils.canvasPenColorG
import com.maunc.toolbox.commonbase.utils.canvasPenColorR
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingColorData
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingData

class SignatureCanvasSettingAdapter :
    BaseMultiItemQuickAdapter<CanvasSettingData, BaseViewHolder>() {
    init {
        addItemType(
            CanvasSettingData.CANVAS_PEN_WIDTH_TYPE,
            R.layout.item_canvas_setting_pen_width
        )
        addItemType(
            CanvasSettingData.CANVAS_PEN_COLOR_TYPE,
            R.layout.item_canvas_setting_pen_color
        )
    }

    /**
     * 临时记录颜色值
     */
    private var aValue = 0
    private var rValue = 0
    private var gValue = 0
    private var bValue = 0

    override fun convert(holder: BaseViewHolder, item: CanvasSettingData) {
        val itemPosition = getItemPosition(item)
        val haveView = holder.itemView
        haveView.findViewById<TextView>(R.id.item_canvas_setting_type_tv).apply {
            text = item.title
        }
        val moreLayout = haveView.findViewById<RelativeLayout>(R.id.item_canvas_setting_more_layout)
        val expandIv = haveView.findViewById<ImageView>(R.id.item_canvas_setting_expand_iv)
        moreLayout.visibleOrGone(item.isExpand)
        expandIv.setImageResource(
            if (item.isExpand) R.drawable.icon_group_expand_yes else R.drawable.icon_group_expand_no
        )
        haveView.findViewById<RelativeLayout>(R.id.item_canvas_setting_tab).setOnClickListener {
            val settingData = data[itemPosition]
            settingData.isExpand = !settingData.isExpand
            notifyItemChanged(itemPosition)
        }
        when (item.itemType) {
            CanvasSettingData.CANVAS_PEN_WIDTH_TYPE -> {

            }

            CanvasSettingData.CANVAS_PEN_COLOR_TYPE -> {
                val colorSeekRecycler =
                    haveView.findViewById<RecyclerView>(R.id.item_canvas_setting_color_seek_recycler)
                val colorTips = haveView.findViewById<View>(R.id.signature_canvas_setting_color)
                val colorTextTips =
                    haveView.findViewById<TextView>(R.id.item_canvas_setting_color_text)
                colorSeekRecycler.layoutManager = context.linearLayoutManager()
                colorSeekRecycler.adapter = SignatureCanvasSettingColorSeekAdapter().apply {
                    setList(mutableListOf<CanvasSettingColorData>().apply {
                        mutableListInsert(
                            CanvasSettingColorData(
                                colorType = CanvasSettingColorData.R_COLOR_TYPE,
                                title = obtainString(R.string.canvas_setting_color_r_text),
                                seekValue = obtainMMKV.getInt(canvasPenColorR),
                                progressTint = R.color.red,
                                progressBackgroundTint = R.color.red_seek,
                            ), CanvasSettingColorData(
                                colorType = CanvasSettingColorData.G_COLOR_TYPE,
                                title = obtainString(R.string.canvas_setting_color_g_text),
                                seekValue = obtainMMKV.getInt(canvasPenColorG),
                                progressTint = R.color.green,
                                progressBackgroundTint = R.color.green_seek,
                            ), CanvasSettingColorData(
                                colorType = CanvasSettingColorData.B_COLOR_TYPE,
                                title = obtainString(R.string.canvas_setting_color_b_text),
                                seekValue = obtainMMKV.getInt(canvasPenColorB),
                                progressTint = R.color.blue,
                                progressBackgroundTint = R.color.blue_seek,
                            ), CanvasSettingColorData(
                                colorType = CanvasSettingColorData.A_COLOR_TYPE,
                                title = obtainString(R.string.canvas_setting_color_a_text),
                                seekValue = obtainMMKV.getInt(canvasPenColorA),
                                progressTint = R.color.black,
                                progressBackgroundTint = R.color.alpha_seek,
                            )
                        )
                        setColorSeekListener { type, progress ->
                            when (type) {
                                CanvasSettingColorData.A_COLOR_TYPE -> aValue = progress
                                CanvasSettingColorData.R_COLOR_TYPE -> rValue = progress
                                CanvasSettingColorData.G_COLOR_TYPE -> gValue = progress
                                CanvasSettingColorData.B_COLOR_TYPE -> bValue = progress
                            }
                            val colorARGB = obtainColorToARAG(aValue, rValue, gValue, bValue)
                            colorTips.setBackgroundColor(colorARGB)
                            colorTextTips.text = colorARGBtoString(colorARGB)
                        }
                    })
                }
            }
        }
    }

    private fun colorARGBtoString(
        colorInt: Int,
    ) = "#${colorInt.toUInt().toString(16).uppercase()}"
}