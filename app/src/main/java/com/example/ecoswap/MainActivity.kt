package com.example.ecoswap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ecoswap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set up navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up bottom navigation
        binding.navView.setupWithNavController(navController)

        // Set up notification and settings button click listeners
        binding.btnNotifications.setOnClickListener {
            navController.navigate(R.id.navigation_notifications)
        }

        binding.btnSettings.setOnClickListener {
            navController.navigate(R.id.navigation_settings)
        }

        // Handle navigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                    // Clear back stack when navigating to home
                    navController.popBackStack(R.id.navigation_home, false)
                }
                R.id.navigation_settings -> {
                    // Enable up navigation for settings
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                else -> {
                    // Disable up navigation for other destinations
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
            }
        }

        // Handle up navigation
        binding.toolbar.setNavigationOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.navigation_settings -> {
                    navController.navigate(R.id.navigation_home)
                }
                else -> {
                    onBackPressed()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return when (navController.currentDestination?.id) {
            R.id.navigation_settings -> {
                navController.navigate(R.id.navigation_home)
                true
            }
            else -> navController.navigateUp() || super.onSupportNavigateUp()
        }
    }
}