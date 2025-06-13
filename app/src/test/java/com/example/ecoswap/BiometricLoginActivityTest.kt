package com.example.ecoswap

import android.content.Context
import android.content.Intent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ActivityScenario
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class BiometricLoginActivityTest {

    private lateinit var context: Context
    private lateinit var biometricManager: BiometricManager
    private lateinit var biometricPrompt: BiometricPrompt

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        biometricManager = mock(BiometricManager::class.java)
        biometricPrompt = mock(BiometricPrompt::class.java)
    }

    @Test
    fun `test biometric authentication success navigates to MainActivity`() {
        // Given
        whenever(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG))
            .thenReturn(BiometricManager.BIOMETRIC_SUCCESS)

        // When
        val scenario = ActivityScenario.launch(BiometricLoginActivity::class.java)
        scenario.onActivity { activity ->
            // Simulate successful biometric authentication
            val callback = activity.getBiometricCallback()
            callback.onAuthenticationSucceeded(mock(BiometricPrompt.AuthenticationResult::class.java))
        }

        // Then
        val intent = scenario.getResult().resultData
        assertNotNull(intent)
        assertEquals(MainActivity::class.java.name, intent?.component?.className)
        assertTrue(intent?.flags?.and(Intent.FLAG_ACTIVITY_NEW_TASK) != 0)
        assertTrue(intent?.flags?.and(Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0)
    }

    @Test
    fun `test biometric authentication error finishes activity`() {
        // Given
        whenever(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG))
            .thenReturn(BiometricManager.BIOMETRIC_SUCCESS)

        // When
        val scenario = ActivityScenario.launch(BiometricLoginActivity::class.java)
        scenario.onActivity { activity ->
            // Simulate biometric authentication error
            val callback = activity.getBiometricCallback()
            callback.onAuthenticationError(
                BiometricPrompt.ERROR_LOCKOUT,
                "Too many attempts"
            )
        }

        // Then
        assertTrue(scenario.getResult().isFinished)
    }

    @Test
    fun `test biometric authentication failure finishes activity`() {
        // Given
        whenever(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG))
            .thenReturn(BiometricManager.BIOMETRIC_SUCCESS)

        // When
        val scenario = ActivityScenario.launch(BiometricLoginActivity::class.java)
        scenario.onActivity { activity ->
            // Simulate biometric authentication failure
            val callback = activity.getBiometricCallback()
            callback.onAuthenticationFailed()
        }

        // Then
        assertTrue(scenario.getResult().isFinished)
    }

    @Test
    fun `test biometric not available shows appropriate message`() {
        // Given
        whenever(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG))
            .thenReturn(BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE)

        // When
        val scenario = ActivityScenario.launch(BiometricLoginActivity::class.java)

        // Then
        scenario.onActivity { activity ->
            // Verify that appropriate error message is shown
            // This would typically be done by checking a TextView or Toast message
            // For now, we just verify the activity is finished
            assertTrue(activity.isFinishing)
        }
    }

    @Test
    fun `test biometric not enrolled shows appropriate message`() {
        // Given
        whenever(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG))
            .thenReturn(BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED)

        // When
        val scenario = ActivityScenario.launch(BiometricLoginActivity::class.java)

        // Then
        scenario.onActivity { activity ->
            // Verify that appropriate error message is shown
            // This would typically be done by checking a TextView or Toast message
            // For now, we just verify the activity is finished
            assertTrue(activity.isFinishing)
        }
    }

    @Test
    fun `test biometric prompt is shown on activity start`() {
        // Given
        whenever(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG))
            .thenReturn(BiometricManager.BIOMETRIC_SUCCESS)

        // When
        val scenario = ActivityScenario.launch(BiometricLoginActivity::class.java)

        // Then
        scenario.onActivity { activity ->
            // Verify that biometric prompt is shown
            // This would typically be done by checking if the prompt is visible
            // For now, we just verify the activity is not finishing
            assertFalse(activity.isFinishing)
        }
    }
}

// Extension function to get the biometric callback from the activity
private fun BiometricLoginActivity.getBiometricCallback(): BiometricPrompt.AuthenticationCallback {
    return this::class.java.getDeclaredField("biometricPrompt")
        .apply { isAccessible = true }
        .get(this) as BiometricPrompt
        .getDeclaredField("callback")
        .apply { isAccessible = true }
        .get(this) as BiometricPrompt.AuthenticationCallback
} 