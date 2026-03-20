package com.maunc.toolbox.commonbase.databindadapter

import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.addView

object CommonDataBindAdapter {

    /**
     * 使用这个方法需要在xml中定义一个
     * <androidx.appcompat.widget.LinearLayoutCompat
     *    android:layout_width="match_parent"
     *    android:layout_height="match_parent"
     *    app:handleNotDataLayout="@{selectGroupToMainViewModel.groupData}" />
     *    然后传入这个对应的要请求的列表即可
     *    不给LiveData设置初始值  就会返回null
     */
    @JvmStatic
    @BindingAdapter(value = ["handleNotDataLayout"], requireAll = false)
    fun <T> handleNotDataLayout(viewGroup: ViewGroup, dataList: MutableList<T>?) {
        viewGroup.removeAllViews()
        if (dataList != null && dataList.isEmpty()) {
            viewGroup.addView(R.layout.layout_not_data_common)
        }
    }
}