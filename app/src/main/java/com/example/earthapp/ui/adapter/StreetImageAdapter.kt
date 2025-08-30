package com.example.earthapp.ui.adapter

import com.example.earthapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StreetImageAdapter(private var images: List<String>) :
    RecyclerView.Adapter<StreetImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivStreet: ImageView = itemView.findViewById(R.id.ivStreet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_street_views, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(images[position])
            .centerCrop()
            .into(holder.ivStreet)
    }

    override fun getItemCount(): Int = images.size

    fun updateData(newImages: List<String>) {
        images = newImages
        notifyDataSetChanged()
    }
}

