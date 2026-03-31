package com.example.cafeteria.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteria.Datamodels.DessertItem
import com.example.cafeteria.R
//
//class DessertAdapter(private val dessertList: List<DessertItem>) :
//    RecyclerView.Adapter<DessertAdapter.DessertViewHolder>() {
//
//    class DessertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val nameText: TextView = view.findViewById(R.id.itemName)
//        val priceText: TextView = view.findViewById(R.id.itemPrice)
//        val btnPlus: Button = view.findViewById(R.id.btnPlus)
//        val btnMinus: Button = view.findViewById(R.id.btnMinus)
//        val quantityText: TextView = view.findViewById(R.id.quantityText)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DessertViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_dessert_row, parent, false) // Your single item XML
//        return DessertViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: DessertViewHolder, position: Int) {
//        val item = dessertList[position]
//        holder.nameText.text = item.name
//        holder.priceText.text = "₹${item.price}"
//
//        // Logic for quantity buttons
//        var count = 0
//        holder.btnPlus.setOnClickListener {
//            count++
//            holder.quantityText.text = count.toString()
//        }
//        holder.btnMinus.setOnClickListener {
//            if (count > 0) count--
//            holder.quantityText.text = count.toString()
//        }
//    }
//
//    override fun getItemCount() = dessertList.size
//}