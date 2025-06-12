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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecoswap.databinding.FragmentAwardsBinding
import kotlin.random.Random

class AwardsFragment : Fragment() {

    private var _binding: FragmentAwardsBinding? = null
    private val binding get() = _binding!!
    
    private var loyaltyPoints = 200
    private val boxCost = 100
    private val possibleRewards = listOf(
        "10% discount on next rental",
        "Free delivery",
        "24h premium membership",
        "50 bonus points",
        "Special badge"
    )

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
        updateLoyaltyPointsDisplay()
    }

    private fun setupRecyclerView() {
        binding.awardsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // TODO: Implement awards adapter
    }

    private fun setupRewardBox() {
        binding.btnOpenBox.setOnClickListener {
            if (loyaltyPoints >= boxCost) {
                loyaltyPoints -= boxCost
                updateLoyaltyPointsDisplay()
                animateBoxOpening()
            } else {
                Toast.makeText(requireContext(), "Not enough points!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLoyaltyPointsDisplay() {
        binding.tvLoyaltyPoints.text = "Loyalty Points: $loyaltyPoints"
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
        val reward = possibleRewards.random()
        Toast.makeText(requireContext(), "Congratulations! You won: $reward", Toast.LENGTH_LONG).show()
        
        // Add some random points as a bonus
        val bonusPoints = Random.nextInt(10, 31)
        loyaltyPoints += bonusPoints
        updateLoyaltyPointsDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 