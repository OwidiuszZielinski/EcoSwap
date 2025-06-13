package com.example.ecoswap.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ecoswap.R
import com.example.ecoswap.ui.apis.RetrofitInstance
import com.example.ecoswap.ui.dto.Deal
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class AuctionDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auction_details)

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Get deal data from intent
        val deal = intent.getSerializableExtra("deal") as? Deal
        if (deal == null) {
            finish()
            return
        }

        // Set up views
        val ivAuctionImage = findViewById<ImageView>(R.id.ivAuctionImage)
        val tvAuctionTitle = findViewById<TextView>(R.id.tvAuctionTitle)
        val tvAuctionPrice = findViewById<TextView>(R.id.tvAuctionPrice)
        val tvSellerInfo = findViewById<TextView>(R.id.tvSellerInfo)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val btnPlaceBid = findViewById<MaterialButton>(R.id.btnPlaceBid)
        val btnStartChat = findViewById<MaterialButton>(R.id.btnStartChat)

        // Set deal data
        tvAuctionTitle.text = deal.title
        tvAuctionPrice.text = if (deal.price == 0.00) "To exchange" else String.format("%.2f PLN/day", deal.price)
        tvSellerInfo.text = "Added by: ${deal.ownerId}"
        tvDescription.text = deal.description

        try {
            val decodedBytes = Base64.decode(deal.photoDataUrl, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            ivAuctionImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
        }

        // Set up click listeners
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressed()
        }

        btnPlaceBid.setOnClickListener {
            if (deal.price == 0.00) {
                Toast.makeText(this, "Please contact the seller to arrange exchange details", Toast.LENGTH_LONG).show()
            } else {
                showDaysSelectionDialog(deal)
            }
        }

        btnStartChat.setOnClickListener {
            val intent = Intent(this@AuctionDetailsActivity, ChatActivity::class.java)
            intent.putExtra("receiverId", deal.ownerId)
            intent.putExtra("userName", deal.ownerId)
            startActivity(intent)
        }
    }

    private fun showDaysSelectionDialog(deal: Deal) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_days, null)
        val etDays = dialogView.findViewById<EditText>(R.id.etDays)

        AlertDialog.Builder(this)
            .setTitle("Select Rental Period")
            .setView(dialogView)
            .setPositiveButton("Confirm") { _, _ ->
                val days = etDays.text.toString().toIntOrNull()
                if (days != null && days > 0) {
                    val totalPrice = days * deal.price
                    Toast.makeText(this, "Bid placed successfully! Total price: ${String.format("%.2f PLN", totalPrice)}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Please enter a valid number of days", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 