package com.maunc.toolbox.ftp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.toolbox.R

class FtpServerViewModel : BaseViewModel<BaseModel>() {
    var currentPort = MutableLiveData(21210)
    var currentUser = MutableLiveData(obtainString(R.string.ftp_server_default_use_text))
    var currentPassWord = MutableLiveData(obtainString(R.string.ftp_server_default_pass_word_text))
    var currentShareDir = MutableLiveData(obtainString(R.string.ftp_server_default_share_dir_text))
}