package com.maunc.toolbox.commonbase.base

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class BaseViewModel<M : BaseModel?> : ViewModel(), BaseLifecycle {

    var model: M? = null

    init {
        val mClass = obtainGenericModelClass()
        if (mClass != null) {
            try {
                model = mClass.newInstance()
            } catch (e: IllegalAccessException) {
                Log.e("BaseViewModel", e.toString())
            } catch (e: InstantiationException) {
                Log.e("BaseViewModel", e.toString())
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun obtainGenericModelClass(): Class<M>? {
        var genericSuperclass: Type? = javaClass.genericSuperclass
        var superclass: Class<*>? = javaClass.superclass
        while (superclass != null) {
            if (genericSuperclass is ParameterizedType) {
                val modelType = genericSuperclass.actualTypeArguments[0]
                if (modelType is Class<*>) {
                    return modelType as Class<M>
                }
            }
            genericSuperclass = superclass.genericSuperclass
            superclass = superclass.superclass
        }
        Log.e("BaseViewModel", "No generic model class found for ${javaClass.name}")
        return null
    }

    override fun onCreate(owner: LifecycleOwner) {
        model?.onCreate(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        model?.onStart(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        model?.onStop(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        model?.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        model?.onPause(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        model?.onDestroy(owner)
    }
}