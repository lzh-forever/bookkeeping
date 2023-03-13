package com.example.bookkeeping.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bookkeeping.R
import com.example.bookkeeping.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 隐藏 ActionBar
        supportActionBar?.hide()
        lifecycleScope.launchWhenCreated { }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val bottomSet =
            setOf(R.id.navigation_home, R.id.navigation_bookkeeping, R.id.navigation_notifications)
        val appBarConfiguration = AppBarConfiguration(bottomSet)
        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                when (destination.id) {
                    in bottomSet -> navView.visibility = View.VISIBLE
                    else -> navView.visibility = View.GONE
                }
            }
        })
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}