package com.example.ecoswap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecoswap.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obsługa przycisku rejestracji
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            // Prosta walidacja pól
            if (email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                // Zapisz dane do SharedPreferences
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                prefs.edit()
                    .putString("email", email)
                    .putString("password", password)
                    .apply()

                Toast.makeText(this, "Rejestracja zakończona sukcesem!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                // Błąd walidacji
                Toast.makeText(this, "Uzupełnij poprawnie wszystkie pola!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}