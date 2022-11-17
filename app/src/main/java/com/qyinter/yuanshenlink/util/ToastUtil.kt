package com.qyinter.yuanshenlink.util

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.StringRes

object ToastUtil {
    
    fun Context.toast(@StringRes resId: Int, length: Int = LENGTH_SHORT) =
        Toast.makeText(this, resId, length).show()

}