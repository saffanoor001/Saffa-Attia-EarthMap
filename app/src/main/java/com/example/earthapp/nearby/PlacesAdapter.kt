package com.example.earthapp.nearby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.earthapp.databinding.ItemPlacesBinding
import com.example.earthapp.nearby.model.Result

class PlacesAdapter(
    private val places: List<Result>
) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(val binding: ItemPlacesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlacesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.binding.placeName.text = place.name
        holder.binding.placeAddress.text = place.location.formatted_address
        holder.binding.placeDistance.text = "${place.distance}"
    }

    override fun getItemCount(): Int = places.size
}
