package com.example.ecoswap.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecoswap.MainActivity
import com.example.ecoswap.UserManager
import com.example.ecoswap.databinding.FragmentNotificationsBinding
import com.example.ecoswap.ui.MessagesAdapter
import com.example.ecoswap.ui.apis.RetrofitInstance
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadMessages()
        
        // Remove notification badge when entering notifications
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.hasUnreadMessages = false
            mainActivity.updateNotificationBadge()
            mainActivity.hideBottomNavigation()
        }
    }

    private fun setupRecyclerView() {
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMessages.setPadding(0, 0, 0, 100)
    }

    private fun loadMessages() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val receivedMessages = RetrofitInstance.api.getReceivedMessages(UserManager.ownerId)
                if (receivedMessages.isEmpty()) {
                    binding.tvNoMessages.visibility = View.VISIBLE
                    binding.rvMessages.visibility = View.GONE
                } else {
                    binding.tvNoMessages.visibility = View.GONE
                    binding.rvMessages.visibility = View.VISIBLE
                    binding.rvMessages.adapter = MessagesAdapter(receivedMessages)
                    
                    // Mark unread messages as read
                    receivedMessages.filter { !it.read }.forEach { message ->
                        message.id?.let { messageId ->
                            try {
                                RetrofitInstance.api.markMessageAsRead(messageId)
                            } catch (e: Exception) {
                                Log.e("NotificationsFragment", "Error marking message as read", e)
                            }
                        }
                    }
                    (activity as? MainActivity)?.resetMessageCounter()
                }
            } catch (e: Exception) {
                Log.e("NotificationsFragment", "Error loading messages", e)
                Toast.makeText(requireContext(), "Error loading messages", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadMessages() // Reload messages when returning to this fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.showBottomNavigation()
        }
        _binding = null
    }
}