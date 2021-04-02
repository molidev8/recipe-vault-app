package com.moliverac8.recipevault.ui.recipeDetail.instructions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailBinding
import com.moliverac8.recipevault.ui.common.toListOfInstructions
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.instructions.RecipeInstructionsAdapter
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import com.moliverac8.recipevault.ui.recipeList.RecipeListFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {

    // Obtengo el ViewModel generado en RecipePagerFragment (asociado a su contexto/ciclo de vida)
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecipeDetailBinding.inflate(layoutInflater)

        val adapter = RecipeInstructionsAdapter()
        binding.instructions.adapter = adapter

        val recipe = viewModel.recipeWithIng.value?.domainRecipe
        recipe?.let {
            binding.recipe = it
            adapter.submitList(it.instructions.toListOfInstructions())
        }

        binding.lifecycleOwner = this

        return binding.root
    }

    companion object {
        fun newInstance(): RecipeDetailFragment = RecipeDetailFragment()
    }
}