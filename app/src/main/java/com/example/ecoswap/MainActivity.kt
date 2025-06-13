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
import androidx.lifecycle.lifecycleScope
import com.example.ecoswap.ui.apis.RetrofitInstance
import com.example.ecoswap.UserManager
import kotlinx.coroutines.launch
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    var hasUnreadMessages = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.setupWithNavController(navController)

        binding.btnNotifications.setOnClickListener {
            navController.navigate(R.id.navigation_notifications)
        }

        binding.btnSettings.setOnClickListener {
            navController.navigate(R.id.navigation_settings)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                    navController.popBackStack(R.id.navigation_home, false)
                }
                R.id.navigation_settings, R.id.navigation_notifications -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.navigation_settings, R.id.navigation_notifications -> {
                    navController.navigate(R.id.navigation_home)
                }
                else -> {
                    onBackPressed()
                }
            }
        }

        // Start checking for unread messages
        checkUnreadMessages()
    }

    private fun checkUnreadMessages() {
        lifecycleScope.launch {
            try {
                val receivedMessages = RetrofitInstance.api.getReceivedMessages(UserManager.ownerId)
                hasUnreadMessages = receivedMessages.any { !it.read }
                updateNotificationBadge()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error checking unread messages", e)
            }
        }
    }

    fun updateNotificationBadge() {
        val notificationButton = binding.btnNotifications
        val parent = notificationButton.parent as? FrameLayout ?: return

        // Remove existing badge if any
        parent.findViewWithTag<TextView>("badge")?.let {
            parent.removeView(it)
        }

        if (hasUnreadMessages) {
            // Create badge with exclamation mark
            val badge = TextView(this).apply {
                tag = "badge"
                text = "!"
                setTextColor(Color.WHITE)
                textSize = 16f
                setBackgroundColor(Color.RED)
                setPadding(12, 6, 12, 6)
                elevation = 4f
            }

            // Add badge to notification button
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.TOP or android.view.Gravity.END
                topMargin = 4
                rightMargin = 4
            }
            parent.addView(badge, params)
        }
    }

    override fun onResume() {
        super.onResume()
        checkUnreadMessages()
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