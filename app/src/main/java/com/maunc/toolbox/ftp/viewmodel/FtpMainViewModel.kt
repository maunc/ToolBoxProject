package com.maunc.toolbox.ftp.viewmodel

import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.ftp.data.FtpConfigData

class FtpMainViewModel : BaseViewModel<BaseModel>() {

    val ftpConfigList = mutableListOf<FtpConfigData>().mutableListInsert(
        FtpConfigData(
            title = obtainString(R.string.ftp_main_port_text),
            type = FtpConfigData.FTP_PORT_TYPE,
            content = obtainString(R.string.ftp_main_default_port_text),
            editHint = obtainString(R.string.ftp_main_port_tips_text)
        ),
        FtpConfigData(
            title = obtainString(R.string.ftp_main_use_text),
            type = FtpConfigData.FTP_USE_TYPE,
            content = obtainString(R.string.ftp_main_default_use_text),
            editHint = obtainString(R.string.ftp_main_use_tips_text)
        ),
        FtpConfigData(
            title = obtainString(R.string.ftp_main_pass_word_text),
            type = FtpConfigData.FTP_PASS_WORD_TYPE,
            content = obtainString(R.string.ftp_main_default_pass_word_text),
            editHint = obtainString(R.string.ftp_main_pass_word_tips_text)
        ),
        FtpConfigData(
            title = obtainString(R.string.ftp_main_share_dir_text),
            type = FtpConfigData.FTP_SHARE_DIR,
            content = obtainString(R.string.ftp_main_default_share_dir_text),
            editHint = obtainString(R.string.ftp_main_share_dir_tips_text)
        ),
    )


}