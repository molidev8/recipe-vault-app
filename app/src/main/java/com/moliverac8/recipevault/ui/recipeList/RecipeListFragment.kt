package com.moliverac8.recipevault.ui.recipeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipeListBinding
import com.moliverac8.recipevault.ui.common.SwipeToDeleteRecipeList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    private val viewModel: RecipeListVM by viewModels(ownerProducer = { requireActivity() })
    private val filterList: MutableList<String> = mutableListOf()
    private lateinit var binding: FragmentRecipeListBinding
    private lateinit var adapter: RecipeListAdapter
    private lateinit var previousList: List<RecipeWithIng>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeListBinding.inflate(layoutInflater)
        adapter =
            RecipeListAdapter(RecipeListAdapter.OnClickListener { recipe, isEditable, view ->
                recipe.domainRecipe.id.let { id ->
                    (activity as RecipeListNavigation).navigateToExistingRecipe(
                        id,
                        view.itemView
                    )
                }
            })

        binding.filterChips.children.forEach {
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
                if (filterList.isEmpty()) {
                    viewModel.loadOriginalRecipes()
                } else {
                    viewModel.filterByChips(filterList)
                }
            }
        }

        binding.recipeList.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner, { recipes ->
            previousList = recipes
            adapter.submitList(recipes)
        })

        viewModel.updateRecipes()

        binding.searchView.setOnClickListener {
            (activity as RecipeListNavigation).navigateToSearch()
        }

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onStop() {
        super.onStop()
        /*Log.d(GENERAL, "${hasAnyRecipeBeenRemoved()}")
        if (hasAnyRecipeBeenRemoved()) {
            val diff = adapter.currentList.minus(previousList)
            diff.forEach {
                viewModel.deleteRecipeOnDatabase(it)
            }
        }*/
    }

    override fun onStart() {
        super.onStart()
        ItemTouchHelper(
            SwipeToDeleteRecipeList(
                requireContext(),
                viewModel,
                adapter,
                requireActivity().findViewById<FloatingActionButton>(R.id.newRecipeBtn)
            )
        ).attachToRecyclerView(binding.recipeList)
        requireActivity().findViewById<FloatingActionButton>(R.id.newRecipeBtn).setOnClickListener {
            (activity as RecipeListNavigation).navigateToNewRecipe()
        }
    }

    private fun hasAnyRecipeBeenRemoved(): Boolean = adapter.currentList != previousList

    companion object {
        fun newInstance(): RecipeListFragment = RecipeListFragment()
    }
}