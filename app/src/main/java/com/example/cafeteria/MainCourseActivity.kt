package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainCourseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maincourse_activity)

        // Initialize all 7 main course items (IDs 33 to 39) using a loop
        for (i in 33..39) {
            setupItem(i)
        }

        // View Cart Button logic
        findViewById<Button>(R.id.btnViewCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    /**
     * Finds views dynamically by ID and attaches logic.
     * Replaces the manual variables and repetitive if-else blocks.
     */
    private fun setupItem(idSuffix: Int) {
        val res = resources
        val pkg = packageName

        // Find views using the numerical suffix (e.g., itemName33, btnMinus33)
        val itemNameTxt = findViewById<TextView>(res.getIdentifier("itemName$idSuffix", "id", pkg))
        val itemPriceTxt = findViewById<TextView>(res.getIdentifier("itemPrice$idSuffix", "id", pkg))
        val quantityTxt = findViewById<TextView>(res.getIdentifier("quantityText$idSuffix", "id", pkg))
        val btnMinus = findViewById<Button>(res.getIdentifier("btnMinus$idSuffix", "id", pkg))
        val btnPlus = findViewById<Button>(res.getIdentifier("btnPlus$idSuffix", "id", pkg))
        val btnAdd = findViewById<Button>(res.getIdentifier("btnAddToCart$idSuffix", "id", pkg))

        // Basic safety check: if the view isn't found, skip to avoid crashes
        if (itemNameTxt == null || quantityTxt == null) return

        val itemName = itemNameTxt.text.toString()

        // Minus Button Logic
        btnMinus.setOnClickListener {
            val currentQty = quantityTxt.text.toString().toIntOrNull() ?: 0
            if (currentQty > 0) {
                quantityTxt.text = (currentQty - 1).toString()
            }
        }

        // Plus Button Logic
        btnPlus.setOnClickListener {
            val currentQty = quantityTxt.text.toString().toIntOrNull() ?: 0
            quantityTxt.text = (currentQty + 1).toString()
        }

        // Add to Cart Logic
        btnAdd.setOnClickListener {
            val quantity = quantityTxt.text.toString().toIntOrNull() ?: 0
            if (quantity > 0) {
                // Extracts price safely by filtering for digits and decimals (e.g., "₹250.00" -> 250.0)
                val priceString = itemPriceTxt.text.toString().filter { it.isDigit() || it == '.' }
                val price = priceString.toDoubleOrNull() ?: 0.0

                // Add to the ShoppingCart Singleton
                ShoppingCart.addItem(itemName, quantity, price)

                Toast.makeText(this, "$quantity $itemName added to cart", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select a quantity for $itemName", Toast.LENGTH_SHORT).show()
            }
        }
    }
}