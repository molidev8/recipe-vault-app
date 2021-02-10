package com.moliverac8.recipevault.ui.recipeList

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moliverac8.recipevault.databinding.ItemRecipeListBinding
import com.moliverac8.domain.Recipe

class RecipeListAdapter(private val onClickListener: OnClickListener) : ListAdapter<Recipe, RecipeListAdapter.ViewHolder>(RecipeListDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
        holder.bind(item)
    }

    class OnClickListener(val clickListener: (recipe: Recipe) -> Unit) {
        fun onClick(recipe: Recipe) = clickListener(recipe)
    }

    class ViewHolder private constructor(private val binding: ItemRecipeListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Recipe) {
            binding.recipe = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRecipeListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class RecipeListDiffCallback : DiffUtil.ItemCallback<Recipe>() {

    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }
}