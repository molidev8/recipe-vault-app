package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moliverac8.domain.Ingredient
import com.moliverac8.recipevault.databinding.ItemIngEditListBinding
import com.moliverac8.recipevault.databinding.ItemIngListBinding
import com.moliverac8.recipevault.ui.recipeDetail.ingredients.RecipeIngsAdapter

class RecipeIngsEditAdapter : ListAdapter<Ingredient, RecipeIngsEditAdapter.ViewHolder>(IngsDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class ViewHolder private constructor(private val binding: ItemIngEditListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Ingredient) {
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemIngEditListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class IngsDiffCallback : DiffUtil.ItemCallback<Ingredient>() {

    override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean =
        oldItem == newItem
}