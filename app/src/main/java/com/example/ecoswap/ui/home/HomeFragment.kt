package com.example.ecoswap.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecoswap.databinding.FragmentHomeBinding
import com.example.ecoswap.ui.DealsAdapter
import com.example.ecoswap.ui.apis.RetrofitInstance
import com.example.ecoswap.ui.dto.Deal
import kotlinx.coroutines.launch

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

        binding.rvDeals.layoutManager = LinearLayoutManager(requireContext())
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
                Log.e("HomeFragment", "Błąd pobierania best deals", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}