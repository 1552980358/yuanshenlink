package com.qyinter.yuanshenlink.util

import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar

object SnackBarUtil {
    
    fun CoordinatorLayout.snack(message: Int, length: Int = LENGTH_SHORT) =
        Snackbar.make(this, message, length)
    
}