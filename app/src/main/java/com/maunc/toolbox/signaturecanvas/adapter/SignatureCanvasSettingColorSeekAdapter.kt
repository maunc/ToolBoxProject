package com.maunc.toolbox.signaturecanvas.adapter

import android.annotation.SuppressLint
import android.widget.SeekBar
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.addSeekBarListener
import com.maunc.toolbox.commonbase.ext.obtainColorStateList
import com.maunc.toolbox.commonbase.utils.canvasPenColorA
import com.maunc.toolbox.commonbase.utils.canvasPenColorB
import com.maunc.toolbox.commonbase.utils.canvasPenColorG
import com.maunc.toolbox.commonbase.utils.canvasPenColorR
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.databinding.ItemCanvasSettingPenColorSeekBinding
import com.maunc.toolbox.signaturecanvas.constant.RGB_SEEK_MAX_VALUE
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingColorData
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingColorData.Companion.A_COLOR_TYPE
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingColorData.Companion.B_COLOR_TYPE
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingColorData.Companion.G_COLOR_TYPE
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingColorData.Companion.R_COLOR_TYPE

class SignatureCanvasSettingColorSeekAdapter :
    BaseQuickAdapter<CanvasSettingColorData, BaseDataBindingHolder<ItemCanvasSettingPenColorSeekBinding>>(
        R.layout.item_canvas_setting_pen_color_seek
    ) {

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemCanvasSettingPenColorSeekBinding>,
        item: CanvasSettingColorData,
    ) {
        holder.dataBinding?.let { databind ->
            databind.itemCanvasSettingColorTitle.text = item.title
            databind.itemCanvasSettingColorSeek.apply {
                max = RGB_SEEK_MAX_VALUE
                thumbTintList = obtainColorStateList(item.progressTint)
                progressTintList = obtainColorStateList(item.progressTint)
                progressBackgroundTintList = obtainColorStateList(item.progressBackgroundTint)
                addSeekBarListener(
                    onProgressChanged = { _, progress, _ ->
                        databind.itemCanvasSettingColorValue.text = "$progress"
                        colorSeekListener?.onProgressChange(item.colorType, progress)
                    }, onStopTrackingTouch = { seekBar ->
                        seekBar?.progress?.let { value ->
                            when (item.colorType) {
                                A_COLOR_TYPE -> obtainMMKV.putInt(canvasPenColorA, value)
                                R_COLOR_TYPE -> obtainMMKV.putInt(canvasPenColorR, value)
                                G_COLOR_TYPE -> obtainMMKV.putInt(canvasPenColorG, value)
                                B_COLOR_TYPE -> obtainMMKV.putInt(canvasPenColorB, value)
                            }
                        }
                    }
                )
            }
            databind.itemCanvasSettingColorValue.text = "${item.seekValue}"
            showSeekProgress(item, databind.itemCanvasSettingColorSeek)
        }
    }

    private fun showSeekProgress(item: CanvasSettingColorData, seekBar: SeekBar) {
        when (item.colorType) {
            A_COLOR_TYPE -> seekBar.progress = obtainMMKV.getInt(canvasPenColorA)
            R_COLOR_TYPE -> seekBar.progress = obtainMMKV.getInt(canvasPenColorR)
            G_COLOR_TYPE -> seekBar.progress = obtainMMKV.getInt(canvasPenColorG)
            B_COLOR_TYPE -> seekBar.progress = obtainMMKV.getInt(canvasPenColorB)
        }
    }

    private var colorSeekListener: CanvasSettingColorSeekListener? = null

    fun setColorSeekListener(colorSeekListener: CanvasSettingColorSeekListener) {
        this.colorSeekListener = colorSeekListener
    }

    fun interface CanvasSettingColorSeekListener {
        fun onProgressChange(type: Int, progress: Int)
    }
}