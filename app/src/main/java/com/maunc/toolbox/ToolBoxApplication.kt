package com.maunc.toolbox

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.maunc.toolbox.commonbase.ext.loge
import com.tencent.mmkv.MMKV

val appViewModel: ToolBoxApplicationViewModel by lazy {
    ToolBoxApplication.applicationViewModel
}

class ToolBoxApplication(
    override val viewModelStore: ViewModelStore = ViewModelStore(),
) : Application(), ViewModelStoreOwner {

    private var mViewModelFactory: ViewModelProvider.Factory? = null

    companion object {
        lateinit var app: Application
        lateinit var applicationViewModel: ToolBoxApplicationViewModel
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        val mmkvRootDir = MMKV.initialize(this)
        mmkvRootDir.loge("RandomNameMMKVRootDir")
        applicationViewModel = ViewModelProvider(
            this, getViewModelFactory()
        )[ToolBoxApplicationViewModel::class]
    }

    private fun getViewModelFactory(): ViewModelProvider.Factory {
        if (mViewModelFactory == null) {
            mViewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return mViewModelFactory as ViewModelProvider.Factory
    }
}