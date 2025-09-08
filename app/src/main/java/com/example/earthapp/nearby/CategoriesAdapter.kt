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
        RecyclerView.ViewHolder(binding.root)

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
        holder.binding.apply {
            categoryName.text = category.categoryName
            categoryImage.setImageResource(category.resId)
            root.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }

    override fun getItemCount(): Int = categories.size
}