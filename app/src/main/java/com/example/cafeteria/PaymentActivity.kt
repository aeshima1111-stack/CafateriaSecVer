package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var totalAmountTextView: TextView
    private lateinit var qrImageView: ImageView
    private lateinit var nameInput: EditText
    private lateinit var rollInput: EditText
    private lateinit var timeSpinner: Spinner
    private lateinit var confirmButton: Button

    private var totalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_activity)

        // 1. Initialize Views
        totalAmountTextView = findViewById(R.id.totalAmount)
        qrImageView = findViewById(R.id.qrImage)
        nameInput = findViewById(R.id.nameInput)
        rollInput = findViewById(R.id.rollInput)
        timeSpinner = findViewById(R.id.timeSpinner)
        confirmButton = findViewById(R.id.confirmButton)

        // 2. Retrieve the total amount safely
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        totalAmountTextView.text = String.format(Locale.getDefault(), "Total: ₹%.2f", totalAmount)

        // 3. Populate the time spinner using Kotlin's listOf
        val pickupTimes = listOf(
                "10:00 AM - 10:30 AM",
                "10:30 AM - 11:00 AM",
                "11:00 AM - 11:30 AM",
                "11:30 AM - 12:00 PM",
                "12:00 PM - 12:30 PM"
        )

        val timeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, pickupTimes)
        timeSpinner.adapter = timeAdapter

        // 4. Confirm Button Logic
        confirmButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val rollNumber = rollInput.text.toString().trim()
            val selectedTime = timeSpinner.selectedItem.toString()

            if (name.isEmpty()) {
                nameInput.error = "Name is required"
                return@setOnClickListener
            }
            if (rollNumber.isEmpty()) {
                rollInput.error = "Roll Number is required"
                return@setOnClickListener
            }

            // Simulate payment confirmation
            val confirmationMsg = String.format(Locale.getDefault(), "Payment of ₹%.2f confirmed.", totalAmount)
            Toast.makeText(this, confirmationMsg, Toast.LENGTH_SHORT).show()

            // Generate unique order ID and navigate
            val orderId = generateOrderId()

            val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
                putExtra("orderId", orderId)
            }
            startActivity(intent)

            // Optional: Clear the cart since the order is placed
            ShoppingCart.clearCart()
        }
    }

    // Method to generate a more unique order ID
    private fun generateOrderId(): String {
        val timestamp = System.currentTimeMillis().toString()
        val randomPart = UUID.randomUUID().toString().substring(0, 8).uppercase()
        val shortTime = timestamp.takeLast(8)

        return "ORDER-$shortTime-$randomPart"
    }
}