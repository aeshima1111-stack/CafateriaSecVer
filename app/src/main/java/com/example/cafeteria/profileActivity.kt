package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteria.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class profileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val dbOrder = FirebaseDatabase.getInstance().getReference("Orders")
    private val userorderlist = mutableListOf<userOrder>()
    private lateinit var userAdapter: userOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadUserOrderHistory()
        userInterface()
        loadUserFirestoreData()


        binding.btnlogout.setOnClickListener {
            auth.signOut()

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadUserOrderHistory() {
        userAdapter = userOrderAdapter(userorderlist)
        binding.rvhistory.layoutManager = LinearLayoutManager(this)
        binding.rvhistory.adapter = userAdapter
    }

    private fun loadUserFirestoreData() {
        val uid = auth.currentUser?.uid ?: return
        val userEmail = auth.currentUser?.email
        binding.tvemail.text = userEmail ?: "Email not found"

//        firestore.collection("users").document(uid).get()
//            .addOnSuccessListener { doc ->
//                if (doc.exists()) {
//                    binding.tvemail.text = doc.getString("name")
//                }
//
//            }
    }

    private fun userInterface() {
        val uid = auth.currentUser?.uid ?: return

        dbOrder.orderByChild("userId").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userorderlist.clear()
                    for (snap in snapshot.children) {
                        snap.getValue(userOrder::class.java)?.let { userorderlist.add(it) }
                    }
                    userorderlist.sortByDescending { it.timestamp }
                    binding.tvEmptyHistory.visibility = if (userorderlist.isEmpty()) View.VISIBLE else View.GONE

                    userAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@profileActivity, "An error has occured loading the history", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

//Adapter class

class userOrderAdapter(private val userOrders: List<userOrder>): RecyclerView.Adapter<userOrderAdapter.userOrderViewHolder>(){

    class userOrderViewHolder(view: View): RecyclerView.ViewHolder(view){
        val date: TextView = view.findViewById(R.id.orderDate)
        val status: TextView = view.findViewById(R.id.orderStatus)
        val items: TextView = view.findViewById(R.id.itemsOrdered)
        val total: TextView = view.findViewById(R.id.orderTotal)
        val orderId: TextView = view.findViewById(R.id.orderID)

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): userOrderAdapter.userOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_history, parent, false)
        return userOrderViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: userOrderAdapter.userOrderViewHolder,
        position: Int
    ) {
        val order = userOrders[position]
        holder.orderId.text = "Order: ...${order.orderId.takeLast(6)}"
        // Date formating
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        holder.date.text = sdf.format(Date(order.timestamp))

        //status formatting
        holder.status.text = order.status
        if (order.status == "COC") {
            holder.status.setTextColor(android.graphics.Color.RED)
        } else {
            holder.status.setTextColor(android.graphics.Color.GREEN)
        }

        holder.items.text = order.items.joinToString(", ") {"${it.name} x ${it.quantity}"}
        holder.total.text = "₹${String.format("%.2f", order.totalAmount)}"

    }

    override fun getItemCount() = userOrders.size



}





// Data Class

data class userOrder(
    val orderId: String = "",
    val userId: String = "",
    val paymentId: String = "",
    val userEmail: String = "",
    val items: List<ShoppingCart.CartItem> = listOf(),
    val totalAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending"
) {
    constructor(): this("","","","",emptyList(),0.0,0,"Pending")
}