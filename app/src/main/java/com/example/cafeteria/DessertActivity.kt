package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DessertActivity : AppCompatActivity() {

//     Initialize Firebase Reference
         val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dessert_activity)

        // Initialize all 7 dessert items (IDs 22 to 28) using a loop
        for (i in 22..28) {
            setupItem(i)
        }

        // View Cart Button logic
        findViewById<Button>(R.id.btnViewCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

//        addData()
//        showData()
    }

//    fun addData() {
//        var items = FoodItem(
//            name = "Chocolate",
//            price = "₹30",
//            imageUrl = "chocopasty"
//
//        )
//        database.child("items").child("desserts").push().setValue(items)
//            .addOnSuccessListener {
//                Toast.makeText(this, "added", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
//
//            }
//
//    }
//    fun showData() {
//        database.child("items").child("desserts")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val list = ArrayList<FoodItem>()
//
//                    for (data in snapshot.children) {
//                        val item = data.getValue(FoodItem::class.java)
//                        if (item != null) {
//                            list.add(item)
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@DessertActivity, "Not able to fetch", Toast.LENGTH_SHORT).show()
//                }
//
//
//            })
//    }

    /**
     * Finds views dynamically by ID and attaches logic.
     * Replaces the repetitive manual initialization.
     */
    private fun setupItem(idSuffix: Int) {
        val res = resources
        val pkg = packageName

        // Find views using the numerical suffix (e.g., itemName22, btnMinus22)
        val itemNameTxt = findViewById<TextView>(res.getIdentifier("itemName$idSuffix", "id", pkg))
        val itemPriceTxt = findViewById<TextView>(res.getIdentifier("itemPrice$idSuffix", "id", pkg))
        val quantityTxt = findViewById<TextView>(res.getIdentifier("quantityText$idSuffix", "id", pkg))
        val btnMinus = findViewById<Button>(res.getIdentifier("btnMinus$idSuffix", "id", pkg))
        val btnPlus = findViewById<Button>(res.getIdentifier("btnPlus$idSuffix", "id", pkg))
        val btnAdd = findViewById<Button>(res.getIdentifier("btnAddToCart$idSuffix", "id", pkg))

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
                // Extract price safely (removes '₹' and parses decimal)
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

//class DessertActivity : AppCompatActivity() {
//
//    private lateinit var database: DatabaseReference
//    private lateinit var recyclerView: RecyclerView
//    private val dessertList = mutableListOf<DessertItem>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dessert)
//
//        recyclerView = findViewById(R.id.dessertRecyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        // Initialize Firebase Reference
//        database = FirebaseDatabase.getInstance().getReference("desserts")
//
//        fetchDessertData()
//    }
//
//    private fun fetchDessertData() {
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                dessertList.clear()
//                for (postSnapshot in snapshot.children) {
//                    val item = postSnapshot.getValue(DessertItem::class.java)
//                    item?.let { dessertList.add(it) }
//                }
//                recyclerView.adapter = DessertAdapter(dessertList)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@DessertActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//}