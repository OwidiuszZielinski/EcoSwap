package com.example.ecoswap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.ecoswap.databinding.ActivityLoginBinding
import com.example.ecoswap.auth.AuthManager
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize AuthManager
        AuthManager.init(this)

        // Check if user is already logged in
        if (AuthManager.isLoggedIn()) {
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
            return
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Wprowadź email i hasło", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading state
            binding.loginButton.isEnabled = false
            binding.loginButton.text = "Logowanie..."

            lifecycleScope.launch {
                val result = AuthManager.login(email, password)
                result.fold(
                    onSuccess = { user ->
                        // Save login state if "Remember Me" is checked
                        if (binding.switchRememberMe.isChecked) {
                            // AuthManager already handles this
                        }
                        
                        Toast.makeText(this@LoginActivity, "Zalogowano pomyślnie!", Toast.LENGTH_SHORT).show()
                        startActivity(
                            Intent(this@LoginActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                        finish()
                    },
                    onFailure = { error ->
                        Toast.makeText(this@LoginActivity, "Błąd logowania: ${error.message}", Toast.LENGTH_SHORT).show()
                        binding.loginButton.isEnabled = true
                        binding.loginButton.text = "Zaloguj"
                    }
                )
            }
        }

        // Przygotuj prompt biometryczny
        val executor: Executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                
                // Check if user is already authenticated
                if (AuthManager.isLoggedIn()) {
                    startActivity(
                        Intent(this@LoginActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                    finish()
                } else {
                    // Try to validate existing token
                    lifecycleScope.launch {
                        val validationResult = AuthManager.validateToken()
                        validationResult.fold(
                            onSuccess = { user ->
                                startActivity(
                                    Intent(this@LoginActivity, MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                )
                                finish()
                            },
                            onFailure = { error ->
                                Toast.makeText(this@LoginActivity, "Brak ważnej sesji. Zaloguj się ponownie.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@LoginActivity, "Błąd biometrii: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@LoginActivity, "Nieudana próba biometrii", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Logowanie biometryczne")
            .setSubtitle("Użyj odcisku palca, aby się zalogować")
            .setNegativeButtonText("Anuluj")
            .build()

        binding.fingerprintButton.setOnClickListener {
            val biometricManager = BiometricManager.from(this)
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, "Biometria niedostępna na tym urządzeniu", Toast.LENGTH_SHORT).show()
            }
        }

        // Obsługa przycisku rejestracji
        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}