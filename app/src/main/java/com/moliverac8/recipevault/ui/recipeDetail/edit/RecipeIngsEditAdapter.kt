package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moliverac8.domain.Ingredient
import com.moliverac8.recipevault.databinding.ItemIngEditListBinding
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeList.RecipeListAdapter

class RecipeIngsEditAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Ingredient, RecipeIngsEditAdapter.ViewHolder>(IngsDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent, onClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class OnClickListener(val clickListener: (ing: Ingredient) -> Unit) {
        fun onClick(ing: Ingredient) = clickListener(ing)
    }

    class ViewHolder private constructor(
        private val binding: ItemIngEditListBinding,
        private val onClickListener: OnClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Ingredient) {
            binding.ing = item
            binding.editBtn.setOnClickListener {
                onClickListener.onClick(item)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, onClickListener: OnClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemIngEditListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, onClickListener)
            }
        }
    }
}

class IngsDiffCallback : DiffUtil.ItemCallback<Ingredient>() {

    override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean =
        oldItem.name == newItem.name || oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean =
        oldItem == newItem
}