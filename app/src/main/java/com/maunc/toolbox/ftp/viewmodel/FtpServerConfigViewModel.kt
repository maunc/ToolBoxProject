package com.maunc.toolbox.ftp.viewmodel

import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.updateLayoutParams
import com.maunc.base.ext.mutableListInsert
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.constant.ONE_DELAY_MILLIS
import com.maunc.toolbox.ftp.data.FtpServerConfigData

class FtpServerConfigViewModel : BaseViewModel<BaseModel>() {

    fun obtainFtpServerConfigList(
        port: Int,
        user: String,
        passWord: String,
        shareDir: String,
    ) = mutableListOf<FtpServerConfigData>().mutableListInsert(
        FtpServerConfigData(
            title = obtainString(R.string.ftp_server_port_text),
            type = FtpServerConfigData.FTP_PORT_TYPE,
            content = port.toString(),
            editHint = obtainString(R.string.ftp_server_port_tips_text)
        ),
        FtpServerConfigData(
            title = obtainString(R.string.ftp_server_use_text),
            type = FtpServerConfigData.FTP_USE_TYPE,
            content = user,
            editHint = obtainString(R.string.ftp_server_use_tips_text)
        ),
        FtpServerConfigData(
            title = obtainString(R.string.ftp_server_pass_word_text),
            type = FtpServerConfigData.FTP_PASS_WORD_TYPE,
            content = passWord,
            editHint = obtainString(R.string.ftp_server_pass_word_tips_text)
        ),
        FtpServerConfigData(
            title = obtainString(R.string.ftp_server_share_dir_text),
            type = FtpServerConfigData.FTP_SHARE_DIR,
            content = shareDir,
            editHint = obtainString(R.string.ftp_server_share_dir_tips_text)
        ),
    )

    fun updateNewGroupLayout(keyBoardHeight: Int, newGroup: ViewGroup) {
        newGroup.postDelayed({
            newGroup.updateLayoutParams<RelativeLayout.LayoutParams> {
                bottomMargin = keyBoardHeight
            }
        }, ONE_DELAY_MILLIS)
    }
}