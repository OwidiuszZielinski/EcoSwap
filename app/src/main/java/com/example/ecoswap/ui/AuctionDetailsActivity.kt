package com.example.ecoswap.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ecoswap.R
import com.example.ecoswap.ui.dto.Deal
import com.google.android.material.button.MaterialButton

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
        val btnPlaceBid = findViewById<MaterialButton>(R.id.btnPlaceBid)
        val btnStartChat = findViewById<MaterialButton>(R.id.btnStartChat)

        // Set deal data
        tvAuctionTitle.text = deal.title
        tvAuctionPrice.text = if (deal.price == 0.00) "To exchange" else String.format("%.2f PLN/day", deal.price)
        tvSellerInfo.text = "Added by: ${deal.ownerId}"

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
            // TODO: Implement bid placement logic
        }

        btnStartChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 