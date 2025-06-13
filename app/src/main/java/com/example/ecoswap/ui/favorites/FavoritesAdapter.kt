package com.example.ecoswap.ui.favorites

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoswap.R
import com.example.ecoswap.databinding.ItemFavoriteBinding
import com.example.ecoswap.ui.dto.Deal
import com.example.ecoswap.ui.dto.FavoritesManager

class FavoritesAdapter(
    private val onFavoriteClick: (Deal) -> Unit
) : ListAdapter<Deal, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(deal: Deal) {
            binding.tvTitle.text = deal.title
            binding.tvDescription.text = deal.description
            // Obsługa obrazka
            try {
                val decodedBytes = Base64.decode(deal.photoDataUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                binding.ivItemImage.setImageBitmap(bitmap)
            } catch (e: Exception) {}
            // Obsługa serduszka
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
            binding.btnFavorite.setColorFilter(Color.RED)
            binding.btnFavorite.setOnClickListener {
                onFavoriteClick(deal)
            }
        }
    }
}

class FavoriteDiffCallback : DiffUtil.ItemCallback<Deal>() {
    override fun areItemsTheSame(oldItem: Deal, newItem: Deal): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Deal, newItem: Deal): Boolean {
        return oldItem == newItem
    }
} 