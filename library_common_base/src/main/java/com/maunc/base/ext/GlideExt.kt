package com.maunc.base.ext

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.maunc.base.BaseApp
import java.io.File

fun ImageView.loadImage(uri: Uri) =
    Glide.with(BaseApp.app).load(uri).into(this)

fun ImageView.loadImage(file: File) =
    Glide.with(BaseApp.app).load(file).into(this)

fun ImageView.loadImage(@RawRes @DrawableRes resId: Int) =
    Glide.with(BaseApp.app).load(resId).into(this)

fun ImageView.loadImage(string: String) =
    Glide.with(BaseApp.app).load(string).into(this)

fun ImageView.loadImage(bitmap: Bitmap) =
    Glide.with(BaseApp.app).load(bitmap).into(this)

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
    Glide.with(BaseApp.app).load(uri).circleCrop().into(this)

fun ImageView.loadImageCircleCrop(file: File) =
    Glide.with(BaseApp.app).load(file).circleCrop().into(this)

fun ImageView.loadImageCircleCrop(@RawRes @DrawableRes resId: Int) =
    Glide.with(BaseApp.app).load(resId).circleCrop().into(this)

fun ImageView.loadImageCircleCrop(string: String) =
    Glide.with(BaseApp.app).load(string).circleCrop().into(this)