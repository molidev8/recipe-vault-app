package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputLayout
import com.moliverac8.domain.Ingredient
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.recipevault.databinding.FragmentIngListEditBinding
import com.moliverac8.recipevault.ui.common.IngQuantityDialog
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment

class RecipeIngsEditFragment : Fragment() {

    private lateinit var binding: FragmentIngListEditBinding
    private lateinit var ings: MutableList<Ingredient>
    private lateinit var adapter: RecipeIngsEditAdapter
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIngListEditBinding.inflate(layoutInflater)

        viewModel.recipeWithIng.observe(viewLifecycleOwner, { recipe ->
            ings = recipe.ings.toMutableList()

            adapter = RecipeIngsEditAdapter(ings, RecipeIngsEditAdapter.OnClickListener { adapter, pos, ings ->
                IngQuantityDialog(adapter, pos, ings).show(parentFragmentManager, "")
            })

            binding.ingredients.adapter = adapter
            adapter.submitList(ings)
        })

        binding.addBtn.setOnClickListener {
            ings.add(Ingredient())
            adapter.notifyItemInserted(ings.size - 1)
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveIngredients(adapter.currentList)
    }

    companion object {
        fun newInstance(): RecipeIngsEditFragment = RecipeIngsEditFragment()
    }
}