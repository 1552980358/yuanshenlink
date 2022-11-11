package com.qyinter.yuanshenlink

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.qyinter.yuanshenlink.databinding.ActivityMainBinding
import com.qyinter.yuanshenlink.dto.ChouKaObj
import com.qyinter.yuanshenlink.http.HttpUtil

class MainActivity : AppCompatActivity() {

    private companion object {
        const val MIHOYO_USER_URL = "https://user.mihoyo.com"
    }
    
    private lateinit var binding: ActivityMainBinding
    private val webView: WebView
        get() = binding.webview
    private val editText: EditText
        get() = binding.input
    private val materialButton: MaterialButton
        get() = binding.cookieBtn
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        webView.loadUrl(MIHOYO_USER_URL)
        val handle: Handler = object : Handler(Looper.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg: Message) {
                //正常操作
                msg.let {
                    val obj: ChouKaObj = msg.obj as ChouKaObj
                    if (obj.code == 200) {

                        if (obj.urlListObj.size > 1) {
                            val uidList = obj.urlListObj.map { it.uid }.toTypedArray()

                            AlertDialog.Builder(this@MainActivity)
                                .setIcon(R.drawable.ic_launcher_foreground)
                                .setItems(uidList) { _, which ->
                                    val selected = uidList[which]
                                    obj.urlListObj.find { it.uid == selected }?.let {
                                        copyToClipboard(it.url)
                                    }
                                    Toast.makeText(applicationContext, "你选择了Uid: $selected", Toast.LENGTH_SHORT).show()
                            }.show()
                        } else {
                            copyToClipboard(obj.urlListObj[0].url)
                        }
                    } else {
                        shortToast("请先登录米游社")
                    }
                }
            }
        }

        materialButton.setOnClickListener {
            val instance = CookieManager.getInstance()
            val cookie = instance.getCookie(MIHOYO_USER_URL)
            HttpUtil.getAuthKey(cookie, handle)
        }
    }

    private fun copyToClipboard(url: String) {
        editText.setText(url)
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            .setPrimaryClip(ClipData.newPlainText("", url))
        shortToast("已复制到剪贴板")
    }

    private fun shortToast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

}
