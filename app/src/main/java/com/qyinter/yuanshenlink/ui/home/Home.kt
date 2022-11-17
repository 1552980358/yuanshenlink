package com.qyinter.yuanshenlink.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.platform.Hold
import com.qyinter.yuanshenlink.databinding.FragmentHomeBinding

class Home: Fragment(), OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!
    
    private val appBarLayout: AppBarLayout
        get() = binding.appBarLayout
    private val materialToolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val apiButton: MaterialButton
        get() = binding.materialButtonApi
    private val webViewButton: MaterialButton
        get() = binding.materialButtonWebView

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        exitTransition = Hold()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appBarLayout.statusBarForeground = MaterialShapeDrawable.createWithElevationOverlay(requireContext())
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(materialToolbar)
        }
        apiButton.setOnClickListener(this)
        webViewButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            apiButton -> navController.navigate(
                HomeDirections.actionHomeToApi(),
                FragmentNavigatorExtras(apiButton to apiButton.transitionName)
            )

            webViewButton -> navController.navigate(
                HomeDirections.actionHomeToWebView(),
                FragmentNavigatorExtras(webViewButton to webViewButton.transitionName)
            )
        }
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}