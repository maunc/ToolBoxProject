package com.maunc.videoplayer

interface CustomVideoLifecycle {

    fun onPause()

    fun onResume()

    fun onDestroy()
}