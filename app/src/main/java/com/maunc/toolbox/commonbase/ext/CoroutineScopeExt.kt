package com.maunc.toolbox.commonbase.ext

import androidx.lifecycle.viewModelScope
import com.maunc.toolbox.commonbase.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *  调用携程
 */
fun <T> BaseViewModel<*>.launch(
    block: () -> T,
    success: (T) -> Unit = {},
    error: (Throwable) -> Unit = {},
) {
    viewModelScope.launch {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                block()
            }
        }.onSuccess {
            success(it)
        }.onFailure {
            error(it)
        }
    }
}
