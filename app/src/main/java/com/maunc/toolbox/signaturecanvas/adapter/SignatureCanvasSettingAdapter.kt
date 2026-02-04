package com.maunc.toolbox.signaturecanvas.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.addSeekBarListener
import com.maunc.toolbox.commonbase.ext.gone
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainColorToARAG
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.commonbase.ext.visible
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.commonbase.utils.canvasEraserWidth
import com.maunc.toolbox.commonbase.utils.canvasPenColorA
import com.maunc.toolbox.commonbase.utils.canvasPenColorB
import com.maunc.toolbox.commonbase.utils.canvasPenColorG
import com.maunc.toolbox.commonbase.utils.canvasPenColorR
import com.maunc.toolbox.commonbase.utils.canvasPenWidth
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.signaturecanvas.constant.ERASER_WIDTH_MAX_VALUE
import com.maunc.toolbox.signaturecanvas.constant.PEN_WIDTH_MAX_VALUE
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingColorData
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingData
import com.maunc.toolbox.signaturecanvas.ui.activity.SignatureCanvasManagerFileActivity

@SuppressLint("SetTextI18n")
class SignatureCanvasSettingAdapter :
    BaseMultiItemQuickAdapter<CanvasSettingData, BaseViewHolder>() {
    init {
        addItemType(
            CanvasSettingData.CANVAS_PEN_WIDTH_TYPE,
            R.layout.item_canvas_setting_pen_width
        )
        addItemType(
            CanvasSettingData.CANVAS_ERASER_WIDTH_TYPE,
            R.layout.item_canvas_setting_eraser_width
        )
        addItemType(
            CanvasSettingData.CANVAS_PEN_COLOR_TYPE,
            R.layout.item_canvas_setting_pen_color
        )
        addItemType(
            CanvasSettingData.CANVAS_FILE_MANAGE,
            R.layout.item_canvas_setting_manager_file
        )
    }

    /**
     * 临时记录颜色值
     */
    private var aValue = 0
    private var rValue = 0
    private var gValue = 0
    private var bValue = 0

    //临时记录的画笔宽度值
    private var temporaryPenWidthValue = 0

    //临时记录的橡皮大小值
    private var temporaryEraserWidthValue = 0

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
                val storagePenWidth = obtainMMKV.getInt(canvasPenWidth)
                temporaryPenWidthValue = storagePenWidth
                val penWidthTv =
                    haveView.findViewById<TextView>(R.id.item_canvas_setting_pen_width_tv)
                penWidthTv.text = "${
                    obtainString(R.string.signature_canvas_setting_current_pen_width_text)
                }:$storagePenWidth"
                val settingPenWidthTips =
                    haveView.findViewById<TextView>(R.id.item_canvas_setting_pen_width_tips_tv)
                val penWidthSeek =
                    haveView.findViewById<SeekBar>(R.id.item_canvas_setting_pen_width_seek)
                penWidthSeek.apply {
                    max = PEN_WIDTH_MAX_VALUE
                    progress = storagePenWidth
                    addSeekBarListener(onProgressChanged = { seekBar, progress, fromUser ->
                        penWidthTv.text = "${
                            obtainString(R.string.signature_canvas_setting_current_pen_width_text)
                        }:$progress"
                    }, onStartTrackingTouch = { seekBar ->
                        seekBar?.let {
                            temporaryPenWidthValue = seekBar.progress
                        }
                    }, onStopTrackingTouch = { seekBar ->
                        seekBar?.let {
                            if (seekBar.progress <= 0) {
                                seekBar.progress = temporaryPenWidthValue
                                settingPenWidthTips.apply {
                                    visible()
                                    text = obtainString(
                                        R.string.signature_canvas_setting_pen_width_error
                                    )
                                }
                            } else {
                                settingPenWidthTips.gone()
                                obtainMMKV.putInt(canvasPenWidth, progress)
                            }
                        }
                    })
                }
            }

            CanvasSettingData.CANVAS_ERASER_WIDTH_TYPE -> {
                val storageEraserWidth = obtainMMKV.getInt(canvasEraserWidth)
                temporaryEraserWidthValue = storageEraserWidth
                val eraserWidthTv =
                    haveView.findViewById<TextView>(R.id.item_canvas_setting_eraser_width_tv)
                eraserWidthTv.text = "${
                    obtainString(R.string.signature_canvas_setting_current_eraser_width_text)
                }:$storageEraserWidth"
                val settingEraserWidthTips =
                    haveView.findViewById<TextView>(R.id.item_canvas_setting_eraser_width_tips_tv)
                val eraserWidthSeek =
                    haveView.findViewById<SeekBar>(R.id.item_canvas_setting_eraser_width_seek)
                eraserWidthSeek.apply {
                    max = ERASER_WIDTH_MAX_VALUE
                    progress = storageEraserWidth
                    addSeekBarListener(onProgressChanged = { seekBar, progress, fromUser ->
                        eraserWidthTv.text = "${
                            obtainString(R.string.signature_canvas_setting_current_eraser_width_text)
                        }:$progress"
                    }, onStartTrackingTouch = { seekBar ->
                        seekBar?.let {
                            temporaryEraserWidthValue = seekBar.progress
                        }
                    }, onStopTrackingTouch = { seekBar ->
                        seekBar?.let {
                            if (seekBar.progress <= 0) {
                                seekBar.progress = temporaryEraserWidthValue
                                settingEraserWidthTips.apply {
                                    visible()
                                    text = obtainString(
                                        R.string.signature_canvas_setting_eraser_width_error
                                    )
                                }
                            } else {
                                settingEraserWidthTips.gone()
                                obtainMMKV.putInt(canvasEraserWidth, progress)
                            }
                        }
                    })
                }
            }

            CanvasSettingData.CANVAS_PEN_COLOR_TYPE -> {
                val colorSeekRecycler =
                    haveView.findViewById<RecyclerView>(R.id.item_canvas_setting_color_seek_recycler)
                val colorTips = haveView.findViewById<View>(R.id.signature_canvas_setting_color)
                val colorTextTips =
                    haveView.findViewById<TextView>(R.id.item_canvas_setting_color_text)
                colorSeekRecycler.layoutManager = context.linearLayoutManager()
                rValue = obtainMMKV.getInt(canvasPenColorR)
                gValue = obtainMMKV.getInt(canvasPenColorG)
                bValue = obtainMMKV.getInt(canvasPenColorB)
                aValue = obtainMMKV.getInt(canvasPenColorA)
                colorSeekRecycler.adapter = SignatureCanvasSettingColorSeekAdapter().apply {
                    setList(mutableListOf<CanvasSettingColorData>().apply {
                        mutableListInsert(
                            CanvasSettingColorData(
                                colorType = CanvasSettingColorData.R_COLOR_TYPE,
                                title = obtainString(R.string.canvas_setting_color_r_text),
                                seekValue = rValue,
                                progressTint = R.color.red,
                                progressBackgroundTint = R.color.red_seek,
                            ), CanvasSettingColorData(
                                colorType = CanvasSettingColorData.G_COLOR_TYPE,
                                title = obtainString(R.string.canvas_setting_color_g_text),
                                seekValue = gValue,
                                progressTint = R.color.green,
                                progressBackgroundTint = R.color.green_seek,
                            ), CanvasSettingColorData(
                                colorType = CanvasSettingColorData.B_COLOR_TYPE,
                                title = obtainString(R.string.canvas_setting_color_b_text),
                                seekValue = bValue,
                                progressTint = R.color.blue,
                                progressBackgroundTint = R.color.blue_seek,
                            ), CanvasSettingColorData(
                                colorType = CanvasSettingColorData.A_COLOR_TYPE,
                                title = obtainString(R.string.canvas_setting_color_a_text),
                                seekValue = aValue,
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

            CanvasSettingData.CANVAS_FILE_MANAGE -> {
                haveView.findViewById<RelativeLayout>(R.id.item_canvas_setting_tab).setOnClickListener {
                    context.startTargetActivity(SignatureCanvasManagerFileActivity::class.java)
                }
            }
        }
    }

    fun getAValue() = aValue
    fun getRValue() = rValue
    fun getGValue() = gValue
    fun getBValue() = bValue
    fun getTemporaryPenWidthValue() = temporaryPenWidthValue
    fun getTemporaryEraserWidthValue() = temporaryEraserWidthValue

    private fun colorARGBtoString(colorInt: Int) =
        "#${colorInt.toUInt().toString(16).uppercase()}"
}