package com.example.ecoswap.ui

import com.example.ecoswap.MainActivity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoswap.R
import com.example.ecoswap.ui.dto.Deal
import com.example.ecoswap.ui.dto.FavoritesManager
import com.example.ecoswap.ui.dto.AppNotifications
import com.example.ecoswap.ui.dto.NotificationItem

class DealsAdapter(
    private val deals: List<Deal>
) : RecyclerView.Adapter<DealsAdapter.DealViewHolder>() {

    inner class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgDeal: ImageView = itemView.findViewById(R.id.imgDeal)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvUserInfo: TextView = itemView.findViewById(R.id.tvUserInfo)
        val cardRoot: View = itemView.findViewById(R.id.card_root)
        val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deal, parent, false)
        return DealViewHolder(view)
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal = deals[position]
        holder.tvTitle.text = deal.title
        if (deal.price == 0.00) {
            holder.tvPrice.text = "To exchange"
        } else {
            holder.tvPrice.text = String.format("%.2f PLN/day", deal.price)
        }
        
        holder.tvUserInfo.text = "Added by: ${deal.ownerId}"

        try {
            val decodedBytes = Base64.decode(deal.photoDataUrl, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            holder.imgDeal.setImageBitmap(bitmap)
        } catch (e: Exception) {
            // Handle image loading error
        }

        // Obsługa serduszka
        if (FavoritesManager.isFavorite(deal)) {
            holder.btnFavorite.setImageResource(R.drawable.ic_favorite)
            holder.btnFavorite.setColorFilter(Color.RED)
        } else {
            holder.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
            holder.btnFavorite.setColorFilter(Color.GRAY)
        }
        holder.btnFavorite.setOnClickListener {
            if (FavoritesManager.isFavorite(deal)) {
                FavoritesManager.removeFavorite(holder.itemView.context, deal)
            } else {
                FavoritesManager.addFavorite(holder.itemView.context, deal)
                AppNotifications.addNotification(
                    NotificationItem(
                        title = "Added to favorites",
                        message = "\"${deal.title}\" has been added to your favorites."
                    )
                )
                // Update notification badge and icon
                (holder.itemView.context as? MainActivity)?.updateNotificationBadge()
            }
            notifyItemChanged(position)
        }

        holder.cardRoot.setOnClickListener {
            val intent = Intent(holder.itemView.context, AuctionDetailsActivity::class.java)
            intent.putExtra("deal", deal)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = deals.size
}