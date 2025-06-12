package com.example.ecoswap.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecoswap.R

class AddAnnouncementFragment : Fragment() {
    private var imageBitmap: Bitmap? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                this.imageBitmap = imageBitmap
                view?.findViewById<ImageView>(R.id.ivPreview)?.setImageBitmap(imageBitmap)
            }
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_announcement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnTakePhoto = view.findViewById<Button>(R.id.btnTakePhoto)
        val ivPreview = view.findViewById<ImageView>(R.id.ivPreview)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val etPrice = view.findViewById<EditText>(R.id.etPrice)
        val cbToExchange = view.findViewById<CheckBox>(R.id.cbToExchange)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitAnnouncement)

        // Example categories
        val categories = listOf("Electronics", "Fashion", "Home", "Sports", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.d("AddAnnouncementFragment", "Camera permission granted")
                openCamera()
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        cbToExchange.setOnCheckedChangeListener { _, isChecked ->
            etPrice.isEnabled = !isChecked
            if (isChecked) etPrice.setText("")
        }

        btnSubmit.setOnClickListener {
            val description = etDescription.text.toString()
            val category = spinnerCategory.selectedItem.toString()
            val price = etPrice.text.toString()
            val toExchange = cbToExchange.isChecked
            if (imageBitmap == null) {
                Toast.makeText(requireContext(), "Please add a photo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (description.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!toExchange && price.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a price or select To Exchange", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(requireContext(), "Announcement added! (stub)", Toast.LENGTH_LONG).show()
        }
    }

    private fun openCamera() {
        Log.d("AddAnnouncementFragment", "Opening camera")
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            takePictureLauncher.launch(takePictureIntent)
        }
    }
} 