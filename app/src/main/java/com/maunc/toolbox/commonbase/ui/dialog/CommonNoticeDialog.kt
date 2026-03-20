package com.maunc.toolbox.commonbase.ui.dialog

import android.os.Bundle
import com.maunc.base.ext.marquee
import com.maunc.base.ext.obtainScreenHeight
import com.maunc.base.ext.obtainScreenWidth
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseDialog
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.viewmodel.CommonNoticeDialogViewModel
import com.maunc.toolbox.databinding.DialogCommonNoticeBinding

class CommonNoticeDialog : BaseDialog<CommonNoticeDialogViewModel, DialogCommonNoticeBinding>() {

    private var titleText: String = obtainString(R.string.functions_are_under_development)
    private var contentText: String = obtainString(R.string.functions_are_under_development)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_Transparent)
    }

    override fun initView(savedInstanceState: Bundle?) {
        val layoutParams = mDatabind.commonNoticeLayout.layoutParams
        layoutParams.width = obtainScreenWidth() / 10 * 8
        layoutParams.height = obtainScreenHeight() / 2
        mDatabind.commonNoticeLayout.layoutParams = layoutParams
        mDatabind.commonNoticeTitleTv.text = titleText
        mDatabind.commonNoticeTitleTv.marquee()
        mDatabind.commonNoticeContentTv.text = contentText
        mDatabind.commonNoticeRootLayout.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {

    }

    fun setTitle(title: String): CommonNoticeDialog {
        this.titleText = title
        return this
    }

    fun setContentText(content: String): CommonNoticeDialog {
        this.contentText = content
        return this
    }
}