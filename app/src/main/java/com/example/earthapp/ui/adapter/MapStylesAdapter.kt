package com.example.earthapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.earthapp.R
import com.example.earthapp.databinding.ItemMapStylesBinding
import com.example.earthapp.model.MapStyle


class MapStylesAdapter(
    private val styles: List<MapStyle>,
    private val onItemClick: (MapStyle) -> Unit
) : RecyclerView.Adapter<MapStylesAdapter.StyleViewHolder>() {


    private var selectedPosition = 0

    inner class StyleViewHolder(val binding: ItemMapStylesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleViewHolder {
        val binding = ItemMapStylesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StyleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StyleViewHolder, position: Int) {
        val styleItem = styles[position]

        holder.binding.txtLabel.text = styleItem.name


        Glide.with(holder.itemView.context)
            .load(styleItem.previewUrl)
            .into(holder.binding.imgCheck)


        if (selectedPosition == position) {
            holder.binding.frameContainer.setBackgroundResource(R.drawable.bg_selected)
        } else {
            holder.binding.frameContainer.setBackgroundResource(R.drawable.bg_unselected)
        }


        holder.itemView.setOnClickListener {
            val previous = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previous)
            notifyItemChanged(selectedPosition)
            onItemClick(styleItem)
        }
    }

    override fun getItemCount() = styles.size
}

