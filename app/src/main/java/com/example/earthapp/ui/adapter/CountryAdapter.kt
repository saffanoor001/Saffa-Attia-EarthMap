package com.example.earthapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.earthapp.R
import com.example.earthapp.model.Country

class CountryAdapter(
    private val items: MutableList<Country>,
    private val onClick: (Country) -> Unit
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    inner class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFlag: ImageView = itemView.findViewById(R.id.ivFlag)
        val tvCountryName: TextView = itemView.findViewById(R.id.tvCountryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(v)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = items[position]
        holder.tvCountryName.text = country.name



        //val code = country.code ?: ""
        // val flagUrl = "https://flagcdn.com/w160/${code.lowercase()}.png"
        val flagUrl = "https://flagcdn.com/w320/${country.code.lowercase()}.png"
        // val flagUrl = "https://flagcdn.com/w320/${filteredDataList[position].countryCode.lowercase()}.png"

        Glide.with(holder.itemView.context)
            .load(flagUrl)
            .centerCrop()
            .into(holder.ivFlag)

        holder.itemView.setOnClickListener { onClick(country) }
    }


    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Country>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
