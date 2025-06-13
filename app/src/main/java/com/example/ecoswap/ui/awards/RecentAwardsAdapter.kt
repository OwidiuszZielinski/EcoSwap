package com.example.ecoswap.ui.awards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoswap.databinding.ItemRecentAwardBinding
import java.text.SimpleDateFormat
import java.util.*

data class RecentAward(
    val id: String,
    val title: String,
    val winnerName: String,
    val date: Date
)

class RecentAwardsAdapter : ListAdapter<RecentAward, RecentAwardsAdapter.AwardViewHolder>(AwardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AwardViewHolder {
        val binding = ItemRecentAwardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AwardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AwardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AwardViewHolder(
        private val binding: ItemRecentAwardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
        
        fun bind(award: RecentAward) {
            binding.tvAwardTitle.text = award.title
            binding.tvWinnerName.text = "Won by ${award.winnerName}"
            binding.tvAwardDate.text = getTimeAgo(award.date)
        }

        private fun getTimeAgo(date: Date): String {
            val now = System.currentTimeMillis()
            val diff = now - date.time

            return when {
                diff < 60 * 1000 -> "just now"
                diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
                diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
                else -> dateFormat.format(date)
            }
        }
    }
}

class AwardDiffCallback : DiffUtil.ItemCallback<RecentAward>() {
    override fun areItemsTheSame(oldItem: RecentAward, newItem: RecentAward): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RecentAward, newItem: RecentAward): Boolean {
        return oldItem == newItem
    }
} 