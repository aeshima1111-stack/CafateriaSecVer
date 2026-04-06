package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteria.databinding.HomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class
HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeBinding
    private val categories = arrayOf("Breakfast", "Starters", "Main Course", "Dessert" ,"Beverages")

    private val allFoodItems = mutableListOf<FoodItem>()


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

        fetchAllItems()

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                val query = p0.toString().lowercase().trim()
                if (query.isEmpty()) {
                    binding.dropdownContainer.visibility = View.GONE
            } else{
                    binding.dropdownContainer.visibility = View.VISIBLE
                    updateDropdown(query)
            }

            }
        })


        // Checkout Button Click
        binding.checkoutButton.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        setupCategoryClicks()

        binding.profileorder.setOnClickListener {
            startActivity(Intent(this, profileActivity::class.java))
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

    private fun fetchAllItems() {
        val menuref = FirebaseDatabase.getInstance().getReference("items")
        menuref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allFoodItems.clear()
                for (categorySnapshot in snapshot.children) {
                    val categoryName = categorySnapshot.key ?: ""
                    for (itemSnapshot in categorySnapshot.children) {
                        val item = itemSnapshot.getValue(FoodItem::class.java)
                        if (item != null) {
                            // Store the item with its category info
                            item.category = categoryName
                            allFoodItems.add(item)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    private fun updateDropdown(query: String) {
        binding.dropdownContainer.removeAllViews()
        val matches = allFoodItems.filter { it.name?.lowercase()?.contains(query) == true }.take(5)

        if (matches.isEmpty()) {
            val tv = TextView(this@HomeActivity).apply {
                text = "No items found"; setPadding(30, 20, 30, 20)
            }
            binding.dropdownContainer.addView(tv)
            return
        }

        for (item in matches) {
            val itemView = TextView(this@HomeActivity).apply {
                text = "${item.name} (in ${item.category})"
                textSize = 16f
                setPadding(40, 30, 40, 30)
                setBackgroundResource(android.R.drawable.list_selector_background)
                setOnClickListener {
                    // Send to ViewItemsActivity with the search query
                    val intent = Intent(this@HomeActivity, ViewItemsActivity::class.java)
                    intent.putExtra("CATEGORY_ID", item.category)
                    intent.putExtra("SEARCH_QUERY", item.name)
                    startActivity(intent)
                    binding.dropdownContainer.visibility = View.GONE
                    binding.searchEditText.setText("")
                }
            }
            binding.dropdownContainer.addView(itemView)
        }
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