package com.maunc.toolbox

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.maunc.base.BaseApp
import com.maunc.toolbox.commonbase.constant.GLOBAL_TAG
import com.maunc.toolbox.commonbase.utils.AppStatusManager
import com.maunc.torrent.TorrentManager
import com.tencent.mmkv.MMKV

val appViewModel: ToolBoxApplicationViewModel by lazy {
    ToolBoxApplication.applicationViewModel
}

class ToolBoxApplication : BaseApp(), ViewModelStoreOwner {

    override val viewModelStore: ViewModelStore = ViewModelStore()

    private var mViewModelFactory: ViewModelProvider.Factory? = null

    companion object {
        lateinit var applicationViewModel: ToolBoxApplicationViewModel
    }

    override fun onCreate() {
        super.onCreate()
        val mmkvRootDir = MMKV.initialize(this)
        Log.e("MMKVRootDir", mmkvRootDir)
        applicationViewModel = ViewModelProvider(
            this, getViewModelFactory()
        )[ToolBoxApplicationViewModel::class]
        AppStatusManager.initAppStatusManager(this)
        try {
            // 提前启动会话便于 DHT；失败时不阻断应用启动
            TorrentManager.init()
        } catch (t: Throwable) {
            Log.e(GLOBAL_TAG, "LibTorrentSession.ensureStarted in Application", t)
        }
    }

    override fun onTerminate() {
        TorrentManager.unInit()
        super.onTerminate()
    }

    private fun getViewModelFactory(): ViewModelProvider.Factory {
        if (mViewModelFactory == null) {
            mViewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return mViewModelFactory as ViewModelProvider.Factory
    }
}