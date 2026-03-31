package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BeveragesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beverage_activity)

        // Initialize all 7 items using a helper function
        setupItem(15)
        setupItem(16)
        setupItem(17)
        setupItem(18)
        setupItem(19)
        setupItem(20)
        setupItem(21)

        // View Cart Button logic
        findViewById<Button>(R.id.btnViewCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    /**
     * Finds views dynamically by ID and attaches logic.
     * This replaces the long list of private variables.
     */
    private fun setupItem(idSuffix: Int) {
        // Find views using the numerical suffix
        val itemNameTxt = findViewById<TextView>(resources.getIdentifier("itemName$idSuffix", "id", packageName))
        val itemPriceTxt = findViewById<TextView>(resources.getIdentifier("itemPrice$idSuffix", "id", packageName))
        val quantityTxt = findViewById<TextView>(resources.getIdentifier("quantityText$idSuffix", "id", packageName))
        val btnMinus = findViewById<Button>(resources.getIdentifier("btnMinus$idSuffix", "id", packageName))
        val btnPlus = findViewById<Button>(resources.getIdentifier("btnPlus$idSuffix", "id", packageName))
        val btnAdd = findViewById<Button>(resources.getIdentifier("btnAddToCart$idSuffix", "id", packageName))

        val itemName = itemNameTxt.text.toString()

        // Minus Button Logic
        btnMinus.setOnClickListener {
            var quantity = quantityTxt.text.toString().toInt()
            if (quantity > 0) {
                quantity--
                quantityTxt.text = quantity.toString()
            }
        }

        // Plus Button Logic
        btnPlus.setOnClickListener {
            var quantity = quantityTxt.text.toString().toInt()
            quantity++
            quantityTxt.text = quantity.toString()
        }

        // Add to Cart Logic
        btnAdd.setOnClickListener {
            val quantity = quantityTxt.text.toString().toInt()
            if (quantity > 0) {
                // Extract price (assuming format "₹100.00")
                val priceString = itemPriceTxt.text.toString().replace("₹", "")
                val price = priceString.toDoubleOrNull() ?: 0.0

                // Add to Singleton
                ShoppingCart.addItem(itemName, quantity, price)

                Toast.makeText(this, "$quantity $itemName(s) added to cart", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select a quantity for $itemName", Toast.LENGTH_SHORT).show()
            }
        }
    }
}