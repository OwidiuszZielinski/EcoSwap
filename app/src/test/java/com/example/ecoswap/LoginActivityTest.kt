package com.example.ecoswap

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import android.app.Activity
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun `test login with valid credentials saves to SharedPreferences`() {
        // Given
        val email = "test@example.com"
        val password = "password123"
        sharedPreferences.edit().putString("email", email).putString("password", password).apply()

        // When
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        scenario.onActivity { activity ->
            activity.findViewById<android.widget.EditText>(R.id.emailEditText).setText(email)
            activity.findViewById<android.widget.EditText>(R.id.passwordEditText).setText(password)
            activity.findViewById<android.widget.Button>(R.id.loginButton).performClick()
        }

        // Then
        assertTrue(sharedPreferences.getBoolean("is_logged_in", false))
        assertEquals(email, sharedPreferences.getString("email", null))
        assertEquals(password, sharedPreferences.getString("password", null))
    }

    @Test
    fun `test login with invalid credentials does not save to SharedPreferences`() {
        // Given
        val email = "wrong@example.com"
        val password = "wrongpass"
        val savedEmail = "test@example.com"
        val savedPassword = "password123"
        sharedPreferences.edit()
            .putString("email", savedEmail)
            .putString("password", savedPassword)
            .apply()

        // When
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        scenario.onActivity { activity ->
            activity.findViewById<android.widget.EditText>(R.id.emailEditText).setText(email)
            activity.findViewById<android.widget.EditText>(R.id.passwordEditText).setText(password)
            activity.findViewById<android.widget.Button>(R.id.loginButton).performClick()
        }

        // Then
        assertFalse(sharedPreferences.getBoolean("is_logged_in", false))
        assertEquals(savedEmail, sharedPreferences.getString("email", null))
        assertEquals(savedPassword, sharedPreferences.getString("password", null))
    }

    @Test
    fun `test already logged in user is redirected to MainActivity`() {
        // Given
        sharedPreferences.edit()
            .putBoolean("is_logged_in", true)
            .putString("email", "test@example.com")
            .putString("password", "password123")
            .apply()

        // When
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        val intent = scenario.getResult().resultData

        // Then
        assertNotNull(intent)
        assertEquals(MainActivity::class.java.name, intent?.component?.className)
        assertTrue(intent?.flags?.and(Intent.FLAG_ACTIVITY_NEW_TASK) != 0)
        assertTrue(intent?.flags?.and(Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0)
    }

    @Test
    fun `test login with empty credentials shows error`() {
        // Given
        val email = ""
        val password = ""

        // When
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        scenario.onActivity { activity ->
            activity.findViewById<android.widget.EditText>(R.id.emailEditText).setText(email)
            activity.findViewById<android.widget.EditText>(R.id.passwordEditText).setText(password)
            activity.findViewById<android.widget.Button>(R.id.loginButton).performClick()
        }

        // Then
        assertFalse(sharedPreferences.getBoolean("is_logged_in", false))
        assertNull(sharedPreferences.getString("email", null))
        assertNull(sharedPreferences.getString("password", null))
    }

    @Test
    fun `test login with invalid email format shows error`() {
        // Given
        val email = "invalid-email"
        val password = "password123"

        // When
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        scenario.onActivity { activity ->
            activity.findViewById<android.widget.EditText>(R.id.emailEditText).setText(email)
            activity.findViewById<android.widget.EditText>(R.id.passwordEditText).setText(password)
            activity.findViewById<android.widget.Button>(R.id.loginButton).performClick()
        }

        // Then
        assertFalse(sharedPreferences.getBoolean("is_logged_in", false))
        assertNull(sharedPreferences.getString("email", null))
        assertNull(sharedPreferences.getString("password", null))
    }

    @Test
    fun `test login with short password shows error`() {
        // Given
        val email = "test@example.com"
        val password = "123" // Too short password

        // When
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        scenario.onActivity { activity ->
            activity.findViewById<android.widget.EditText>(R.id.emailEditText).setText(email)
            activity.findViewById<android.widget.EditText>(R.id.passwordEditText).setText(password)
            activity.findViewById<android.widget.Button>(R.id.loginButton).performClick()
        }

        // Then
        assertFalse(sharedPreferences.getBoolean("is_logged_in", false))
        assertNull(sharedPreferences.getString("email", null))
        assertNull(sharedPreferences.getString("password", null))
    }
} 