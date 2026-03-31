package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteria.databinding.HomeBinding
import com.google.firebase.auth.FirebaseAuth

class
HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeBinding
    private val categories = arrayOf("Breakfast", "Starters", "Main Course", "Dessert")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using ViewBinding to initialize layout
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle System Bar Insets (ViewCompat)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set User Greeting
        val username = intent.getStringExtra("USERNAME") ?: "User"
        binding.greetingText.text = "Hi $username!"

        // Admin Card Click Listener
        binding.adminCard.setOnClickListener { view ->
            showAdminMenu(view)
        }

        // Checkout Button Click
        binding.checkoutButton.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        setupCategoryClicks()

        binding.logoutbtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showAdminMenu(view: android.view.View) {
        val popup = PopupMenu(this, view)

        // 1. Added the Inventory option here
        popup.menu.add("Add New Item")
        popup.menu.add("View Admin Dashboard")
        popup.menu.add("Inventory")
        popup.menu.add("Order History")

        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Add New Item" -> {
                    startActivity(Intent(this, AddDataActivity::class.java))
                    true
                }
                "View Admin Dashboard" -> {
                    startActivity(Intent(this, AdminViewActivity::class.java))
                    true
                }
                // 2. Navigation to Inventory Activity
                "Inventory" -> {
                    startActivity(Intent(this, InventoryActivity::class.java))
                    true
                }
                "Order History" -> {
                    startActivity(Intent(this, OrderActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun setupCategoryClicks() {
        // Using binding.categoryGrid directly
        for (i in 0 until binding.categoryGrid.childCount) {
            val categoryItem = binding.categoryGrid.getChildAt(i)
            categoryItem.setOnClickListener {
                if (i < categories.size) {
                    navigateToCategory(categories[i])
                }
            }
        }
    }

    private fun navigateToCategory(category: String) {
        val intent = Intent(this, ViewItemsActivity::class.java)
        val categoryId = when (category) {
            "Main Course" -> "Main_course"
            "Dessert" -> "Desserts"
            else -> category
        }
        intent.putExtra("CATEGORY_ID", categoryId)
        startActivity(intent)
    }
}