package com.moliverac8.recipevault.ui.recipeDetail.instructions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialFadeThrough
import com.moliverac8.domain.DishType
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailBinding
import com.moliverac8.recipevault.ui.common.toListOfInstructions
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.RecipePager
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {

    // I retrieved the ViewModel associated with the RecipePager fragment to share its data between the loaded fragments in the ViewPager
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })
    private lateinit var binding: FragmentRecipeDetailBinding
    private lateinit var adapter: RecipeInstructionsAdapter
    private lateinit var pager: ViewPager2

    interface DetailToEditNavigateInterface {
        fun navigateToEdit(prepareNavigation: () -> Unit)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailBinding.inflate(layoutInflater)

        adapter = RecipeInstructionsAdapter()
        binding.instructions.adapter = adapter

        viewModel.recipeWithIng.observe(viewLifecycleOwner, { recipe ->
            binding.recipe = recipe.domainRecipe
            loadTimeToEatChips(recipe.domainRecipe.dishType)
            adapter.submitList(recipe.domainRecipe.instructions.toListOfInstructions())
        })

        binding.editBtn.setOnClickListener {
            (parentFragment as DetailToEditNavigateInterface).navigateToEdit {
                setupTransitionToEdit()
                pager.visibility = GONE
                pager.adapter = RecipePager(requireParentFragment(), true)
                pager.visibility = VISIBLE
            }
        }

        binding.lifecycleOwner = this
        return binding.root
    }

    private fun setupTransitionToEdit() {
        val view = parentFragment?.view as ConstraintLayout
        pager = view.findViewById(R.id.pager)
        val transition = MaterialFadeThrough()
        TransitionManager.beginDelayedTransition(view, transition)
    }

    private fun loadTimeToEatChips(dishType: List<DishType>) {
        dishType.forEach { type ->
            val chip = Chip(context).apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                text = type.toString()
                isClickable = false
            }
            binding.timeToEatChips.addView(chip)
        }
    }

    companion object {
        fun newInstance(): RecipeDetailFragment = RecipeDetailFragment()
    }
}