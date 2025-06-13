package com.example.ecoswap.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoswap.R
import com.example.ecoswap.UserManager
import com.example.ecoswap.databinding.ActivityChatBinding
import com.example.ecoswap.ui.apis.RetrofitInstance
import com.example.ecoswap.ui.dto.Message
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private var receiverId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Get user data from intent
        receiverId = intent.getStringExtra("receiverId") ?: "rnowak"
        val userName = intent.getStringExtra("userName") ?: "User"
        supportActionBar?.title = "Chat with $userName"

        setupRecyclerView()
        setupMessageSending()
        loadMessages()
    }

    private fun setupRecyclerView() {
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        messagesAdapter = MessagesAdapter(emptyList())
        binding.rvMessages.adapter = messagesAdapter
    }

    private fun setupMessageSending() {
        binding.btnSend.setOnClickListener {
            val messageContent = binding.etMessage.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                sendMessage(messageContent)
                binding.etMessage.text?.clear()
            }
        }
    }

    private fun sendMessage(content: String) {
        lifecycleScope.launch {
            try {
                val message = Message(
                    senderId = UserManager.ownerId,
                    receiverId = receiverId,
                    content = content,
                )
                val response = RetrofitInstance.api.sendMessage(message)
                loadMessages() // Reload messages after sending
            } catch (e: Exception) {
                Log.e("ChatActivity", "Error sending message", e)
                Toast.makeText(this@ChatActivity, "Error sending message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMessages() {
        lifecycleScope.launch {
            try {
                val receivedMessages = RetrofitInstance.api.getReceivedMessages(UserManager.ownerId)
                val sentMessages = RetrofitInstance.api.getSentMessages(UserManager.ownerId)
                
                // Filter messages for this conversation
                val conversationMessages = (receivedMessages + sentMessages)
                    .filter { 
                        (it.senderId == UserManager.ownerId && it.receiverId == receiverId) ||
                        (it.senderId == receiverId && it.receiverId == UserManager.ownerId)
                    }
                    .sortedBy { it.timestamp }
                
                messagesAdapter = MessagesAdapter(conversationMessages)
                binding.rvMessages.adapter = messagesAdapter
                binding.rvMessages.scrollToPosition(conversationMessages.size - 1)
            } catch (e: Exception) {
                Log.e("ChatActivity", "Error loading messages", e)
                Toast.makeText(this@ChatActivity, "Error loading messages", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 