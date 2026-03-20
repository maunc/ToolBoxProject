package com.maunc.toolbox

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.maunc.toolbox.commonbase.constant.GLOBAL_TAG
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.utils.AppStatusManager
import com.maunc.torrent.TorrentManager
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.cancellation.CancellationException

val appViewModel: ToolBoxApplicationViewModel by lazy {
    ToolBoxApplication.applicationViewModel
}

class ToolBoxApplication : Application(), ViewModelStoreOwner {

    override val viewModelStore: ViewModelStore = ViewModelStore()

    private var mViewModelFactory: ViewModelProvider.Factory? = null

    companion object {
        lateinit var app: Application
        lateinit var applicationViewModel: ToolBoxApplicationViewModel
        private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            if (throwable is CancellationException) {
                // 协程取消异常，无需处理（正常流程）
                return@CoroutineExceptionHandler
            }
        }
        val mGlobalScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        val mmkvRootDir = MMKV.initialize(this)
        mmkvRootDir.loge("RandomNameMMKVRootDir")
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