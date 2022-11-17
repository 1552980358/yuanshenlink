package com.qyinter.yuanshenlink

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qyinter.yuanshenlink.util.ToastUtil.toast

class MainViewModel: ViewModel() {
    
    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url
    fun setUrl(url: String, context: Context) {
        _url.value = url
        
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            .setPrimaryClip(ClipData.newPlainText("", url))
        context.toast(R.string.snack_copied)
    }
    
}