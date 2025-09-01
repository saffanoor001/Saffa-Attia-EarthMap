package com.example.earthapp.nearby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.earthapp.databinding.ItemCategoryBinding
import com.example.kotlinproject.nearby.model.Categories

class CategoryAdapter(
    private val categories: List<Categories>,
    private val onCategoryClick: (Categories) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root){
            init {
                binding.root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        onCategoryClick(categories[position])
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.categoryName.text = category.categoryName
        holder.binding.categoryImage.setImageResource(category.resId)
    }

    override fun getItemCount(): Int = categories.size
}