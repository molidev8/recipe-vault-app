package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moliverac8.domain.Ingredient
import com.moliverac8.recipevault.databinding.FragmentIngListEditBinding
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import es.uam.eps.tfg.menuPlanner.util.IngQuantityDialog

class RecipeIngsEditFragment : Fragment() {

    private lateinit var binding: FragmentIngListEditBinding
    private lateinit var ings: MutableList<Ingredient>
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIngListEditBinding.inflate(layoutInflater)

        val adapter = RecipeIngsEditAdapter(RecipeIngsEditAdapter.OnClickListener {
            IngQuantityDialog().show(parentFragmentManager, "")
        })

        binding.ingredients.adapter = adapter

        viewModel.recipeWithIng.observe(viewLifecycleOwner, { recipe ->
            ings = recipe.ings.toMutableList()
            adapter.submitList(ings)
        })

        binding.addBtn.setOnClickListener {
            ings.add(Ingredient())
            adapter.submitList(ings)
            adapter.notifyItemInserted(ings.size - 1)
        }

        return binding.root
    }

    companion object {
        fun newInstance(): RecipeIngsEditFragment = RecipeIngsEditFragment()
    }
}