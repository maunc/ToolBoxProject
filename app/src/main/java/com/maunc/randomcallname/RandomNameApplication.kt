package com.maunc.randomcallname

import android.app.Application
import com.maunc.randomcallname.ext.loge
import com.tencent.mmkv.MMKV

class RandomNameApplication : Application() {

    companion object {
        lateinit var app: Application
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        val mmkvRootDir = MMKV.initialize(this)
        mmkvRootDir.loge()
    }
}