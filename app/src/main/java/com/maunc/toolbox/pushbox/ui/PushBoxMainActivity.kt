package com.maunc.toolbox.pushbox.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainDimens
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.setWidthAndHeight
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.databinding.ActivityPushBoxMainBinding
import com.maunc.toolbox.pushbox.adapter.PushBoxMainFunctionAdapter
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MAX
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MAX_TWO
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MEDIUM
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MIN
import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MIN_TWO
import com.maunc.toolbox.pushbox.constant.allGradesMapData
import com.maunc.toolbox.pushbox.data.PushBoxMainFunctionData.Companion.PUSH_BOX_MAIN_FUNCTION_NEXT_GRADE
import com.maunc.toolbox.pushbox.data.PushBoxMainFunctionData.Companion.PUSH_BOX_MAIN_FUNCTION_RESTART_GRADE
import com.maunc.toolbox.pushbox.data.PushBoxMainFunctionData.Companion.PUSH_BOX_MAIN_FUNCTION_UNDO_GRADE
import com.maunc.toolbox.pushbox.data.PushBoxMainFunctionData.Companion.PUSH_BOX_MAIN_FUNCTION_UP_GRADE
import com.maunc.toolbox.pushbox.view.PushBoxGameView
import com.maunc.toolbox.pushbox.viewmodel.PushBoxMainViewModel

@SuppressLint("SetTextI18n")
class PushBoxMainActivity : BaseActivity<PushBoxMainViewModel, ActivityPushBoxMainBinding>() {

    private val onPushBoxEventListener = object : PushBoxGameView.OnPushBoxEventListener {

        override fun onCurrentGradeMoveNumber(currentNumber: Int) {
            mDatabind.pushBoxMainCurrentMoveNumTv.text = "当前关卡移动次数:$currentNumber"
        }

        override fun onNextGrade(mapIndex: Int, map: ArrayList<ArrayList<Int>>) {
            rePushBoxMainUI(mapIndex)
        }

        override fun onNotGrade() {
        }
    }

    private val pushBoxMainFunctionAdapter by lazy {
        PushBoxMainFunctionAdapter().apply {
            setOnPushBoxFunctionListener {
                when (it) {
                    PUSH_BOX_MAIN_FUNCTION_UP_GRADE -> {
                        val targetIndex =
                            mDatabind.pushBoxGameView.obtainCurrentGradleIndex().first - 1
                        if (targetIndex < 0) return@setOnPushBoxFunctionListener
                        rePushBoxMainUI(targetIndex)
                    }

                    PUSH_BOX_MAIN_FUNCTION_NEXT_GRADE -> {
                        val targetIndex =
                            mDatabind.pushBoxGameView.obtainCurrentGradleIndex().first + 1
                        if (targetIndex > allGradesMapData.size - 1) return@setOnPushBoxFunctionListener
                        rePushBoxMainUI(targetIndex)
                    }

                    PUSH_BOX_MAIN_FUNCTION_UNDO_GRADE -> {
                        mDatabind.pushBoxGameView.undoLastStep()
                    }

                    PUSH_BOX_MAIN_FUNCTION_RESTART_GRADE -> {
                        rePushBoxMainUI(mDatabind.pushBoxGameView.obtainCurrentGradleIndex().first)
                    }
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.tool_box_item_push_box_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            startTargetActivity(PushBoxSettingActivity::class.java)
        }
        rePushBoxMainUI(0)
        mDatabind.pushBoxControllerUp.clickScale {
            mDatabind.pushBoxGameView.moveUp()
        }
        mDatabind.pushBoxControllerDown.clickScale {
            mDatabind.pushBoxGameView.moveDown()
        }
        mDatabind.pushBoxControllerLeft.clickScale {
            mDatabind.pushBoxGameView.moveLeft()
        }
        mDatabind.pushBoxControllerRight.clickScale {
            mDatabind.pushBoxGameView.moveRight()
        }
        mDatabind.pushBoxGameView.setOnPushBoxEventListener(onPushBoxEventListener)
        mDatabind.pushBoxMainFunctionRecycler.layoutManager = linearLayoutManager(
            LinearLayoutManager.HORIZONTAL
        )
        mDatabind.pushBoxMainFunctionRecycler.adapter = pushBoxMainFunctionAdapter
        pushBoxMainFunctionAdapter.setList(mViewModel.initFunctionList())
    }

    private fun rePushBoxMainUI(currentIndex: Int) {
        mDatabind.pushBoxMainCurrentIndexTv.text = "第${currentIndex + 1}关"
        mDatabind.pushBoxGameView.setGateIndex(currentIndex)
        mDatabind.pushBoxMainCurrentMoveNumTv.text = "当前关卡移动次数:0"
    }

    override fun createObserver() {
        appViewModel.pushBoxControllerSize.observe(this) {
            when (it) {
                PUSH_BOX_CONTROLLER_SIZE_MIN -> handleControllerSize(obtainDimens(R.dimen.dp_50))
                PUSH_BOX_CONTROLLER_SIZE_MIN_TWO -> handleControllerSize(obtainDimens(R.dimen.dp_75))
                PUSH_BOX_CONTROLLER_SIZE_MEDIUM -> handleControllerSize(obtainDimens(R.dimen.dp_100))
                PUSH_BOX_CONTROLLER_SIZE_MAX_TWO -> handleControllerSize(obtainDimens(R.dimen.dp_125))
                PUSH_BOX_CONTROLLER_SIZE_MAX -> handleControllerSize(obtainDimens(R.dimen.dp_150))
            }
        }
        appViewModel.initPushBoxConfig()
    }

    private fun handleControllerSize(dimension: Int) {
        mDatabind.pushBoxControllerUp.setWidthAndHeight(dimension, dimension)
        mDatabind.pushBoxControllerDown.setWidthAndHeight(dimension, dimension)
        mDatabind.pushBoxControllerLeft.setWidthAndHeight(dimension, dimension)
        mDatabind.pushBoxControllerRight.setWidthAndHeight(dimension, dimension)
    }
}