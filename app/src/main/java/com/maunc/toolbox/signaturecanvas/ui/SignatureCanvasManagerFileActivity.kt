package com.maunc.toolbox.signaturecanvas.ui

import android.os.Bundle
import com.maunc.base.ext.clickScale
import com.maunc.base.ext.finishCurrentActivity
import com.maunc.base.ext.gridLayoutManager
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseActivity
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ActivitySignatureCanvasManagerFileBinding
import com.maunc.toolbox.signaturecanvas.adapter.SignatureCanvasSaveFileAdapter
import com.maunc.toolbox.signaturecanvas.viewmodel.SignatureCanvasManagerFileViewModel

class SignatureCanvasManagerFileActivity :
    BaseActivity<SignatureCanvasManagerFileViewModel, ActivitySignatureCanvasManagerFileBinding>() {

    private val signatureCanvasSaveFileAdapter by lazy {
        SignatureCanvasSaveFileAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.signatureCanvasManagerFileViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.signature_canvas_setting_manager_file_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.signatureCanvasSaveFileRec.layoutManager = gridLayoutManager(2)
        mDatabind.signatureCanvasSaveFileRec.adapter = signatureCanvasSaveFileAdapter
        mDatabind.signatureCanvasSaveFileSmart.setOnRefreshListener {
            mViewModel.initSaveFile()
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.initSaveFile()
    }

    override fun createObserver() {
        mViewModel.saveFileList.observe(this) {
            mDatabind.signatureCanvasSaveFileSmart.finishRefresh()
            mViewModel.isSaveFileData.value = it.isNotEmpty()
            if (it.isNotEmpty()) {
                signatureCanvasSaveFileAdapter.setList(it)
            }
        }
    }
}