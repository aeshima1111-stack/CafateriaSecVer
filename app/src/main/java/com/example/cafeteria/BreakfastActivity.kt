package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged

class BreakfastActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var btnViewCart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.breakfast_activity)

        searchBar = findViewById(R.id.searchBar)
        btnViewCart = findViewById(R.id.btnViewCart)

        // Initialize all 7 breakfast items using a helper function
        for (i in 1..7) {
            setupItem(i)
        }

        // View Cart Button logic
        btnViewCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // 🔍 Search Functionality
        searchBar.doOnTextChanged { text, _, _, _ ->
            filterItems(text.toString())
        }
    }

    /**
     * Finds views dynamically by ID suffix (1-7) and attaches logic.
     */
    private fun setupItem(idSuffix: Int) {
        val res = resources
        val pkg = packageName

        val layout = findViewById<LinearLayout>(res.getIdentifier("layoutItem$idSuffix", "id", pkg))
        val nameTxt = findViewById<TextView>(res.getIdentifier("itemName$idSuffix", "id", pkg))
        val priceTxt = findViewById<TextView>(res.getIdentifier("itemPrice$idSuffix", "id", pkg))
        val quantityTxt = findViewById<TextView>(res.getIdentifier("quantityText$idSuffix", "id", pkg))
        val btnMinus = findViewById<Button>(res.getIdentifier("btnMinus$idSuffix", "id", pkg))
        val btnPlus = findViewById<Button>(res.getIdentifier("btnPlus$idSuffix", "id", pkg))
        val btnAdd = findViewById<Button>(res.getIdentifier("btnAddToCart$idSuffix", "id", pkg))

        val itemName = nameTxt.text.toString()

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
                // Extracts price by removing the currency symbol (e.g., "₹100" -> "100")
                val priceString = priceTxt.text.toString().substring(1)
                val price = priceString.toDoubleOrNull() ?: 0.0

                ShoppingCart.addItem(itemName, quantity, price)
                Toast.makeText(this, "$quantity $itemName(s) added to cart", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select a quantity for $itemName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 🔍 Filter function to hide/show layouts based on search query
     */
    private fun filterItems(query: String) {
        val lowerCaseQuery = query.lowercase()

        for (i in 1..7) {
            val layoutId = resources.getIdentifier("layoutItem$i", "id", packageName)
            val nameId = resources.getIdentifier("itemName$i", "id", packageName)

            val layout = findViewById<LinearLayout>(layoutId)
            val nameTxt = findViewById<TextView>(nameId)

            if (layout != null && nameTxt != null) {
                val name = nameTxt.text.toString().lowercase()
                layout.visibility = if (name.contains(lowerCaseQuery)) View.VISIBLE else View.GONE
            }
        }
    }
}