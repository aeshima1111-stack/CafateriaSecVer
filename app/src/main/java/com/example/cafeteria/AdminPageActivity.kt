package com.example.cafeteria

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteria.databinding.ActivityAdminPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPageBinding

    // Data models for Statistics
    data class CategoryStats(var count: Int = 0, var revenue: Double = 0.0)
    data class ItemStats(
        val name: String,
        val category: String,
        val unitPrice: Double,
        var totalQty: Int = 0,
        var totalRevenue: Double = 0.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdminPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.logoutadmin.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.cardadd.setOnClickListener {
            startActivity(Intent(this, AddDataActivity::class.java))
        }
        binding.addimg.setOnClickListener {
            startActivity(Intent(this, AddDataActivity::class.java))
        }
        binding.cardview.setOnClickListener {
            startActivity(Intent(this, AdminViewActivity::class.java))
        }
        binding.viewimg.setOnClickListener {
            startActivity(Intent(this, AdminViewActivity::class.java))
        }

        binding.cardhistory.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

        binding.historyimg.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

        // --- GENERATE DETAILED EXCEL/CSV ---
        binding.cardsale.setOnClickListener {
            Toast.makeText(this, "Fetching data and generating report...", Toast.LENGTH_SHORT).show()
            startDetailedReportExport()
        }

        binding.salesimg.setOnClickListener {
            Toast.makeText(this, "Fetching data and generating report...", Toast.LENGTH_SHORT).show()
            startDetailedReportExport()
        }

        binding.cardinventory.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
        binding.inventoryimg.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
    }

    private fun startDetailedReportExport() {
        val itemsRef = FirebaseDatabase.getInstance().getReference("items")
        val ordersRef = FirebaseDatabase.getInstance().getReference("Orders")

        // 1. Fetch Menu items first to know which item belongs to which category
        itemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(itemsSnapshot: DataSnapshot) {
                val productToCategoryMap = mutableMapOf<String, String>()

                for (categorySnapshot in itemsSnapshot.children) {
                    val categoryName = categorySnapshot.key ?: "Unknown"
                    for (foodSnapshot in categorySnapshot.children) {
                        val productName = foodSnapshot.child("name").getValue(String::class.java)
                        if (productName != null) {
                            productToCategoryMap[productName] = categoryName
                        }
                    }
                }

                // 2. Now process the orders using the category map
                processDetailedOrders(ordersRef, productToCategoryMap)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminPageActivity, "Failed to load Menu data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun processDetailedOrders(ordersRef: com.google.firebase.database.DatabaseReference, categoryMap: Map<String, String>) {
        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val catStatsMap = mutableMapOf<String, CategoryStats>()
                val itemStatsMap = mutableMapOf<String, ItemStats>()

                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.items?.forEach { item ->
                        val category = categoryMap[item.name] ?: "Uncategorized"

                        // Track Category Totals
                        val cStats = catStatsMap.getOrPut(category) { CategoryStats() }
                        cStats.count += item.quantity
                        cStats.revenue += (item.price * item.quantity)

                        // Track Item-Specific Details
                        val iStats = itemStatsMap.getOrPut(item.name) {
                            ItemStats(item.name, category, item.price)
                        }
                        iStats.totalQty += item.quantity
                        iStats.totalRevenue += (item.price * item.quantity)
                    }
                }

                // 3. Build CSV String with two sections
                val csvContent = StringBuilder()

                // Section 1: Category Summary
                csvContent.append("SUMMARY BY CATEGORY\n")
                csvContent.append("Category,Total Items Sold,Total Revenue (INR)\n")
                catStatsMap.forEach { (name, data) ->
                    csvContent.append("$name,${data.count},${String.format("%.2f", data.revenue)}\n")
                }

                csvContent.append("\n\n") // Gap between tables

                // Section 2: Detailed Item Sales
                csvContent.append("DETAILED ITEM SALES\n")
                csvContent.append("Item Name,Category,Unit Price,Quantity Sold,Total Item Revenue\n")
                itemStatsMap.values.forEach { item ->
                    // Replace commas in names with spaces to prevent CSV corruption
                    val safeName = item.name.replace(",", " ")
                    csvContent.append("$safeName,${item.category},${item.unitPrice},${item.totalQty},${String.format("%.2f", item.totalRevenue)}\n")
                }

                saveCsvToDevice(csvContent.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminPageActivity, "Failed to load Orders", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveCsvToDevice(content: String) {
        val fileName = "Cafeteria_Sales_${System.currentTimeMillis()}.csv"
        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/Cafeteria_Reports")
            }
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { stream ->
                stream.write(content.toByteArray())
                Toast.makeText(this, "Detailed Report saved in Downloads folder", Toast.LENGTH_LONG).show()
            }
        } ?: Toast.makeText(this, "Error: Could not create file", Toast.LENGTH_SHORT).show()
    }
}