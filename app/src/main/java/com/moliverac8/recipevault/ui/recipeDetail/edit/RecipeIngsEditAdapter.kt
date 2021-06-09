package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.moliverac8.domain.Ingredient
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.recipevault.databinding.ItemIngEditListBinding
import com.moliverac8.recipevault.databinding.ItemIngListBinding
import com.moliverac8.recipevault.ui.recipeDetail.ingredients.RecipeIngsAdapter
import com.moliverac8.recipevault.ui.recipeList.RecipeListAdapter

class RecipeIngsEditAdapter(
    private val ings: MutableList<Ingredient>,
    private val onClickListener: OnClickListener
) :
    ListAdapter<Ingredient, RecipeIngsEditAdapter.ViewHolder>(IngsDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent, onClickListener, this, ings)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class OnClickListener(val clickListener: (adapter: RecipeIngsEditAdapter, pos: Int, ings: MutableList<Ingredient>) -> Unit) {
        fun onClick(adapter: RecipeIngsEditAdapter, pos: Int, ings: MutableList<Ingredient>) =
            clickListener(adapter, pos, ings)
    }

    class ViewHolder private constructor(
        private val binding: ItemIngEditListBinding,
        private val onClickListener: OnClickListener,
        private val adapter: RecipeIngsEditAdapter,
        private val ings: MutableList<Ingredient>
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Ingredient) {
            binding.ingredient = item
            binding.units.setOnClickListener {
                val pos = absoluteAdapterPosition
                onClickListener.onClick(adapter, pos, ings)
            }

            binding.ingEdit.doAfterTextChanged {
                val pos = absoluteAdapterPosition
                val ing = ings[pos]
                ings[pos] = Ingredient(
                    ing.id,
                    it.toString(),
                    ing.unit,
                    ing.quantity
                )
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onClickListener: OnClickListener,
                adapter: RecipeIngsEditAdapter,
                ings: MutableList<Ingredient>
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemIngEditListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, onClickListener, adapter, ings)
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