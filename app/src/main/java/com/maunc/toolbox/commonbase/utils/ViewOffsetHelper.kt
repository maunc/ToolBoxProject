package com.maunc.toolbox.commonbase.utils

import android.view.View
import androidx.core.view.ViewCompat

internal class ViewOffsetHelper(private val mView: View) {
    private var mLayoutTop = 0
    private var mLayoutLeft = 0
    private var topAndBottomOffset: Int = 0
    private var leftAndRightOffset: Int = 0

    fun onViewLayout() {
        // Now grab the intended top
        mLayoutTop = mView.top
        mLayoutLeft = mView.left
        // And offset it as needed
        updateOffsets()
    }

    private fun updateOffsets() {
        // Else we can use the offset position methods
        ViewCompat.offsetTopAndBottom(mView, topAndBottomOffset - mView.top - mLayoutTop)
        ViewCompat.offsetLeftAndRight(mView, leftAndRightOffset - mView.left - mLayoutLeft)
    }

    /**
     * Set the top and bottom offset for this [ViewOffsetHelper]'s view.
     *
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    fun setTopAndBottomOffset(offset: Int): Boolean {
        if (topAndBottomOffset != offset) {
            topAndBottomOffset = offset
            updateOffsets()
            return true
        }
        return false
    }

    /**
     * Set the left and right offset for this [ViewOffsetHelper]'s view.
     *
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    fun setLeftAndRightOffset(offset: Int): Boolean {
        if (leftAndRightOffset != offset) {
            leftAndRightOffset = offset
            updateOffsets()
            return true
        }
        return false
    }
}