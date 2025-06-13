package com.example.ecoswap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.ecoswap.databinding.ActivityLoginBinding
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is already logged in
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("is_logged_in", false)) {
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

            // Pobierz zapisane dane
            val savedEmail = prefs.getString("email", null)
            val savedPassword = prefs.getString("password", null)

            if (email == savedEmail && password == savedPassword) {
                // Save login state if "Remember Me" is checked
                if (binding.switchRememberMe.isChecked) {
                    prefs.edit().putBoolean("is_logged_in", true).apply()
                }
                
                startActivity(
                    Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            } else {
                Toast.makeText(this, "Nieprawidłowy email lub hasło", Toast.LENGTH_SHORT).show()
            }
        }

        // Przygotuj prompt biometryczny
        val executor: Executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // Save login state if "Remember Me" is checked
                if (binding.switchRememberMe.isChecked) {
                    prefs.edit().putBoolean("is_logged_in", true).apply()
                }
                startActivity(
                    Intent(this@LoginActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
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