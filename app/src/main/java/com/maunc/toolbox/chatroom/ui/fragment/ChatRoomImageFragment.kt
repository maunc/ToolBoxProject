package com.maunc.toolbox.chatroom.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import com.maunc.toolbox.chatroom.constant.FULL_SCREEN_IMAGE_DATA_EXTRA
import com.maunc.toolbox.chatroom.data.ChatImageData
import com.maunc.toolbox.chatroom.data.LoadImageType
import com.maunc.toolbox.chatroom.viewmodel.ChatRoomImageViewModel
import com.maunc.toolbox.commonbase.base.BaseFragment
import com.maunc.toolbox.commonbase.ext.loadImage
import com.maunc.toolbox.databinding.FragmentChatRoomImageBinding
import com.maunc.toolbox.randomname.constant.GROUP_WITH_NAME_EXTRA

@SuppressLint("NewApi")
class ChatRoomImageFragment :
    BaseFragment<ChatRoomImageViewModel, FragmentChatRoomImageBinding>() {

    companion object {
        @JvmStatic
        fun newInstance(chatImageData: ChatImageData): ChatRoomImageFragment {
            val args = Bundle()
            args.putSerializable(FULL_SCREEN_IMAGE_DATA_EXTRA, chatImageData)
            val fragment = ChatRoomImageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.chatRoomImageViewModel = mViewModel
        obtainImageData()?.let {
            when (it.loadImageType) {
                LoadImageType.FILE -> it.filePath?.let { filePath ->
                    mDatabind.showImageZoomImageView.loadImage(filePath)
                }

                LoadImageType.URL -> it.url?.let { url ->
                    mDatabind.showImageZoomImageView.loadImage(url)
                }

                LoadImageType.RES -> it.imageRes?.let { res ->
                    mDatabind.showImageZoomImageView.loadImage(res)
                }
            }
        }
    }

    private fun obtainImageData(): ChatImageData? {
        val chatImageData: ChatImageData? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(
                    FULL_SCREEN_IMAGE_DATA_EXTRA,
                    ChatImageData::class.java
                )
            } else {
                arguments?.getSerializable(
                    FULL_SCREEN_IMAGE_DATA_EXTRA
                ) as ChatImageData
            }
        return chatImageData
    }

    override fun lazyLoadData() {

    }

    override fun onRestartFragment() {

    }

    override fun createObserver() {

    }
}