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
import com.example.ecoswap.ui.dto.Deal
import com.example.ecoswap.ui.dto.FavoritesManager
import com.example.ecoswap.ui.apis.RetrofitInstance

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
        favoritesAdapter = FavoritesAdapter { deal ->
            FavoritesManager.removeFavorite(requireContext(), deal)
            loadFavorites()
        }
        binding.rvFavorites.adapter = favoritesAdapter
    }

    private fun loadFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val allDeals = RetrofitInstance.api.getBestDeals() // lub inna metoda pobierania wszystkich ogłoszeń
                val favoriteDeals = allDeals.filter { FavoritesManager.isFavorite(it) }
                favoritesAdapter.submitList(favoriteDeals)
                updateEmptyState()
            } catch (e: Exception) {
                favoritesAdapter.submitList(emptyList())
                updateEmptyState()
            }
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