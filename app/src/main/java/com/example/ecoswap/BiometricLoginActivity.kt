package com.example.ecoswap

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.ecoswap.databinding.ActivityBiometricLoginBinding
import java.util.concurrent.Executor

class BiometricLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBiometricLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBiometricAuthentication()
    }

    private fun setupBiometricAuthentication() {
        val executor: Executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            // Sukces biometrii
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                startActivity(
                    Intent(this@BiometricLoginActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            }

            // Błąd biometrii 
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                finish()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }
        })

        // Konfiguracja promptu biometrycznego
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Logowanie biometryczne")
            .setSubtitle("Użyj odcisku palca, aby zalogować się")
            .setNegativeButtonText("Anuluj")
            .build()

        // Sprawdź czy biometria jest dostępna na urządzeniu
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            // Jeśli biometria niedostępna, zamknij aktywność
            finish()
        }
    }
}