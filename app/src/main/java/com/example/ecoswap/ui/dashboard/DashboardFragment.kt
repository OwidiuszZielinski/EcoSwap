package com.example.ecoswap.ui.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecoswap.R
import com.example.ecoswap.UserManager
import com.example.ecoswap.databinding.FragmentDashboardBinding
import com.example.ecoswap.ui.MyAuctionsAdapter
import com.example.ecoswap.ui.apis.RetrofitInstance
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var auctionsAdapter: MyAuctionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadUserAuctions()
    }

    private fun setupRecyclerView() {
        binding.rvMyAuctions.layoutManager = LinearLayoutManager(requireContext())
        auctionsAdapter = MyAuctionsAdapter(
            emptyList(),
            onEditClick = { deal ->
                // TODO: Implement edit functionality
                Toast.makeText(requireContext(), "Edit functionality coming soon", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { deal ->
                showDeleteConfirmationDialog(deal)
            }
        )
        binding.rvMyAuctions.adapter = auctionsAdapter
    }

    private fun loadUserAuctions() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userAuctions = RetrofitInstance.api.getByOwnerId(UserManager.ownerId)
                if (userAuctions.isEmpty()) {
                    binding.tvNoAuctions.visibility = View.VISIBLE
                    binding.rvMyAuctions.visibility = View.GONE
                } else {
                    binding.tvNoAuctions.visibility = View.GONE
                    binding.rvMyAuctions.visibility = View.VISIBLE
                    auctionsAdapter = MyAuctionsAdapter(
                        userAuctions,
                        onEditClick = { deal ->
                            // TODO: Implement edit functionality
                            Toast.makeText(requireContext(), "Edit functionality coming soon", Toast.LENGTH_SHORT).show()
                        },
                        onDeleteClick = { deal ->
                            showDeleteConfirmationDialog(deal)
                        }
                    )
                    binding.rvMyAuctions.adapter = auctionsAdapter
                }
            } catch (e: Exception) {
                Log.e("DashboardFragment", "Error loading user auctions", e)
                Toast.makeText(requireContext(), "Error loading auctions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(deal: com.example.ecoswap.ui.dto.Deal) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_auction)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                deleteAuction(deal)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun deleteAuction(deal: com.example.ecoswap.ui.dto.Deal) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.deleteItem(deal.id)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), R.string.auction_deleted, Toast.LENGTH_SHORT).show()
                    loadUserAuctions() // Reload the list after deletion
                } else {
                    Toast.makeText(requireContext(), R.string.error_deleting_auction, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DashboardFragment", "Error deleting auction", e)
                Toast.makeText(requireContext(), R.string.error_deleting_auction, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}