package com.moliverac8.recipevault.ui.recipeList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipeListBinding
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    private val viewModel: RecipeListVM by viewModels(ownerProducer = { this })
    private val isTablet: Boolean by lazy { requireContext().resources.getBoolean(R.bool.isTablet) }

    private val navigateToDetails = { id: Int, isEditable: Boolean ->
        findNavController().navigate(
            RecipeListFragmentDirections.actionRecipeListFragmentToRecipePagerFragment(
                id,
                isEditable
            )
        )
    }

    private val navigateToDetailsOnTablet = { id: Int, isEditable: Boolean ->
        findNavController().navigate(RecipePagerFragmentDirections.actionRecipePagerFragmentSelf(id, isEditable))
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

        binding.recipeList.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner, {
            adapter.submitList(it)
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