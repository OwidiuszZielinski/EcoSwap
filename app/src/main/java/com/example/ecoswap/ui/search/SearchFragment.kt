package com.example.ecoswap.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecoswap.databinding.FragmentSearchBinding
import android.widget.SeekBar
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

        // Price filter
        binding.priceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.priceLabel.text = "Price: $progress"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
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

        // Delivery options
        val deliveryOptions = listOf("Any", "Pickup", "Courier", "In-person")
        val deliveryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, deliveryOptions)
        deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.deliverySpinner.adapter = deliveryAdapter

        // Negotiable price
        val negotiableOptions = listOf("Any", "Yes", "No")
        val negotiableAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, negotiableOptions)
        negotiableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.negotiableSpinner.adapter = negotiableAdapter

        // Listing type
        val listingTypes = listOf("Any", "For sale", "For exchange", "For free")
        val listingTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listingTypes)
        listingTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.listingTypeSpinner.adapter = listingTypeAdapter

        // TODO: Implement search and sorting functionality
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}