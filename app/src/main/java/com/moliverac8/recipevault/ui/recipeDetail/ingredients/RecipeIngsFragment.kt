package com.moliverac8.recipevault.ui.recipeDetail.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moliverac8.recipevault.databinding.FragmentIngListBinding
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.instructions.RecipeInstructionsAdapter
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeIngsFragment : Fragment() {

    // Obtengo el ViewModel generado en RecipePagerFragment (asociado a su contexto/ciclo de vida)
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentIngListBinding.inflate(layoutInflater)

        val adapter = RecipeIngsAdapter()
        binding.ingList.adapter = adapter

        viewModel.recipeWithIng.observe(viewLifecycleOwner, { recipe ->
            adapter.submitList(recipe.ings)
        })

        binding.lifecycleOwner = this
        return binding.root
    }

    companion object {
        fun newInstance(): RecipeIngsFragment = RecipeIngsFragment()
    }
}