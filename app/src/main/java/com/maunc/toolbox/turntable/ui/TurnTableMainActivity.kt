package com.maunc.toolbox.turntable.ui

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainActivityIntentPutData
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.commonbase.utils.SoundPlayerHelper
import com.maunc.toolbox.commonbase.utils.TURN_TABLE_ANIM_SOUND_ID
import com.maunc.toolbox.databinding.ActivityTurnTableMainBinding
import com.maunc.toolbox.turntable.adapter.TurnTableLoggerAdapter
import com.maunc.toolbox.turntable.constant.RESULT_SOURCE_FROM_TURN_TABLE_SETTING_PAGE
import com.maunc.toolbox.turntable.ui.TurnTableSettingActivity.Companion.TURN_TABLE_ENABLE_SOUND_EFFECT
import com.maunc.toolbox.turntable.ui.TurnTableSettingActivity.Companion.TURN_TABLE_ENABLE_TOUCH
import com.maunc.toolbox.turntable.viewmodel.TurnTableMainViewModel
import com.us.mauncview.TurnTableView

class TurnTableMainActivity : BaseActivity<TurnTableMainViewModel, ActivityTurnTableMainBinding>() {

    private val turnTableActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_SOURCE_FROM_TURN_TABLE_SETTING_PAGE) {
            val intent = it.data!!
            mViewModel.initSettingConfig(
                enableTouch = intent.getBooleanExtra(TURN_TABLE_ENABLE_TOUCH, false),
                enableSoundEffect = intent.getBooleanExtra(TURN_TABLE_ENABLE_SOUND_EFFECT, false)
            )
        }
    }

    private val turnTableLoggerAdapter by lazy {
        TurnTableLoggerAdapter()
    }

    private val soundPlayer by lazy {
        SoundPlayerHelper()
    }

    private val mTurnTableListener = object : TurnTableView.OnTurnTableEventListener {
        override fun onRotateStart() {

        }

        override fun onRotateEnd(content: String) {
            turnTableLoggerAdapter.addResultLogger(content)
        }

        override fun onRotateIng(content: String, posIndex: Int) {
            if (content.isNotEmpty() && posIndex != mViewModel.turnTableAnimLastSelectIndex) {
                if (mViewModel.turnTableIsEnableSoundEffect.value!!) {
                    soundPlayer.playSound(TURN_TABLE_ANIM_SOUND_ID)
                }
                mViewModel.turnTableAnimLastSelectIndex = posIndex
                mViewModel.turnTableAnimSelectContent.value = content
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.turnTableMainViewModel = mViewModel
        // 初始化
        mViewModel.initViewModelConfig()
        soundPlayer.loadSound(R.raw.turn_table_anim_sound, TURN_TABLE_ANIM_SOUND_ID)
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title)
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            turnTableActivityResult.launch(obtainActivityIntentPutData(
                TurnTableSettingActivity::class.java,
                mutableMapOf<String, Any>().apply {
                    put(
                        TURN_TABLE_ENABLE_TOUCH,
                        mViewModel.turnTableIsEnableTouch.value!!
                    )
                    put(
                        TURN_TABLE_ENABLE_SOUND_EFFECT,
                        mViewModel.turnTableIsEnableSoundEffect.value!!
                    )
                }
            ))
        }
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.startTurnTable.clickScale {
            mDatabind.turnTableView.startTurnTable()
        }
        mDatabind.turnTableListIcon.clickScale {
            startTargetActivity(TurnTableDataManagerActivity::class.java)
        }
        mDatabind.turnTableView.setOnTurnTableListener(mTurnTableListener)
        mDatabind.turnTableLoggerRec.layoutManager = linearLayoutManager()
        mDatabind.turnTableLoggerRec.adapter = turnTableLoggerAdapter
        turnTableLoggerAdapter.addTipsLogger("欢迎体验转盘")
    }

    override fun createObserver() {
        mViewModel.currentSelectData.observe(this) {
            mDatabind.turnTableView.setTurnTableContents(it)
        }
    }

    override fun onPause() {
        super.onPause()
        mDatabind.turnTableView.pauseTurnTable()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.initSelectTurnTableData()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer.release()
    }
}