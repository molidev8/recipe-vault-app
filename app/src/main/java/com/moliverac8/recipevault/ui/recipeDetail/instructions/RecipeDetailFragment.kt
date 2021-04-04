package com.moliverac8.recipevault.ui.recipeDetail.instructions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.moliverac8.domain.DishType
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailBinding
import com.moliverac8.recipevault.ui.common.toListOfInstructions
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.RecipePager
import com.moliverac8.recipevault.ui.recipeDetail.instructions.RecipeInstructionsAdapter
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import com.moliverac8.recipevault.ui.recipeList.RecipeListFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {

    // Obtengo el ViewModel generado en RecipePagerFragment (asociado a su contexto/ciclo de vida)
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })
    private lateinit var binding: FragmentRecipeDetailBinding
    private lateinit var adapter: RecipeInstructionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailBinding.inflate(layoutInflater)

        adapter = RecipeInstructionsAdapter()
        binding.instructions.adapter = adapter

        viewModel.recipeWithIng.observe(viewLifecycleOwner, { recipe ->
            Log.d(GENERAL, "FG - Receta cargada $recipe")
            binding.recipe = recipe.domainRecipe
            loadTimeToEatChips(recipe.domainRecipe.dishType)
            adapter.submitList(recipe.domainRecipe.instructions.toListOfInstructions())
        })

        binding.editBtn.setOnClickListener {
            val view = parentFragment?.view as CoordinatorLayout
            val pager = view.findViewById<ViewPager2>(R.id.pager)
            pager.adapter = RecipePager(requireParentFragment(), true)
        }

        binding.lifecycleOwner = this

        return binding.root
    }

    private fun loadTimeToEatChips(dishType: List<DishType>) {
        dishType.forEach { type ->
            val chip = Chip(context).apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                text = type.toString()
            }
            binding.timeToEatChips.addView(chip)
        }
    }

    companion object {
        fun newInstance(): RecipeDetailFragment = RecipeDetailFragment()
    }
}