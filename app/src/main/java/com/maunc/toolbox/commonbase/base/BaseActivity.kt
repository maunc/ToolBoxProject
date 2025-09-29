package com.maunc.toolbox.commonbase.base

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.getVmClazz
import com.maunc.toolbox.commonbase.ext.inflateBindingWithGeneric

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
        enableEdgeToEdge()
        mDatabind = inflateBindingWithGeneric(layoutInflater)
        setContentView(mDatabind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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