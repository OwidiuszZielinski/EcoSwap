package com.example.ecoswap.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.ecoswap.LoginActivity
import com.example.ecoswap.UserManager
import com.example.ecoswap.databinding.FragmentSettingsBinding
import com.example.ecoswap.ui.ChatActivity
import com.example.ecoswap.auth.AuthManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.switchTheme.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        // Display current user info
        val currentUser = UserManager.getCurrentUser()
        if (currentUser != null) {
            binding.tvUserInfo.text = "Zalogowano jako: ${currentUser.firstName} ${currentUser.lastName} (${currentUser.email})"
        } else {
            binding.tvUserInfo.text = "Zalogowano jako: ${UserManager.ownerId}"
        }

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                val result = AuthManager.logout()
                result.fold(
                    onSuccess = {
                        Toast.makeText(requireContext(), "Wylogowano pomyślnie", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        requireActivity().finish()
                    },
                    onFailure = { error ->
                        Toast.makeText(requireContext(), "Błąd wylogowania: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        binding.btnStartChat.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            val userId = UserManager.getCurrentUserId() ?: UserManager.ownerId
            val userName = currentUser?.username ?: UserManager.ownerId
            intent.putExtra("receiverId", userId)
            intent.putExtra("userName", userName)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 