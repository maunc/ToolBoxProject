package com.maunc.toolbox

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class ToolBoxGlideModule : AppGlideModule() {
    companion object {
        private const val TAG = "ToolBoxGlideModule"
        const val MEMORY_CACHE_SIZE = 10 * 1024 * 1024L

        const val DISK_CACHE_SIZE = 20 * 1024 * 1024L
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        val diskCacheDir = "${context.cacheDir}/image_cache"
        Log.e(TAG,"applyOptions diskCacheDir:$diskCacheDir")
        builder.setMemoryCache(LruResourceCache(MEMORY_CACHE_SIZE))
        builder.setSourceExecutor(
            GlideExecutor.newSourceBuilder().setThreadCount(4).setThreadTimeoutMillis(5000).build()
        )
        builder.setDiskCache(DiskLruCacheFactory(diskCacheDir, DISK_CACHE_SIZE))
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
    }

    override fun isManifestParsingEnabled(): Boolean {
        Log.e(TAG,"isManifestParsingEnabled")
        return false
    }
}