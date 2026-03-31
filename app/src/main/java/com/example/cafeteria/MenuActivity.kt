package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var categoryTitle: TextView
    private lateinit var btnBreakfast: Button
    private lateinit var btnStarters: Button
    private lateinit var btnMainCourse: Button
    private lateinit var btnDessert: Button
    private lateinit var btnBeverages: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_page)

        val category = intent.getStringExtra("CATEGORY") ?: "Our"
        categoryTitle = findViewById(R.id.categoryTitle)
        categoryTitle.text = "$category Menu"

        initializeButtons()
    }

    private fun initializeButtons() {
        btnBreakfast = findViewById(R.id.btnBreakfast)
        btnStarters = findViewById(R.id.btnStarters)
        btnMainCourse = findViewById(R.id.btnMainCourse)
        btnDessert = findViewById(R.id.btnDessert)
        btnBeverages = findViewById(R.id.btnBeverages)

        // All buttons now point to the SAME ViewItemsActivity
        // but pass a DIFFERENT "CATEGORY_ID" string.

        btnBreakfast.setOnClickListener {
            navigateToCategory("Breakfast")
        }

        btnStarters.setOnClickListener {
            navigateToCategory("Starters")
        }

        btnMainCourse.setOnClickListener {
            navigateToCategory("Main_course")
        }

        btnDessert.setOnClickListener {
            navigateToCategory("Desserts")
        }

        btnBeverages.setOnClickListener {
            navigateToCategory("Beverages")
        }
    }

    /**
     * Helper function to reduce code repetition
     */
    private fun navigateToCategory(categoryName: String) {
        val intent = Intent(this, ViewItemsActivity::class.java)
        intent.putExtra("CATEGORY_ID", categoryName)
        startActivity(intent)
    }
}