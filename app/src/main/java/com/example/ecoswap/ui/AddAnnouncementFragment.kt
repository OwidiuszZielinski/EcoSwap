package com.example.ecoswap.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecoswap.R
import com.example.ecoswap.UserManager
import com.example.ecoswap.ui.apis.RetrofitInstance
import com.example.ecoswap.ui.dto.Category
import com.example.ecoswap.ui.dto.Condition
import com.example.ecoswap.ui.dto.Deal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

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
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerCondition = view.findViewById<Spinner>(R.id.spinnerCondition)
        val etPrice = view.findViewById<EditText>(R.id.etPrice)
        val cbToExchange = view.findViewById<CheckBox>(R.id.cbToExchange)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitAnnouncement)

        // Setup category spinner
        val categories = Category.values().map { it.name.lowercase().replaceFirstChar { it.uppercase() } }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        // Setup condition spinner
        val conditions = Condition.values().map { it.name.lowercase().replaceFirstChar { it.uppercase() } }
        val conditionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, conditions)
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCondition.adapter = conditionAdapter

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
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()
            val category = Category.valueOf(spinnerCategory.selectedItem.toString().uppercase())
            val condition = Condition.valueOf(spinnerCondition.selectedItem.toString().uppercase())
            val price = if (cbToExchange.isChecked) 0.0 else etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val toExchange = cbToExchange.isChecked

            if (imageBitmap == null) {
                Toast.makeText(requireContext(), "Please add a photo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (title.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (description.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!toExchange && price <= 0) {
                Toast.makeText(requireContext(), "Please enter a valid price or select To Exchange", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            val deal = Deal(
                title = title,
                price = price,
                photoDataUrl = base64Image,
                ownerId = UserManager.ownerId,
                description = description,
                category = category,
                condition = condition
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.api.createItem(deal)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Announcement added successfully!", Toast.LENGTH_LONG).show()
                        requireActivity().onBackPressed()
                    }
                } catch (e: Exception) {
                    Log.e("AddAnnouncementFragment", "Error creating announcement", e)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error creating announcement: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
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