package com.example.ecoswap.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ecoswap.databinding.FragmentEditDealBinding
import com.example.ecoswap.ui.apis.RetrofitInstance
import com.example.ecoswap.ui.dto.Deal
import kotlinx.coroutines.launch

class EditDealFragment : Fragment() {
    private var _binding: FragmentEditDealBinding? = null
    private val binding get() = _binding!!
    private lateinit var deal: Deal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            deal = it.getSerializable("deal") as Deal
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etTitle.setText(deal.title)
        binding.switchExchange.isChecked = (deal.price == 0.0)
        binding.etPrice.setText(if (deal.price == 0.0) "" else deal.price.toString())

        binding.switchExchange.setOnCheckedChangeListener { _, isChecked ->
            binding.etPrice.isEnabled = !isChecked
            if (isChecked) binding.etPrice.setText("")
        }

        binding.btnSave.setOnClickListener {
            val updatedDeal = deal.copy(
                title = binding.etTitle.text.toString(),
                price = if (binding.switchExchange.isChecked) 0.0 else binding.etPrice.text.toString().toDoubleOrNull() ?: deal.price
            )
            updateDealOnBackend(updatedDeal)
        }
    }

    private fun updateDealOnBackend(updatedDeal: Deal) {
        lifecycleScope.launch {
            try {
                RetrofitInstance.api.updateItem(updatedDeal.id, updatedDeal)
                Toast.makeText(requireContext(), "Zaktualizowano!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(deal: Deal): EditDealFragment {
            val fragment = EditDealFragment()
            val args = Bundle()
            args.putSerializable("deal", deal)
            fragment.arguments = args
            return fragment
        }
    }
} 