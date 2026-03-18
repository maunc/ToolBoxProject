package com.maunc.toolbox.ftp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.obtainString

class FtpServerViewModel : BaseViewModel<BaseModel>() {
    var currentPort = MutableLiveData(21210)
    var currentUser = MutableLiveData(obtainString(R.string.ftp_server_default_use_text))
    var currentPassWord = MutableLiveData(obtainString(R.string.ftp_server_default_pass_word_text))
    var currentShareDir = MutableLiveData(obtainString(R.string.ftp_server_default_share_dir_text))
}