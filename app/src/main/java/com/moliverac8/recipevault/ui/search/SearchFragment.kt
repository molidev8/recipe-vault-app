package com.moliverac8.recipevault.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialSharedAxis
import com.moliverac8.recipevault.databinding.FragmentSearchBinding
import com.moliverac8.recipevault.hideSoftKeyboard
import com.moliverac8.recipevault.ui.recipeList.RecipeListAdapter
import com.moliverac8.recipevault.ui.recipeList.RecipeListFragment
import com.moliverac8.recipevault.ui.recipeList.RecipeListVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        val adapter =
            RecipeListAdapter(RecipeListAdapter.OnClickListener { recipe, isEditable, position ->
                recipe.domainRecipe.id.let { id ->
                    (activity as SearchFragmentNavigation).navigateToDetails(
                        id
                    )
                }
            })
        binding.searchResults.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchBar.setNavigationOnClickListener {
            (activity as SearchFragmentNavigation).navigateHomeFromSearch()
        }

        binding.searchEdit.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                activity?.hideSoftKeyboard()
                binding.searchEdit.clearFocus()
                binding.searchEdit.isCursorVisible = false
                val filter = binding.searchEdit.text.toString()
                viewModel.filterRecipes(filter)
                return@setOnKeyListener true
            }
            false
        }
    }
}