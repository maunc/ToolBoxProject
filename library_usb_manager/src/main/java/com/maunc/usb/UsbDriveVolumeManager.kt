package com.maunc.usb

import android.content.Context
import android.os.storage.StorageManager

class UsbDriveVolumeManager {

    fun initUsbStorage(context: Context) {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    }
}