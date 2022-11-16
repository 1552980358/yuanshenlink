package com.qyinter.yuanshenlink

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.qyinter.yuanshenlink.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView
    private val materialToolbar: MaterialToolbar
        get() = binding.materialToolbar
    
    private lateinit var navController: NavController
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(materialToolbar)
        navController = fragmentContainerView.getFragment<NavHostFragment>().navController
        val appbarConfiguration = AppBarConfiguration(navController.graph)
        materialToolbar.setupWithNavController(navController, appbarConfiguration)
    }

}
