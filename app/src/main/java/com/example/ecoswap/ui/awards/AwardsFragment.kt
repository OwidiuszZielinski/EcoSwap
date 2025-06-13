package com.example.ecoswap.ui.awards

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecoswap.databinding.FragmentAwardsBinding
import com.example.ecoswap.data.repository.AwardRepository
import com.example.ecoswap.data.db.AppDatabase
import kotlinx.coroutines.launch

class AwardsFragment : Fragment() {

    private var _binding: FragmentAwardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recentAwardsAdapter: RecentAwardsAdapter

    private val viewModel: AwardsViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val repository = AwardRepository(database.awardDao())
        AwardsViewModel.Factory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAwardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupRewardBox()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.awardsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recentAwardsAdapter = RecentAwardsAdapter()
        binding.awardsRecyclerView.adapter = recentAwardsAdapter
    }

    private fun setupRewardBox() {
        binding.btnOpenBox.setOnClickListener {
            if (viewModel.loyaltyPoints.value >= 100) {
                animateBoxOpening()
            } else {
                Toast.makeText(requireContext(), "Not enough points!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loyaltyPoints.collect { points ->
                        binding.tvLoyaltyPoints.text = "Loyalty Points: $points"
                    }
                }
                launch {
                    viewModel.recentAwards.collect { awards ->
                        recentAwardsAdapter.submitList(awards)
                    }
                }
            }
        }
    }

    private fun animateBoxOpening() {
        val boxCard = binding.rewardBoxCard
        val boxImage = binding.ivRewardBox

        // Scale down animation
        val scaleDownX = ObjectAnimator.ofFloat(boxCard, "scaleX", 1f, 0.8f)
        val scaleDownY = ObjectAnimator.ofFloat(boxCard, "scaleY", 1f, 0.8f)
        
        // Rotation animation
        val rotate = ObjectAnimator.ofFloat(boxCard, "rotation", 0f, 360f)
        
        // Scale up animation
        val scaleUpX = ObjectAnimator.ofFloat(boxCard, "scaleX", 0.8f, 1f)
        val scaleUpY = ObjectAnimator.ofFloat(boxCard, "scaleY", 0.8f, 1f)

        // Create animation set
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleDownX, scaleDownY, rotate, scaleUpX, scaleUpY)
        animatorSet.duration = 1000
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        
        animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                showReward()
            }
        })
        
        animatorSet.start()
    }

    private fun showReward() {
        val reward = viewModel.openRewardBox()
        if (reward != null) {
            Toast.makeText(requireContext(), "Congratulations! You won: $reward", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 