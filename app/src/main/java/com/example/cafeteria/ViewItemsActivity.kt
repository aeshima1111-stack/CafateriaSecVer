package com.example.cafeteria
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.bumptech.glide.Glide

class ViewItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTitle: TextView
    private val database = FirebaseDatabase.getInstance().reference
    private val itemList = mutableListOf<FoodItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_items)

        findViewById<Button>(R.id.viewCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        // 1. Get Category ID passed from MenuActivity
        val categoryName = intent.getStringExtra("CATEGORY_ID") ?: "Breakfast"

        tvTitle = findViewById(R.id.tvCategoryTitle)
        tvTitle.text = categoryName

        recyclerView = findViewById(R.id.rvItems)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchItems(categoryName)
    }

    private fun fetchItems(category: String) {
        // Path: items -> categoryName (e.g. items/Breakfast)
        database.child("items").child(category).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(FoodItem::class.java)
                    if (item != null) {
                        itemList.add(item)
                    }
                }

                if(itemList.isEmpty()) {
                    Toast.makeText(this@ViewItemsActivity, "No food items found", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = FoodAdapter(itemList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewItemsActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- INNER ADAPTER CLASS ---
    inner class FoodAdapter(private val list: List<FoodItem>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {
        private val quantities = mutableMapOf<Int, Int>()
        inner class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val name = view.findViewById<TextView>(R.id.tvFoodName)
            val price = view.findViewById<TextView>(R.id.tvFoodPrice)
            val image = view.findViewById<android.widget.ImageView>(R.id.ivFoodImage)
            val btnPlus: Button = view.findViewById(R.id.btnPlus)
            val btnMinus: Button = view.findViewById(R.id.btnMinus)
            val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
            val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.name.text = item.name
            holder.price.text = "₹${item.price}"
            val currentQty = quantities[position] ?: 0

            holder.tvQuantity.text = currentQty.toString()

            // Load Appwrite URL using Glide
            Glide.with(this@ViewItemsActivity)
                .load(item.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.image)


            // Increment Logic
            holder.btnPlus.setOnClickListener {
                val newQty = (quantities[position] ?: 0) + 1
                quantities[position] = newQty
                holder.tvQuantity.text = newQty.toString()
            }

            // Decrement Logic
            holder.btnMinus.setOnClickListener {
                val current = quantities[position] ?: 0
                if (current > 0) {
                    val newQty = current - 1
                    quantities[position] = newQty
                    holder.tvQuantity.text = newQty.toString()
                }
            }

            // Add to Cart Logic
            holder.btnAddToCart.setOnClickListener {
                val qty = quantities[position] ?: 0
                if (qty > 0) {

                    // Extract price safely (removes '₹' and parses decimal)
                    val priceString = item.price.toString().filter { it.isDigit() || it == '.' }
                    val price = priceString.toDoubleOrNull() ?: 0.0

                    // Add to the ShoppingCart Singleton
                    ShoppingCart.addItem(item.name.toString(), qty, price)
                    // Here you would typically save to a 'Cart' node in Firebase
                    Toast.makeText(holder.itemView.context,
                        "Added $qty ${item.name} to cart", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(holder.itemView.context,
                        "Please select quantity", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun getItemCount() = list.size
    }
}