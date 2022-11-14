package com.qyinter.yuanshenlink.ui.webview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.qyinter.yuanshenlink.R
import com.qyinter.yuanshenlink.databinding.FragmentWebviewBinding

class WebView: Fragment() {
    
    private companion object {
        const val TAG = "WebView"
    }

    private var _binding: FragmentWebviewBinding? = null
    private val binding: FragmentWebviewBinding
        get() = _binding!!

    private val materialToolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val webView
        get() = binding.webView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWebviewBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(materialToolbar)
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_web_view, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.refresh -> webView.reload()
                        R.id.done -> {  }
                    }
                    return true
                }
            }, viewLifecycleOwner)
        }

        with(webView) {
            with(settings) {
                @Suppress("SetJavaScriptEnabled")
                javaScriptEnabled = true
                domStorageEnabled = true
            }
            loadUrl(getString(R.string.web_view_url))
            
            webViewClient = object: WebViewClient() {
                override fun onLoadResource(view: WebView?, url: String) {
                    Log.e("$TAG:onLoadResource", url)
                }
                override fun onPageFinished(view: WebView?, url: String) {
                    Log.e("$TAG:onPageFinished", url)
                }
                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest): WebResourceResponse? {
                    @Suppress("LongLogTag")
                    Log.e("$TAG:shouldInterceptRequest", "${request.method} to ${request.url}")
                    return super.shouldInterceptRequest(view, request)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}