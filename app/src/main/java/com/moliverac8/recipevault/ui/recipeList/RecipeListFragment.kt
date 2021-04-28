package com.moliverac8.recipevault.ui.recipeList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import com.moliverac8.domain.Recipe
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipeListBinding
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    private val viewModel: RecipeListVM by viewModels(ownerProducer = { this })
    private val isTablet: Boolean by lazy { requireContext().resources.getBoolean(R.bool.isTablet) }
    private val filterList: MutableList<String> = mutableListOf()
    private lateinit var unfilteredRecipes: List<RecipeWithIng>

    private val navigateToDetails = { id: Int, isEditable: Boolean ->
        findNavController().navigate(
            RecipeListFragmentDirections.actionRecipeListFragmentToRecipePagerFragment(
                id,
                isEditable
            )
        )
    }

    private val navigateToDetailsOnTablet = { id: Int, isEditable: Boolean ->
        findNavController().navigate(
            RecipePagerFragmentDirections.actionRecipePagerFragmentSelf(
                id,
                isEditable
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecipeListBinding.inflate(layoutInflater)

        val adapter = RecipeListAdapter(RecipeListAdapter.OnClickListener { recipe, isEditable ->
            recipe.domainRecipe.id.let { id ->
                if (isTablet) navigateToDetailsOnTablet(id, isEditable)
                else navigateToDetails(id, isEditable)
            }
        })

        binding.filterChips.children.forEach { it ->
            it.setOnClickListener { chip ->
                with(chip as Chip) {
                    if (isChecked) {
                        when (resources.getResourceEntryName(id)) {
                            "breakfastChip" -> filterList.add(DishType.BREAKFAST.name)
                            "mealChip" -> filterList.add(DishType.MEAL.name)
                            "dinnerChip" -> filterList.add(DishType.DINNER.name)
                            "veganChip" -> filterList.add(DietType.VEGAN.name)
                            "vegetarianChip" -> filterList.add(DietType.VEGETARIAN.name)
                            else -> filterList.add(DietType.REGULAR.name)
                        }
                    } else {
                        when (resources.getResourceEntryName(id)) {
                            "breakfastChip" -> filterList.remove(DishType.BREAKFAST.name)
                            "mealChip" -> filterList.remove(DishType.MEAL.name)
                            "dinnerChip" -> filterList.remove(DishType.DINNER.name)
                            "veganChip" -> filterList.remove(DietType.VEGAN.name)
                            "vegetarianChip" -> filterList.remove(DietType.VEGETARIAN.name)
                            else -> filterList.remove(DietType.REGULAR.name)
                        }
                    }
                }
                var list = unfilteredRecipes
                filterList.forEach { filter ->
                   list = list.filter { recipe -> recipe.domainRecipe.dietType.name == filter || recipe.domainRecipe.dishType.any { it.name == filter } }
                }
                adapter.submitList(list)
            }
        }

        binding.recipeList.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner, { recipes ->
            unfilteredRecipes = recipes
            adapter.submitList(recipes)
        })

        viewModel.updateRecipes()

        binding.newRecipeBtn.setOnClickListener {
            if (isTablet) navigateToDetailsOnTablet(-1, true)
            else navigateToDetails(-1, true)
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}