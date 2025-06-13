package com.example.ecoswap.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.ecoswap.R
import com.example.ecoswap.databinding.FragmentFavoritesBinding
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadFavorites()
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoritesAdapter()
        favoritesAdapter.setOnRemoveClickListener { item ->
            removeFromFavorites(item)
        }
        binding.rvFavorites.adapter = favoritesAdapter
    }

    private fun loadFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            // TODO: Load favorites from database/API
            // For now, show some sample data
            val sampleItems = listOf(
                FavoriteItem(
                    id = "1",
                    title = "Sample Item 1",
                    description = "This is a sample favorite item"
                ),
                FavoriteItem(
                    id = "2",
                    title = "Sample Item 2",
                    description = "Another sample favorite item"
                )
            )
            favoritesAdapter.submitList(sampleItems)
            updateEmptyState()
        }
    }

    private fun removeFromFavorites(item: FavoriteItem) {
        viewLifecycleOwner.lifecycleScope.launch {
            // TODO: Remove from database/API
            val currentList = favoritesAdapter.currentList.toMutableList()
            currentList.remove(item)
            favoritesAdapter.submitList(currentList)
            updateEmptyState()
        }
    }

    private fun updateEmptyState() {
        binding.tvEmptyState.visibility = if (favoritesAdapter.itemCount == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 