package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteria.ShoppingCart.CartItem
import com.example.cafeteria.databinding.CartActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.util.*

class CartActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: CartActivityBinding
    private lateinit var cart: ShoppingCart

    // 1. Initialize Firebase Reference
    private val database = FirebaseDatabase.getInstance().getReference("Orders")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CartActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Checkout.preload(applicationContext)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cart = ShoppingCart
        displayCartItems()

        binding.proceedToPaymentButton.setOnClickListener {
            if (cart.allItems.isNotEmpty()) {
                startPayment()
            } else {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.cocBtn.setOnClickListener {
            if (cart.allItems.isNotEmpty()) {
                saveOrderToFirebase("CoC_Pending")
            } else {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startPayment() {
        val checkout = Checkout()
        checkout.setKeyID("rzp_live_ILgsfZCZoFIKMb")

        try {
            val options = JSONObject()
            options.put("name", "Cafeteria App")
            options.put("description", "Food Order Payment")
            options.put("theme.color", "#4E342E")
            options.put("currency", "INR")

            val amountInPaisa = (cart.totalPrice * 100).toLong()
            options.put("amount", amountInPaisa)

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            checkout.open(this, options)

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        // 2. Save Order to Firebase before clearing cart
        saveOrderToFirebase(razorpayPaymentId ?: "N/A")
    }

    private fun saveOrderToFirebase(paymentId: String) {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val uId = currentUser?.uid ?: ""
        val orderId = database.push().key ?: UUID.randomUUID().toString()
        val currentstatus = if (paymentId== "CoC_Pending") "COC" else "Paid Online"


        // Create Order Object
        val order = Order(
            orderId = orderId,
            userId = uId,
            paymentId = paymentId,
            items = cart.allItems.toList(), // Snapshot of current items
            totalAmount = cart.totalPrice,
            timestamp = System.currentTimeMillis(),
            status = currentstatus
        )

        // Push to Firebase
        database.child(orderId).setValue(order).addOnSuccessListener {
            Toast.makeText(this, "Order placed: $currentstatus", Toast.LENGTH_LONG).show()

            // 3. Clear cart and refresh UI only after success
            cart.clearCart()
            displayCartItems()

            // Optional: Move to a Success Screen
            // startActivity(Intent(this, OrderSuccessActivity::class.java))
            // finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to save order: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show()
    }

    private fun displayCartItems() {
        binding.cartItemsLayout.removeAllViews()
        val cartItems = cart.allItems

        if (cartItems.isEmpty()) {
            val emptyMsg = TextView(this).apply {
                text = "Your cart is empty."
                textSize = 18f
                setTextColor(android.graphics.Color.parseColor("#8D6E63"))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            binding.cartItemsLayout.addView(emptyMsg)
            binding.proceedToPaymentButton.isEnabled = false
        } else {
            binding.proceedToPaymentButton.isEnabled = true
            val inflater = LayoutInflater.from(this)
            for (item in cartItems) {
                val itemView =
                    inflater.inflate(R.layout.cart_item_row, binding.cartItemsLayout, false)
                itemView.findViewById<TextView>(R.id.itemNameTextView).text = item.name
                itemView.findViewById<TextView>(R.id.itemPriceTextView).text = item.formattedPrice
                itemView.findViewById<TextView>(R.id.itemQuantityTextView).text =
                    "Qty: ${item.quantity}"

                itemView.findViewById<Button>(R.id.removeButton).setOnClickListener {
                    cart.removeItem(item.name)
                    displayCartItems()
                }
                binding.cartItemsLayout.addView(itemView)
            }
        }
        binding.totalAmountTextView.text =
            String.format(Locale.getDefault(), "Total: ₹%.2f", cart.totalPrice)
    }
}

data class Order(
    val orderId: String = "",
    val paymentId: String = "",
    val userId: String = "",
    val items: List<CartItem> = listOf(),
    val totalAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending"
) {
    constructor(): this("","","",emptyList(),0.0,0,"")
}
