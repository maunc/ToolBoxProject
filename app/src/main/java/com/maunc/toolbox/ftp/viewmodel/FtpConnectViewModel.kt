package com.maunc.toolbox.ftp.viewmodel

import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.ftp.data.FtpConnectConfigData
import com.maunc.toolbox.ftp.data.FtpServerConfigData

class FtpConnectViewModel : BaseViewModel<BaseModel>() {
    val ftpConnectConfigList = mutableListOf<FtpConnectConfigData>().mutableListInsert(
        FtpConnectConfigData(
            title = obtainString(R.string.ftp_server_host_text),
            type = FtpServerConfigData.FTP_SHARE_DIR,
            content = "10.118.57.180",
            editHint = obtainString(R.string.ftp_server_host_tips_text)
        ),
        FtpConnectConfigData(
            title = obtainString(R.string.ftp_server_port_text),
            type = FtpServerConfigData.FTP_PORT_TYPE,
            content = obtainString(R.string.ftp_server_default_port_text),
            editHint = obtainString(R.string.ftp_server_port_tips_text)
        ),
        FtpConnectConfigData(
            title = obtainString(R.string.ftp_server_use_text),
            type = FtpServerConfigData.FTP_USE_TYPE,
            content = obtainString(R.string.ftp_server_default_use_text),
            editHint = obtainString(R.string.ftp_server_use_tips_text)
        ),
        FtpConnectConfigData(
            title = obtainString(R.string.ftp_server_pass_word_text),
            type = FtpServerConfigData.FTP_PASS_WORD_TYPE,
            content = obtainString(R.string.ftp_server_default_pass_word_text),
            editHint = obtainString(R.string.ftp_server_pass_word_tips_text)
        ),
    )
}