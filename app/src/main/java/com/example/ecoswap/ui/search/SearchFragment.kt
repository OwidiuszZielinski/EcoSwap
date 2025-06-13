package com.example.ecoswap.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecoswap.databinding.FragmentSearchBinding
import android.widget.ArrayAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize category spinner
        val categories = listOf(
            "All categories", "Electronics", "Fashion", "Home", "Garden", "Automotive",
            "Sport", "Baby", "Books", "Animals", "Other"
        )
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = categoryAdapter

        // Location filter (all Polish voivodeships)
        val locations = listOf(
            "All locations",
            "Dolnośląskie", "Kujawsko-Pomorskie", "Lubelskie", "Lubuskie",
            "Łódzkie", "Małopolskie", "Mazowieckie", "Opolskie",
            "Podkarpackie", "Podlaskie", "Pomorskie", "Śląskie",
            "Świętokrzyskie", "Warmińsko-Mazurskie", "Wielkopolskie", "Zachodniopomorskie"
        )
        val locationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSpinner.adapter = locationAdapter

        // Price from filter
        val pricesFrom = listOf("Any", "0", "50", "100", "200", "500", "1000", "2000", "5000")
        val priceFromAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pricesFrom)
        priceFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priceFromSpinner.adapter = priceFromAdapter

        // Price to filter
        val pricesTo = listOf("Any", "50", "100", "200", "500", "1000", "2000", "5000", "10000+")
        val priceToAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pricesTo)
        priceToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priceToSpinner.adapter = priceToAdapter

        // Condition filter
        val conditions = listOf("Any", "New", "Like new", "Used", "Damaged")
        val conditionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, conditions)
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.conditionSpinner.adapter = conditionAdapter

        // Sort by filter
        val sortOptions = listOf("Newest", "Cheapest", "Most expensive")
        val sortAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sortSpinner.adapter = sortAdapter

        // TODO: Implement search and sorting functionality
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}