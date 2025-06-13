package com.example.ecoswap.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoswap.R
import com.example.ecoswap.ui.dto.Message
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MessagesAdapter(
    private val messages: List<Message>
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSender: TextView = view.findViewById(R.id.tvSender)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.tvSender.text = "From: ${message.senderId}"
        holder.tvContent.text = message.content
        
        // Convert Instant to LocalDateTime and format it
        val localDateTime = LocalDateTime.ofInstant(
            message.timestamp,
            ZoneId.systemDefault()
        )
        holder.tvTimestamp.text = localDateTime.format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        )
    }

    override fun getItemCount() = messages.size
} 