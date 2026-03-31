package com.example.cafeteria

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ImageSliderAdapter(private val imageList: List<Int>, val context: Context) :
    RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SliderViewHolder {
        // 1. Create a CardView to give images rounded corners
        val cardView = CardView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(16, 8, 16, 8) // Add some breathing room
            }
            radius = 40f // Soft rounded corners
            elevation = 8f
        }

        // 2. Create the ImageView
        val imageView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        cardView.addView(imageView)
        return SliderViewHolder(cardView, imageView)
    }


    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.imageView.setImageResource(imageList[position].toInt())
    }

    override fun getItemCount(): Int = imageList.size

    class SliderViewHolder(container: CardView, val imageView: ImageView) :
        RecyclerView.ViewHolder(container)
}