package com.example.cafeteria

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cafeteria.Datamodels.AdminFoodItem
import com.example.cafeteria.databinding.ActivityAdminViewBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminViewActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearch: TextInputEditText
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var binding: ActivityAdminViewBinding
    private var fullList = mutableListOf<AdminFoodItem>() // Keeps original data
    private lateinit var adapter: AdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etSearch = findViewById(R.id.etAdminSearch)
        recyclerView = findViewById(R.id.rvAdminItems)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchAllItems()

        // 🔍 Search Logic
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchAllItems() {
        database.child("items").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fullList.clear()
                for (categorySnapshot in snapshot.children) {
                    val categoryName = categorySnapshot.key ?: ""
                    for (itemSnapshot in categorySnapshot.children) {
                        val item = AdminFoodItem(
                            id = itemSnapshot.key ?: "",
                            category = categoryName,
                            name = itemSnapshot.child("name").value.toString(),
                            price = itemSnapshot.child("price").value.toString(),
                            imageUrl = itemSnapshot.child("imageUrl").value.toString()
                        )
                        fullList.add(item)
                    }
                }
                adapter = AdminAdapter(fullList.toMutableList())
                recyclerView.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminViewActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterList(query: String) {
        val filtered = fullList.filter {
            it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
        adapter.updateList(filtered)
    }

    // --- ADAPTER ---
    inner class AdminAdapter(private var list: MutableList<AdminFoodItem>) : RecyclerView.Adapter<AdminAdapter.ViewHolder>() {

        fun updateList(newList: List<AdminFoodItem>) {
            list = newList.toMutableList()
            notifyDataSetChanged()
        }

        inner class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val name = view.findViewById<android.widget.TextView>(R.id.tvAdminFoodName)
            val price = view.findViewById<android.widget.TextView>(R.id.tvAdminFoodPrice)
            val category = view.findViewById<android.widget.TextView>(R.id.tvAdminCategory)
            val image = view.findViewById<android.widget.ImageView>(R.id.ivAdminFoodImage)
            val deleteBtn = view.findViewById<android.widget.ImageButton>(R.id.btnDelete)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int) =
            ViewHolder(android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_admin_food, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.name.text = item.name
            holder.price.text = "₹${item.price}"
            holder.category.text = "Category: ${item.category}"
            Glide.with(this@AdminViewActivity).load(item.imageUrl).into(holder.image)

            holder.deleteBtn.setOnClickListener {
                database.child("items").child(item.category).child(item.id).removeValue()
                    .addOnSuccessListener { Toast.makeText(this@AdminViewActivity, "Deleted", Toast.LENGTH_SHORT).show() }
            }
        }

        override fun getItemCount() = list.size
    }
}