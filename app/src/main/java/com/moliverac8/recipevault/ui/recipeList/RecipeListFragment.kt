package com.moliverac8.recipevault.ui.recipeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mancj.materialsearchbar.MaterialSearchBar
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipeListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    private val viewModel: RecipeListVM by viewModels(ownerProducer = { this })
    private val filterList: MutableList<String> = mutableListOf()
    private lateinit var binding: FragmentRecipeListBinding
    private val newRecipeBtn: FloatingActionButton by lazy {
        requireActivity().findViewById(R.id.newRecipeBtn)
    }

    interface RecipeListNavigationInterface {
        fun navigateToNewRecipe()
        fun navigateToExistingRecipe(id: Int, recipeCard: View)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeListBinding.inflate(layoutInflater)
        val adapter = RecipeListAdapter(RecipeListAdapter.OnClickListener { recipe, isEditable, position ->
            recipe.domainRecipe.id.let { id ->
                (activity as RecipeListNavigationInterface).navigateToExistingRecipe(id, binding.recipeList[position])
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
            }
        }

        binding.recipeList.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner, { recipes ->
            adapter.submitList(recipes)
        })

        viewModel.updateRecipes()

        /*binding.searchView.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {

            override fun onSearchStateChanged(enabled: Boolean) {

            }

            override fun onSearchConfirmed(text: CharSequence?) {
                adapter.submitList(unfilteredRecipes.filter {
                    it.domainRecipe.name.contains(text.toString())
                })
            }

            override fun onButtonClicked(buttonCode: Int) {

            }
        })*/

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onStart() {
        super.onStart()
        newRecipeBtn.setOnClickListener {
            (activity as RecipeListNavigationInterface).navigateToNewRecipe()
        }
    }

    companion object {
        fun newInstance(): RecipeListFragment = RecipeListFragment()
    }
}