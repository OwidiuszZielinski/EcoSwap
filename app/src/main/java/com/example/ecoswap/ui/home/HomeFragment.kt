package com.example.ecoswap.ui.home

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
import com.example.ecoswap.databinding.FragmentHomeBinding
import com.example.ecoswap.ui.DealsAdapter
import com.example.ecoswap.ui.apis.RetrofitInstance
import com.example.ecoswap.ui.dto.Deal
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dealsAdapter: DealsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDeals.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        dealsAdapter = DealsAdapter(emptyList())
        binding.rvDeals.adapter = dealsAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val items: List<Deal> = RetrofitInstance.api.getBestDeals()
                if (items.isNotEmpty()) {
                    dealsAdapter = DealsAdapter(items)
                    binding.rvDeals.adapter = dealsAdapter
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error fetching best deals", e)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}