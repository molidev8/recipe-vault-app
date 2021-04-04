package com.moliverac8.recipevault.ui.recipeDetail.instructions

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moliverac8.domain.Recipe
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.ItemInstructionsListBinding
import com.moliverac8.recipevault.databinding.ItemRecipeListBinding

class RecipeInstructionsAdapter :
    ListAdapter<String, RecipeInstructionsAdapter.ViewHolder>(InstructionDiffCallback()) {

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


    class ViewHolder private constructor(private val binding: ItemInstructionsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var instructionCollapsed: Boolean = false
        private var resources = Resources.getSystem()

        fun bind(item: String) {
            binding.instruction = item
            binding.expandBtn.setOnClickListener {
                binding.instructionText.maxLines =
                    if (!instructionCollapsed) {
                        binding.expandBtn.text = resources.getString(R.string.expand)
                        1
                    } else {
                        resources.getString(R.string.collapse)
                        Int.MAX_VALUE
                    }
                instructionCollapsed = !instructionCollapsed
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemInstructionsListBinding.inflate(layoutInflater, parent, false)
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