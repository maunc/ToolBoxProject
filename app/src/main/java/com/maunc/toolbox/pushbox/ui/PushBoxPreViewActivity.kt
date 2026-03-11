package com.maunc.toolbox.pushbox.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityPushBoxPreViewBinding
import com.maunc.toolbox.pushbox.viewmodel.PushBoxPreViewViewModel

class PushBoxPreViewActivity :BaseActivity<PushBoxPreViewViewModel,ActivityPushBoxPreViewBinding>(){
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.push_box_preview_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
    }

    override fun createObserver() {
        
    }

}