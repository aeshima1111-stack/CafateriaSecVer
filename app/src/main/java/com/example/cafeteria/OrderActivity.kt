package com.example.cafeteria

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cafeteria.databinding.ActivityOrderBinding
import com.google.firebase.database.*
import java.util.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var dbRef: DatabaseReference
    private var orderList = mutableListOf<Order>()
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Orders")

        setupRecyclerView()
        fetchOrders()
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter(orderList)
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter
    }

    private fun fetchOrders() {
        // Query to get orders ordered by timestamp (newest first)
        dbRef.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (snap in snapshot.children) {
                    val order = snap.getValue(Order::class.java)
                    order?.let { orderList.add(it) }
                }
                orderList.reverse() // Newest at top
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OrderActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}




class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.txtOrderId)
        val items: TextView = view.findViewById(R.id.txtOrderItems)
        val date: TextView = view.findViewById(R.id.txtOrderDate)
        val total: TextView = view.findViewById(R.id.txtOrderTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.id.text = "Order: ...${order.orderId.takeLast(6)}"
        holder.total.text = "₹${String.format("%.2f", order.totalAmount)}"

        // Format the names of all items in the order
        holder.items.text = order.items.joinToString(", ") { it.name }

        // Format Date
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        holder.date.text = sdf.format(Date(order.timestamp))
    }

    override fun getItemCount() = orders.size
}