package com.maunc.toolbox.pushbox.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainDimens
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.setScale
import com.maunc.toolbox.commonbase.ext.setWidthAndHeight
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.commonbase.ext.toast
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.pushBoxCurrentGradleIndex
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
import com.maunc.toolbox.pushbox.ui.dialog.PushBoxPassLevelDialog
import com.maunc.toolbox.pushbox.view.PushBoxGameView
import com.maunc.toolbox.pushbox.viewmodel.PushBoxMainViewModel

@SuppressLint("SetTextI18n")
class PushBoxMainActivity : BaseActivity<PushBoxMainViewModel, ActivityPushBoxMainBinding>() {

    private companion object {
        const val PUSH_BOX_CONTROLLER_LONG_TOUCH_DELAY = 220L
        const val PUSH_BOX_CONTROLLER_UP = 1
        const val PUSH_BOX_CONTROLLER_DOWN = 2
        const val PUSH_BOX_CONTROLLER_RIGHT = 3
        const val PUSH_BOX_CONTROLLER_LEFT = 4
    }

    private val onPushBoxEventListener = object : PushBoxGameView.OnPushBoxEventListener {

        override fun onCurrentGradeMoveNumber(currentNumber: Int) {
            mDatabind.pushBoxMainCurrentMoveNumTv.text = String.format(
                obtainString(R.string.push_box_main_current_move_num_text), currentNumber
            )
        }

        override fun onPassGrade(passMapIndex: Int, nextMapIndex: Int, passMoveNum: Int) {
            PushBoxPassLevelDialog().setCurrentLevel(nextMapIndex)
                .setTime(System.currentTimeMillis() - mViewModel.startTimeValue)
                .setMoveNum(passMoveNum).setOnReStartListener {
                    rePushBoxMainUI(passMapIndex)
                }.setOnNextListener {
                    rePushBoxMainUI(nextMapIndex)
                }.show(supportFragmentManager, "pushBoxPassLevelDialog")
        }

        override fun onNotGrade() {
            toast("牛逼")
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

    private var currentControllerEvent = -1 //当前点击
    private var isLongPressTriggered = false // 标记是否已触发长按
    private val continuousHandler = Handler(Looper.getMainLooper())
    private val continuousRunnable = object : Runnable {
        override fun run() {
            if (isLongPressTriggered) {
                continuousHandler.postDelayed(this, PUSH_BOX_CONTROLLER_LONG_TOUCH_DELAY)
                runOnUiThread {
                    when (currentControllerEvent) {
                        PUSH_BOX_CONTROLLER_DOWN -> mDatabind.pushBoxGameView.moveDown()
                        PUSH_BOX_CONTROLLER_RIGHT -> mDatabind.pushBoxGameView.moveRight()
                        PUSH_BOX_CONTROLLER_LEFT -> mDatabind.pushBoxGameView.moveLeft()
                        PUSH_BOX_CONTROLLER_UP -> mDatabind.pushBoxGameView.moveUp()
                    }
                }
            } else {
                continuousHandler.removeCallbacks(this)
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
        mDatabind.pushBoxControllerUp.setControllerOnTouchListener(PUSH_BOX_CONTROLLER_UP)
        mDatabind.pushBoxControllerDown.setControllerOnTouchListener(PUSH_BOX_CONTROLLER_DOWN)
        mDatabind.pushBoxControllerRight.setControllerOnTouchListener(PUSH_BOX_CONTROLLER_RIGHT)
        mDatabind.pushBoxControllerLeft.setControllerOnTouchListener(PUSH_BOX_CONTROLLER_LEFT)
        mDatabind.pushBoxGameView.setOnPushBoxEventListener(onPushBoxEventListener)
        mDatabind.pushBoxMainFunctionRecycler.layoutManager = linearLayoutManager(
            LinearLayoutManager.HORIZONTAL
        )
        mDatabind.pushBoxMainFunctionRecycler.adapter = pushBoxMainFunctionAdapter
        pushBoxMainFunctionAdapter.setList(mViewModel.initFunctionList())
        rePushBoxMainUI(obtainMMKV.getInt(pushBoxCurrentGradleIndex))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun View.setControllerOnTouchListener(controllerType: Int) {
        setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isLongPressTriggered = true
                    currentControllerEvent = controllerType
                    view.setScale(0.85f, 0.85f)
                    continuousHandler.post(continuousRunnable)
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.setScale(1f, 1f)
                    continuousHandler.removeCallbacks(continuousRunnable)
                    if (isLongPressTriggered) isLongPressTriggered = false
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false // 其他事件不消费
        }
    }

    private fun rePushBoxMainUI(currentIndex: Int) {
        mViewModel.startTimeValue = System.currentTimeMillis()
        mDatabind.pushBoxMainCurrentIndexTv.text = "第${currentIndex + 1}关"
        mDatabind.pushBoxGameView.setGateIndex(currentIndex)
        mDatabind.pushBoxMainCurrentMoveNumTv.text =
            String.format(obtainString(R.string.push_box_main_current_move_num_text), 0)
        obtainMMKV.putInt(pushBoxCurrentGradleIndex, currentIndex)
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
        appViewModel.pushBoxViewTouch.observe(this) {
            mDatabind.pushBoxGameView.setTouchMove(it)
        }
        appViewModel.initPushBoxConfig()
    }

    private fun handleControllerSize(dimension: Int) {
        mDatabind.pushBoxControllerUp.setWidthAndHeight(dimension, dimension)
        mDatabind.pushBoxControllerDown.setWidthAndHeight(dimension, dimension)
        mDatabind.pushBoxControllerLeft.setWidthAndHeight(dimension, dimension)
        mDatabind.pushBoxControllerRight.setWidthAndHeight(dimension, dimension)
    }

    override fun onDestroy() {
        super.onDestroy()
        continuousHandler.removeCallbacksAndMessages(null)
    }
}