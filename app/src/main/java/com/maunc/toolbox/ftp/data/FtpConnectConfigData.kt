package com.maunc.toolbox.ftp.data

import java.io.Serializable

data class FtpConnectConfigData(
    val type: Int,
    val title: String,
    var content: String,
    val editHint: String,
) : Serializable {
    companion object {
        const val FTP_PORT_TYPE = 0//端口号
        const val FTP_USE_TYPE = 1//账号
        const val FTP_PASS_WORD_TYPE = 2//密码
        const val FTP_HOST_TYPE = 3//地址
    }
}