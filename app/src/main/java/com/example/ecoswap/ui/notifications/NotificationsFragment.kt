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
import com.example.ecoswap.ui.dto.AppNotifications
import com.example.ecoswap.ui.dto.NotificationItem
import androidx.recyclerview.widget.RecyclerView

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationsAdapter: NotificationsAdapter

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
        loadNotifications()
        
        // Mark notifications as read when entering notifications
        AppNotifications.markAllAsRead()
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.updateNotificationBadge()
            mainActivity.hideBottomNavigation()
        }
    }

    private fun setupRecyclerView() {
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
        notificationsAdapter = NotificationsAdapter()
        binding.rvMessages.adapter = notificationsAdapter
        binding.rvMessages.setPadding(0, 0, 0, 100)
    }

    private fun loadNotifications() {
        val notifications = AppNotifications.getNotifications()
        notificationsAdapter.submitList(notifications)
        binding.tvNoMessages.visibility = if (notifications.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        loadNotifications() // Reload notifications when returning to this fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.showBottomNavigation()
        }
        _binding = null
    }
}