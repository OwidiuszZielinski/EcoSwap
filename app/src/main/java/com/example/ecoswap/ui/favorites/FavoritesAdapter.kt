package com.example.ecoswap.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoswap.databinding.ItemFavoriteBinding

class FavoritesAdapter : ListAdapter<FavoriteItem, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    private var onRemoveClickListener: ((FavoriteItem) -> Unit)? = null

    fun setOnRemoveClickListener(listener: (FavoriteItem) -> Unit) {
        onRemoveClickListener = listener
    }

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
        
        fun bind(item: FavoriteItem) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            // TODO: Load image using Glide or Coil

            binding.btnRemove.setOnClickListener {
                onRemoveClickListener?.invoke(item)
            }
        }
    }
}

class FavoriteDiffCallback : DiffUtil.ItemCallback<FavoriteItem>() {
    override fun areItemsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
        return oldItem == newItem
    }
}

data class FavoriteItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null
) 