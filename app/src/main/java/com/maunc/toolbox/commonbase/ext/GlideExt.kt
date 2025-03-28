package com.maunc.toolbox.commonbase.ext

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.utils.ActivityCompatHelper
import com.maunc.toolbox.ToolBoxApplication
import java.io.File

fun ImageView.loadImage(uri: Uri) =
    Glide.with(ToolBoxApplication.app).load(uri).into(this)

fun ImageView.loadImage(file: File) =
    Glide.with(ToolBoxApplication.app).load(file).into(this)

fun ImageView.loadImage(@RawRes @DrawableRes resId: Int) =
    Glide.with(ToolBoxApplication.app).load(resId).into(this)

fun ImageView.loadImage(string: String) =
    Glide.with(ToolBoxApplication.app).load(string).into(this)

fun Activity.loadImage(imageView: ImageView, uri: Uri) =
    Glide.with(this).load(uri).into(imageView)

fun Activity.loadImage(imageView: ImageView, file: File) =
    Glide.with(this).load(file).into(imageView)

fun Activity.loadImage(imageView: ImageView, @RawRes @DrawableRes resId: Int) =
    Glide.with(this).load(resId).into(imageView)

fun Activity.loadImage(imageView: ImageView, string: String) =
    Glide.with(this).load(string).into(imageView)

fun Fragment.loadImage(imageView: ImageView, uri: Uri) =
    Glide.with(this).load(uri).into(imageView)

fun Fragment.loadImage(imageView: ImageView, file: File) =
    Glide.with(this).load(file).into(imageView)

fun Fragment.loadImage(imageView: ImageView, @RawRes @DrawableRes resId: Int) =
    Glide.with(this).load(resId).into(imageView)

fun Fragment.loadImage(imageView: ImageView, string: String) =
    Glide.with(this).load(string).into(imageView)

fun ImageView.loadImageCircleCrop(uri: Uri) =
    Glide.with(ToolBoxApplication.app).load(uri).circleCrop().into(this)

fun ImageView.loadImageCircleCrop(file: File) =
    Glide.with(ToolBoxApplication.app).load(file).circleCrop().into(this)

fun ImageView.loadImageCircleCrop(@RawRes @DrawableRes resId: Int) =
    Glide.with(ToolBoxApplication.app).load(resId).circleCrop().into(this)

fun ImageView.loadImageCircleCrop(string: String) =
    Glide.with(ToolBoxApplication.app).load(string).circleCrop().into(this)


val obtainGlideEngin: GlideEngin by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    GlideEngin()
}

class GlideEngin : ImageEngine {
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context).load(url).into(imageView)
    }

    override fun loadImage(
        context: Context,
        imageView: ImageView,
        url: String,
        maxWidth: Int,
        maxHeight: Int,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context).load(url).override(maxWidth, maxHeight).into(imageView)
    }

    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context).asBitmap().load(url)
            .override(200, 200).sizeMultiplier(0.5f)
            .transform(CenterCrop(), RoundedCorners(10))
            .into(imageView)
    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context).load(url)
            .override(200, 200).centerCrop()
            .into(imageView)
    }

    override fun pauseRequests(context: Context) {
        Glide.with(context).pauseRequests()
    }

    override fun resumeRequests(context: Context) {
        Glide.with(context).resumeRequests()
    }
}