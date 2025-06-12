package com.example.ecoswap.ui

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoswap.R
import com.example.ecoswap.ui.dto.Deal

class MyAuctionsAdapter(
    private val deals: List<Deal>,
    private val onEditClick: (Deal) -> Unit,
    private val onDeleteClick: (Deal) -> Unit
) : RecyclerView.Adapter<MyAuctionsAdapter.AuctionViewHolder>() {

    inner class AuctionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAuction: ImageView = itemView.findViewById(R.id.imgAuction)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuctionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_auction, parent, false)
        return AuctionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AuctionViewHolder, position: Int) {
        val deal = deals[position]
        holder.tvTitle.text = deal.title
        holder.tvPrice.text = if (deal.price == 0.00) "To exchange" else String.format("%.2f PLN/day", deal.price)
        holder.tvStatus.text = "Active" // You might want to add a status field to your Deal class

        try {
            val decodedBytes = Base64.decode(deal.photoDataUrl, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            holder.imgAuction.setImageBitmap(bitmap)
        } catch (e: Exception) {
            // Handle image loading error
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(deal)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(deal)
        }
    }

    override fun getItemCount(): Int = deals.size
} 