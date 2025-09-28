package com.maunc.toolbox.signaturecanvas.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.gridLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.databinding.ActivitySignatureCanvasMainBinding
import com.maunc.toolbox.signaturecanvas.adapter.SignatureCanvasControllerAdapter
import com.maunc.toolbox.signaturecanvas.constant.MODE_ERASER
import com.maunc.toolbox.signaturecanvas.constant.MODE_PEN
import com.maunc.toolbox.signaturecanvas.viewmodel.SignatureCanvasMainViewModel

class SignatureCanvasMainActivity :
    BaseActivity<SignatureCanvasMainViewModel, ActivitySignatureCanvasMainBinding>() {

    private val signatureCanvasControllerAdapter by lazy {
        SignatureCanvasControllerAdapter().apply {
            setControllerListener(object :
                SignatureCanvasControllerAdapter.SignatureCanvasControllerListener {
                override fun onBackListener() {

                }

                override fun onNextListener() {

                }

                override fun onPenListener() {
                    mViewModel.drawModel.value = MODE_PEN
                }

                override fun onEraserListener() {
                    mViewModel.drawModel.value = MODE_ERASER
                }

                override fun onClearListener() {
                    CommonDialog()
                        .setTitle(obtainString(R.string.signature_canvas_clear_tips))
                        .setSureListener {
                            mDatabind.signatureCanvasView.clear()
                        }.show(supportFragmentManager, "")
                }

                override fun onSaveListener() {

                }
            })
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.signatureCanvasMainViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.signature_canvas_title)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            startTargetActivity(SignatureCanvasSettingActivity::class.java)
        }
        mDatabind.signatureCanvasControllerRecycler.layoutManager = gridLayoutManager(spanCount = 6)
        mDatabind.signatureCanvasControllerRecycler.adapter = signatureCanvasControllerAdapter
        signatureCanvasControllerAdapter.setList(mViewModel.controllerDataList)
    }

    override fun createObserver() {
        mViewModel.drawModel.observe(this) { model ->
            when (model) {
                MODE_PEN -> mDatabind.signatureCanvasView.setPenMode()
                MODE_ERASER -> mDatabind.signatureCanvasView.setEraserMode()
            }
        }
    }
}