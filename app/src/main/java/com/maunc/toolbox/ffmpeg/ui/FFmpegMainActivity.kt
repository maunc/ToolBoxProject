package com.maunc.toolbox.ffmpeg.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityFfmpegMainBinding
import com.maunc.toolbox.ffmpeg.viewmodel.FFmpegMainViewModel

class FFmpegMainActivity : BaseActivity<FFmpegMainViewModel, ActivityFfmpegMainBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.tool_box_item_ffmpeg_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
//        val ffmpegCommand =
//            "-i ${"/storage/emulated/0/DCIM/Camera/VID_20260223_234743.mp4"}" +
//                    " -vn -acodec libmp3lame -b:a 192k ${"/storage/emulated/0/DCIM/Camera/666哈哈哈.mp3"}"
//        FFmpegKit.executeAsync(ffmpegCommand,
//            { session: FFmpegSession ->
//                // 转换完成回调
//                runOnUiThread {
//                    val returnCode = session.returnCode
//                    if (ReturnCode.isSuccess(returnCode)) {
//                        Log.d("ww", "转换成功")
//                    } else if (ReturnCode.isCancel(returnCode)) {
//                        Log.e("ww", "转换已取消  ,${session.logs}")
//                    } else {
//                        Log.e("ww", "转换失败  ,${session.logs}")
//                    }
//                }
//            },
//            { log ->
//                // 实时日志回调（可用于解析进度）
//                Log.d("ww", "FFmpeg日志：${log.message}")
//            },
//            { statistics ->
//                // 统计信息回调（如帧率、比特率等）
//                Log.d("ww", "统计信息：$statistics")
//            }
//        )
    }

    override fun createObserver() {

    }

}