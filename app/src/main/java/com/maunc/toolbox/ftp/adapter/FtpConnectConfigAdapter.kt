package com.maunc.toolbox.ftp.adapter

import android.annotation.SuppressLint
import android.text.InputType
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.base.ext.addEditTextListener
import com.maunc.base.ext.chineseProhibitedInput
import com.maunc.base.ext.setMaxLength
import com.maunc.base.ext.spaceProhibitedInput
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemFtpConfigBinding
import com.maunc.toolbox.ftp.data.FtpConnectConfigData

class FtpConnectConfigAdapter :
    BaseQuickAdapter<FtpConnectConfigData, BaseDataBindingHolder<ItemFtpConfigBinding>>(
        R.layout.item_ftp_config
    ) {

    // 避免实际内容被刷掉
    private var isSetEditViewText = false

    // 当前点击的EditView的位置
    private var hasFocusEditViewPosition = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun convert(
        holder: BaseDataBindingHolder<ItemFtpConfigBinding>,
        item: FtpConnectConfigData,
    ) {
        val itemPosition = holder.bindingAdapterPosition
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemFtpConfigTitle.text = item.title
            mDataBind.itemFtpConfigEditView.setHint(item.editHint)
            // 解决赋值错乱的问题
            isSetEditViewText = true
            mDataBind.itemFtpConfigEditView.setText(item.content)
            isSetEditViewText = false
            mDataBind.itemFtpConfigEditView.spaceProhibitedInput()
            mDataBind.itemFtpConfigEditView.chineseProhibitedInput()
            when (item.type) {
                FtpConnectConfigData.FTP_PORT_TYPE -> {
                    mDataBind.itemFtpConfigEditView.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                    mDataBind.itemFtpConfigEditView.setMaxLength(5)
                }

                FtpConnectConfigData.FTP_HOST_TYPE ->
                    mDataBind.itemFtpConfigEditView.setMaxLength(9999)

                FtpConnectConfigData.FTP_USE_TYPE, FtpConnectConfigData.FTP_PASS_WORD_TYPE ->
                    mDataBind.itemFtpConfigEditView.setMaxLength(20)
            }
            mDataBind.itemFtpConfigEditView.setOnTouchListener { view, event ->
                return@setOnTouchListener false
            }
            mDataBind.itemFtpConfigEditView.setOnFocusChangeListener { v, hasFocus ->
                if (v.isClickable || v.isPressed) {
                    hasFocusEditViewPosition = itemPosition
                }
            }
            mDataBind.itemFtpConfigEditView.addEditTextListener(
                afterTextChanged = { editContent ->
                    if (isSetEditViewText || itemPosition == RecyclerView.NO_POSITION || holder.bindingAdapterPosition >= data.size) {
                        return@addEditTextListener
                    }
                    if (hasFocusEditViewPosition == -1 || itemPosition != hasFocusEditViewPosition) {
                        return@addEditTextListener
                    }
                    data[itemPosition].let { currentItem ->
                        if (currentItem.content != editContent) {
                            currentItem.content = editContent
                        }
                    }
                }
            )
        }
    }
}