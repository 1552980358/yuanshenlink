package com.qyinter.yuanshenlink.ui.webview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.qyinter.yuanshenlink.MainViewModel
import com.qyinter.yuanshenlink.R
import com.qyinter.yuanshenlink.databinding.FragmentWebviewBinding
import com.qyinter.yuanshenlink.util.AccountUtil.requestAuthKey
import com.qyinter.yuanshenlink.util.AccountUtil.requestLoginTokens
import com.qyinter.yuanshenlink.util.AccountUtil.requestTidCookie
import com.qyinter.yuanshenlink.util.AccountUtil.requestUserServiceList
import com.qyinter.yuanshenlink.util.CoroutineUtil.io
import com.qyinter.yuanshenlink.util.CoroutineUtil.ui
import com.qyinter.yuanshenlink.util.SnackBarUtil.snack
import com.qyinter.yuanshenlink.util.UserService
import kotlinx.coroutines.Job

class WebView: Fragment() {
    
    private companion object {
        const val TAG = "WebView"
    }

    private lateinit var navController: NavController
    
    private var _binding: FragmentWebviewBinding? = null
    private val binding: FragmentWebviewBinding
        get() = _binding!!

    private val appBarLayout: AppBarLayout
        get() = binding.appBarLayout
    private val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.swipeRefreshLayout
    private val webView
        get() = binding.webView
    private val materialToolbar: MaterialToolbar
        get() = binding.materialToolbar
    
    private var snackBar: Snackbar? = null
    private var job: Job? = null
    
    private val viewModel by viewModels<MainViewModel>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
        navController = findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWebviewBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appBarLayout.statusBarForeground = MaterialShapeDrawable.createWithElevationOverlay(requireContext())
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(materialToolbar)
            addMenuProvider(
                object : MenuProvider {
                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                        menuInflater.inflate(R.menu.menu_web_view, menu)
                    }
                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                        when (menuItem.itemId) {
                            R.id.refresh -> webView.reload()
                            R.id.done -> obtainUrl()
                        }
                        return true
                    }
                },
                viewLifecycleOwner
            )
        }
    
        swipeRefreshLayout.isEnabled = false
        
        materialToolbar.setupWithNavController(
            navController,
            AppBarConfiguration(navController.graph)
        )

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
    
    private fun obtainUrl() {
        swipeRefreshLayout.isRefreshing = true
        snackBar = binding.root.snack(R.string.snack_obtaining).also { it.show() }
        job = io {
            var cookie: String? = CookieManager.getInstance().getCookie(getString(R.string.web_view_url))
            if (cookie == null) {
                disableUiComponents()
                return@io
            }
            
            val account = requestLoginTokens(cookie)
            if (account == null) {
                disableUiComponents()
                return@io
            }
            
            cookie = requestTidCookie(account, cookie)
            if (cookie == null) {
                disableUiComponents()
                return@io
            }
            
            val userServiceList = requestUserServiceList(cookie)?.onEach { userService ->
                requestAuthKey(userService, cookie)?.let { userService.authKey = it }
            }
            
            if (userServiceList == null) {
                disableUiComponents()
                return@io
            }
            
            when (userServiceList.size) {
                1 -> {
                    userServiceList.first().url.let {
                        ui {
                            viewModel.setUrl(it, requireContext())
                            navController.navigateUp()
                        }
                    }
                }
                else -> {
                    disableUiComponents()
                    launchSelectAccount(userServiceList)
                }
            }
        }
    }
    
    private fun disableUiComponents(block: (() -> Unit)? = null) = ui {
        swipeRefreshLayout.isRefreshing = false
        snackBar?.let {
            it.dismiss()
            snackBar = null
        }
        block?.invoke()
        job = null
    }
    
    private fun launchSelectAccount(userServiceList: List<UserService>) = SelectAccountDialog(userServiceList) {
        onPositive {
            viewModel.setUrl(it.url, requireContext())
            navController.navigateUp()
        }
    }.show(parentFragmentManager)

    override fun onDestroyView() {
        webView.clearCache(true)
        job?.cancel()
        _binding = null
        super.onDestroyView()
    }

}