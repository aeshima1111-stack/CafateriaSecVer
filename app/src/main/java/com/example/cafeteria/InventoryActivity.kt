package com.example.cafeteria

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteria.databinding.ActivityInventoryBinding
import com.google.firebase.database.*
import java.util.*

class InventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private lateinit var dbRef: DatabaseReference
    private var itemList = mutableListOf<InventoryItem>()
    private lateinit var adapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle System Bar Insets (ViewCompat)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbRef = FirebaseDatabase.getInstance().getReference("Inventory")

        setupRecyclerView()
        fetchData()

        binding.fabAddItem.setOnClickListener { showAddDialog() }

        // Search Logic
        binding.searchBar.addTextChangedListener { text ->
            val filtered = itemList.filter { it.name.contains(text.toString(), ignoreCase = true) }
            adapter.updateList(filtered)
        }
    }

    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_inventory, null)
        val etName = dialogView.findViewById<EditText>(R.id.etItemName)
        val btnExpDate = dialogView.findViewById<Button>(R.id.btnPickExpDate)
        var selectedDate = ""

        btnExpDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                selectedDate = "$year-${month + 1}-$day"
                btnExpDate.text = selectedDate
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        AlertDialog.Builder(this)
            .setTitle("Add to Inventory")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString()
                if (name.isNotEmpty() && selectedDate.isNotEmpty()) {
                    val id = dbRef.push().key!!
                    val item = InventoryItem(id, name, "2026-03-24", selectedDate)
                    dbRef.child(id).setValue(item)
                }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun setupRecyclerView() {
        adapter = InventoryAdapter(mutableListOf()) { item ->
            dbRef.child(item.id).removeValue() // Delete logic
        }
        binding.rvInventory.layoutManager = LinearLayoutManager(this)
        binding.rvInventory.adapter = adapter
    }

    private fun fetchData() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (itemSnap in snapshot.children) {
                    val item = itemSnap.getValue(InventoryItem::class.java)
                    item?.let { itemList.add(it) }
                }
                adapter.updateList(itemList)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
// data class
data class InventoryItem(
    val id: String = "",
    val name: String = "",
    val addDate: String = "",
    val expiryDate: String = "",
    val isExpired: Boolean = false
)


//adapter
class InventoryAdapter(
    private var list: List<InventoryItem>,
    private val onDelete: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        val card = holder.itemView.findViewById<com.google.android.material.card.MaterialCardView>(R.id.inventoryCard)

        holder.itemView.findViewById<TextView>(R.id.txtItemName).text = item.name
        holder.itemView.findViewById<TextView>(R.id.txtDates).text = "Expiry: ${item.expiryDate}"

        // EXPIRY LOGIC: Check if date is passed (Simplified example)
        // In a real app, parse the string to a Date object and compare with current date
        if (item.expiryDate < "2026-03-24") {
            card.strokeColor = android.graphics.Color.RED
            card.strokeWidth = 4
        } else {
            card.strokeWidth = 0
        }

        holder.itemView.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener { onDelete(item) }
    }

    override fun getItemCount() = list.size

    fun updateList(newList: List<InventoryItem>) {
        list = newList
        notifyDataSetChanged()
    }
}