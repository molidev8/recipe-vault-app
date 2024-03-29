package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moliverac8.domain.Recipe
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.databinding.ItemEditInstructionsListBinding
import com.moliverac8.recipevault.databinding.ItemInstructionsListBinding
import com.moliverac8.recipevault.databinding.ItemRecipeListBinding

/**
 * Adapter for the instructions editable list recyclerview with automatic detection of difference in items with DiffUtil
 */
class RecipeInstructionsEditAdapter :
    ListAdapter<String, RecipeInstructionsEditAdapter.ViewHolder>(InstructionDiffCallback()) {

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

    class ViewHolder private constructor(private val binding: ItemEditInstructionsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.instruction = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemEditInstructionsListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class InstructionDiffCallback : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem

}