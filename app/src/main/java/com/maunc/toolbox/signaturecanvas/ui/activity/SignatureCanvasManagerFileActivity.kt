package com.maunc.toolbox.signaturecanvas.ui.activity

import android.os.Bundle
import android.util.Log
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.gridLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
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
            Log.e("ww", "saveFileListSize:${it.size}")
            mDatabind.signatureCanvasSaveFileSmart.finishRefresh()
            if(it.isNotEmpty()) {
                signatureCanvasSaveFileAdapter.setList(it)
            }
            mViewModel.isSaveFileData.value = it.isNotEmpty()
        }
    }
}