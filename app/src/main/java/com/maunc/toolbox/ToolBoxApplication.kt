package com.maunc.toolbox

import android.app.Application
import com.maunc.toolbox.commonbase.ext.loge
import com.tencent.mmkv.MMKV

class ToolBoxApplication : Application() {

    companion object {
        lateinit var app: Application
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        val mmkvRootDir = MMKV.initialize(this)
        mmkvRootDir.loge("RandomNameMMKVRootDir")
    }
}