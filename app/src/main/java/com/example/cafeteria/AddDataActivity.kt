package com.example.cafeteria

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cafeteria.databinding.ActivityAddDataBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class AddDataActivity : AppCompatActivity() {

    private val appwriteManager by lazy { AppwriteManager.getInstance(applicationContext) }
    private val database = FirebaseDatabase.getInstance().reference

    private var selectedImageUri: Uri? = null

    private lateinit var etName: TextInputEditText
    private lateinit var etPrice: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var ivPreview: ImageView
    private lateinit var binding: ActivityAddDataBinding

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            ivPreview.visibility = View.VISIBLE
            ivPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etName = findViewById(R.id.etFoodName)
        etPrice = findViewById(R.id.etFoodPrice)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        ivPreview = findViewById(R.id.ivPreview)

        // Set up the Spinner with your requested categories
        val categories = arrayOf("Breakfast", "Starters", "Main_course", "Desserts", "Beverages")
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        findViewById<Button>(R.id.btnSelectImage).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            uploadAndSaveData()
        }
    }

    private fun uploadAndSaveData() {
        val name = etName.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString() // This will be the child node

        if (name.isEmpty() || price.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Show a progress indicator if you have one
        findViewById<Button>(R.id.btnSubmit).isEnabled = false

        lifecycleScope.launch {
            try {
                // 1. Upload to Appwrite and get the URL
                val imageUrl = appwriteManager.uploadImageFromUri(selectedImageUri!!)

                // 2. Prepare the object
                val foodItem = mapOf(
                    "name" to name,
                    "price" to price,
                    "imageUrl" to imageUrl
                )

                // 3. Save to Realtime Database: items -> Category -> (RandomID) -> Data
                database.child("items").child(category).push().setValue(foodItem)
                    .addOnSuccessListener {
                        Toast.makeText(this@AddDataActivity, "Item added successfully!", Toast.LENGTH_SHORT).show()
                        finish() // Go back after success
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@AddDataActivity, "Firebase Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        findViewById<Button>(R.id.btnSubmit).isEnabled = true
                    }

            } catch (e: Exception) {
                Toast.makeText(this@AddDataActivity, "Upload Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                findViewById<Button>(R.id.btnSubmit).isEnabled = true
            }
        }
    }
}