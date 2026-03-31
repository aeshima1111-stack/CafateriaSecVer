package com.example.cafeteria

import java.util.*

/**
 * Singleton object for the ShoppingCart.
 * 'object' in Kotlin ensures thread-safe, lazy initialization automatically.
 */
object ShoppingCart {

    private val items = mutableListOf<CartItem>()

    /**
     * Returns a read-only list of items to the UI to ensure data integrity.
     */
    val allItems: List<CartItem>
        get() = items

    val totalPrice: Double
        get() = items.sumOf { it.price * it.quantity }

    fun addItem(name: String, quantity: Int, price: Double) {
        val existingItem = items.find { it.name == name }

        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            items.add(CartItem(name, quantity, price))
        }
    }

    fun removeItem(name: String) {
        items.removeAll { it.name == name }
    }

    fun updateQuantity(name: String, newQuantity: Int) {
        items.find { it.name == name }?.quantity = newQuantity
    }

    fun clearCart() {
        items.clear()
    }

    /**
     * Data class to represent an item in the cart.
     */
    data class CartItem(
        val name: String,
        var quantity: Int,
        val price: Double
    ) {

        constructor(): this("",0,0.0)
        val formattedPrice: String
            get() = String.format(Locale.getDefault(), "₹%.2f", price)

        val totalItemPriceFormatted: String
            get() = String.format(Locale.getDefault(), "₹%.2f", price * quantity)
    }
}