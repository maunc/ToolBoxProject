package com.maunc.toolbox.ftp.viewmodel

import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.ftp.data.FtpServerConfigData

class FtpServerViewModel:BaseViewModel<BaseModel>() {
    val ftpServerConfigList = mutableListOf<FtpServerConfigData>().mutableListInsert(
        FtpServerConfigData(
            title = obtainString(R.string.ftp_server_port_text),
            type = FtpServerConfigData.FTP_PORT_TYPE,
            content = obtainString(R.string.ftp_server_default_port_text),
            editHint = obtainString(R.string.ftp_server_port_tips_text)
        ),
        FtpServerConfigData(
            title = obtainString(R.string.ftp_server_use_text),
            type = FtpServerConfigData.FTP_USE_TYPE,
            content = obtainString(R.string.ftp_server_default_use_text),
            editHint = obtainString(R.string.ftp_server_use_tips_text)
        ),
        FtpServerConfigData(
            title = obtainString(R.string.ftp_server_pass_word_text),
            type = FtpServerConfigData.FTP_PASS_WORD_TYPE,
            content = obtainString(R.string.ftp_server_default_pass_word_text),
            editHint = obtainString(R.string.ftp_server_pass_word_tips_text)
        ),
        FtpServerConfigData(
            title = obtainString(R.string.ftp_server_share_dir_text),
            type = FtpServerConfigData.FTP_SHARE_DIR,
            content = obtainString(R.string.ftp_server_default_share_dir_text),
            editHint = obtainString(R.string.ftp_server_share_dir_tips_text)
        ),
    )
}