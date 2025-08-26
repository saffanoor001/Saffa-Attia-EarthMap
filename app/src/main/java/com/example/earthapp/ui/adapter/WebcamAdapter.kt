package com.example.earthapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.earthapp.R
import com.example.earthapp.model.Webcam
import com.example.earthapp.ui.WebViewActivity

class WebcamAdapter(
    private val allItems: MutableList<Webcam>
) : RecyclerView.Adapter<WebcamAdapter.WebcamViewHolder>() {

    private val filteredItems = allItems.toMutableList()

    inner class WebcamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPreview: ImageView = itemView.findViewById(R.id.ivWebcamThumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_webcam, parent, false)
        return WebcamViewHolder(v)
    }

    override fun onBindViewHolder(holder: WebcamViewHolder, position: Int) {
        val webcam = filteredItems[position]

        val previewUrl = webcam.images?.current?.preview ?: webcam.image?.current?.preview

        Glide.with(holder.itemView.context)
            .load(previewUrl)
            .centerCrop()
            .into(holder.ivPreview)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            // Safely get numeric webcam ID
            val webcamId = webcam.webcamId ?: webcam.id?.toLongOrNull()

            if (webcamId != null) {
                val url = "https://webcams.windy.com/webcams/public/embed/player/$webcamId/day"
                val intent = Intent(context, WebViewActivity::class.java).apply {
                    putExtra("webcam_url", url)
                }
                context.startActivity(intent)
            } else {
                // Handle invalid ID
                Toast.makeText(context, "Invalid webcam ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = filteredItems.size

    fun updateData(newItems: List<Webcam>) {
        allItems.clear()
        allItems.addAll(newItems)
        filteredItems.clear()
        filteredItems.addAll(newItems)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        if (query.isBlank()) {
            filteredItems.clear()
            filteredItems.addAll(allItems)
        } else {
            val qLower = query.lowercase()
            filteredItems.clear()
            filteredItems.addAll(
                allItems.filter { (it.title ?: "").lowercase().contains(qLower) }
            )
        }
        notifyDataSetChanged()
    }
}
