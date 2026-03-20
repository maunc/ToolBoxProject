package com.maunc.base.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ImmersionBar
import com.maunc.base.ext.finishCurrentActivity
import com.maunc.base.ext.getVmClazz
import com.maunc.base.ext.inflateBindingWithGeneric

abstract class BaseActivity<VM : BaseViewModel<*>, DB : ViewDataBinding> : AppCompatActivity() {

    lateinit var mViewModel: VM

    lateinit var mDatabind: DB

    private val backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressCallBack()
        }
    }

    /**
     * 返回键 默认是销毁当前页面，可以重写定制
     */
    open fun onBackPressCallBack() {
        finishCurrentActivity()
    }

    /**
     * 加载view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 创建LiveData数据观察者
     */
    abstract fun createObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .fitsSystemWindows(true).init()
        mDatabind = inflateBindingWithGeneric(layoutInflater)
        mDatabind.lifecycleOwner = this
        setContentView(mDatabind.root)
        mViewModel = createViewModel()
        lifecycle.addObserver(mViewModel)
        initView(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, backPressCallback)
        createObserver()
    }

    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
//        return ViewModelProvider(this)[getVmClazz(this)]
        return defaultViewModelProviderFactory.create(getVmClazz(this))
    }

    /**
     * 获取ViewModel
     */
    fun <T : BaseViewModel<*>> getViewModel(quickViewModel: Class<T>): T {
        return ViewModelProvider(this)[quickViewModel]
    }
}