package com.moliverac8.recipevault.ui.recipeList

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.databinding.ItemRecipeListBinding

class RecipeListAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<RecipeWithIng, RecipeListAdapter.ViewHolder>(RecipeListDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent, onClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item, false, position)
        }
        holder.bind(item)
    }

    class OnClickListener(val clickListener: (recipe: RecipeWithIng, isEditable: Boolean, position: Int) -> Unit) {
        fun onClick(recipe: RecipeWithIng, isEditable: Boolean = false, position: Int) = clickListener(recipe, isEditable, position)
    }

    class ViewHolder private constructor(private val binding: ItemRecipeListBinding, private val onClickListener: OnClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecipeWithIng) {
            binding.recipe = item.domainRecipe
            /*binding.editBtn.setOnClickListener {
                onClickListener.onClick(item, true)
            }*/
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, onClickListener: OnClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRecipeListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, onClickListener)
            }
        }
    }
}

class RecipeListDiffCallback : DiffUtil.ItemCallback<RecipeWithIng>() {

    override fun areItemsTheSame(oldItem: RecipeWithIng, newItem: RecipeWithIng): Boolean {
        return oldItem.domainRecipe.id == newItem.domainRecipe.id
    }

    override fun areContentsTheSame(oldItem: RecipeWithIng, newItem: RecipeWithIng): Boolean {
        return oldItem.domainRecipe == newItem.domainRecipe
    }
}