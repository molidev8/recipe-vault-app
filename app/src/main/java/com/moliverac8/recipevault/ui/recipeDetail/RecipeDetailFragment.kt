package com.moliverac8.recipevault.ui.recipeDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailBinding
import com.moliverac8.recipevault.ui.common.toListOfInstructions
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

        viewModel.recipeWithIng.observe(viewLifecycleOwner, { recipe ->
            binding.recipe = recipe.domainRecipe
            adapter.submitList(recipe.domainRecipe.instructions.toListOfInstructions())
        })

        binding.lifecycleOwner = this

        return binding.root
    }

    companion object {
        fun newInstance(): RecipeDetailFragment = RecipeDetailFragment()
    }
}