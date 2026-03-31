package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StartersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.starters_activity)

        // Initialize all 7 starter items (IDs 8 to 14) using a loop
        for (i in 8..14) {
            setupItem(i)
        }

        // View Cart Button logic
        findViewById<Button>(R.id.btnViewCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    /**
     * Finds views dynamically by ID suffix and attaches logic.
     * Handles the "btnPlus" vs "btnPlus14" inconsistency automatically.
     */
    private fun setupItem(idSuffix: Int) {
        val res = resources
        val pkg = packageName

        // Dynamic lookup for common IDs
        val itemNameTxt = findViewById<TextView>(res.getIdentifier("itemName$idSuffix", "id", pkg))
        val itemPriceTxt = findViewById<TextView>(res.getIdentifier("itemPrice$idSuffix", "id", pkg))
        val quantityTxt = findViewById<TextView>(res.getIdentifier("quantityText$idSuffix", "id", pkg))
        val btnMinus = findViewById<Button>(res.getIdentifier("btnMinus$idSuffix", "id", pkg))
        val btnAdd = findViewById<Button>(res.getIdentifier("btnAddToCart$idSuffix", "id", pkg))

        // Handle the specific naming quirk for Plus Button (Item 14)
        var btnPlus = findViewById<Button>(res.getIdentifier("btnPlus$idSuffix", "id", pkg))
        if (btnPlus == null && idSuffix == 14) {
            btnPlus = findViewById(R.id.btnPlus)
        }

        // Safety check to ensure the views exist
        if (itemNameTxt == null || quantityTxt == null || btnPlus == null) return

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
                // Safely extracts price (removes currency symbol and parses)
                val priceString = itemPriceTxt.text.toString().filter { it.isDigit() || it == '.' }
                val price = priceString.toDoubleOrNull() ?: 0.0

                // Add to the ShoppingCart object (Singleton)
                ShoppingCart.addItem(itemName, quantity, price)

                Toast.makeText(this, "$quantity $itemName(s) added to cart", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select a quantity for $itemName", Toast.LENGTH_SHORT).show()
            }
        }
    }
}