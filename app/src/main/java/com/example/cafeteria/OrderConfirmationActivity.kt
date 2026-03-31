package com.example.cafeteria

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var orderIdTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        // Initialize the view
        orderIdTextView = findViewById(R.id.orderIdTextView)

        // Retrieve the order ID passed from PaymentActivity
        val orderId = intent.getStringExtra("orderId")

        // In Kotlin, 'if' is an expression that returns a value.
        // We can assign the result directly to the TextView's text property.
        orderIdTextView.text = if (orderId != null) {
            "Your Order ID: $orderId"
        } else {
            "Order ID not found."
        }
    }
}