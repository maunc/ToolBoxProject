package com.maunc.toolbox.pushbox.ui.dialog

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseDialog
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.DialogPushBoxPassLevelBinding
import com.maunc.toolbox.pushbox.viewmodel.PushBoxPassLevelViewModel

class PushBoxPassLevelDialog :
    BaseDialog<PushBoxPassLevelViewModel, DialogPushBoxPassLevelBinding>() {

    private var titleText: String = GLOBAL_NONE_STRING
    private var timeText: String = GLOBAL_NONE_STRING
    private var moveNumText: String = GLOBAL_NONE_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_Transparent)
    }

    override fun initView(savedInstanceState: Bundle?) {
        dialog?.setOnKeyListener { dialog, keyCode, event ->
            return@setOnKeyListener keyCode == KeyEvent.KEYCODE_BACK
        }
        refreshDialogText()
        mDatabind.dialogPushBoxPassLevelReStartButton.clickScale {
            onReStartListener?.onReStart()
            dismissAllowingStateLoss()
        }
        mDatabind.dialogPushBoxPassLevelNextButton.clickScale {
            onNextListener?.onNext()
            dismissAllowingStateLoss()
        }
    }

    private fun refreshDialogText() {
        mDatabind.dialogPushBoxPassLevelTitle.text = titleText
        mDatabind.dialogPushBoxPassLevelTime.text = timeText
        mDatabind.dialogPushBoxPassLevelMoveNum.text = moveNumText
    }

    override fun lazyLoadData() {}

    override fun createObserver() {}

    fun setCurrentLevel(levelIndex: Int): PushBoxPassLevelDialog {
        this.titleText =
            String.format(obtainString(R.string.push_box_main_pass_level_text), levelIndex)
        return this
    }

    fun setTime(time: Long): PushBoxPassLevelDialog {
        this.timeText =
            String.format(obtainString(R.string.push_box_main_pass_level_time_text), time)
        return this
    }

    fun setMoveNum(moveNum: Int): PushBoxPassLevelDialog {
        this.moveNumText =
            String.format(obtainString(R.string.push_box_main_pass_level_move_num_text), moveNum)
        return this
    }

    private var onNextListener: OnPushBoxPassDialogOnNextListener? = null
    private var onReStartListener: OnPushBoxPassDialogOnReStartListener? = null

    fun setOnReStartListener(onReStartListener: OnPushBoxPassDialogOnReStartListener): PushBoxPassLevelDialog {
        this.onReStartListener = onReStartListener
        return this
    }

    fun setOnNextListener(onNextListener: OnPushBoxPassDialogOnNextListener): PushBoxPassLevelDialog {
        this.onNextListener = onNextListener
        return this
    }

    fun interface OnPushBoxPassDialogOnReStartListener {
        fun onReStart()
    }

    fun interface OnPushBoxPassDialogOnNextListener {
        fun onNext()
    }
}