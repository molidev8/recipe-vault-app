package com.moliverac8.recipevault.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moliverac8.recipevault.databinding.FragmentSearchResultsBinding
import com.moliverac8.recipevault.ui.recipeList.RecipeListAdapter
import com.moliverac8.recipevault.ui.recipeList.RecipeListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultFragment : Fragment() {

    private lateinit var binding: FragmentSearchResultsBinding
    private val viewModel: SearchVM by viewModels(ownerProducer = {
        requireActivity()
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultsBinding.inflate(layoutInflater)

        val adapter =
            RecipeListAdapter(RecipeListAdapter.OnClickListener { recipe, isEditable, position ->
                recipe.domainRecipe.id.let { id ->
                    (activity as RecipeListFragment.RecipeListNavigationInterface).navigateToExistingRecipe(
                        id,
                        binding.searchResults[position]
                    )
                }
            })
        binding.searchResults.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes)
        }

        return binding.root
    }
}