package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.recipevault.databinding.FragmentIngListEditBinding
import com.moliverac8.recipevault.ui.common.IngQuantityDialog
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import com.moliverac8.recipevault.ui.recipeDetail.ingredients.RecipeIngsAdapter

class RecipeIngsEditFragment : Fragment() {

    private lateinit var binding: FragmentIngListEditBinding
    private lateinit var adapter: RecipeIngsEditAdapter
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIngListEditBinding.inflate(layoutInflater)

        viewModel.recipeWithIng.observe(viewLifecycleOwner, { recipe ->

            adapter = RecipeIngsEditAdapter(RecipeIngsEditAdapter.OnClickListener { ing ->
                parentFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(IngQuantityDialog(viewModel, ing), null).addToBackStack(null).commit()
            })

            binding.ingredients.adapter = adapter
            adapter.submitList(recipe.ings)
        })

        binding.addBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(IngQuantityDialog(viewModel), null).addToBackStack(null).commit()
        }

        viewModel.dialogIng.observe(viewLifecycleOwner) { ing ->
            Log.d(GENERAL, "ing $ing \n list ${adapter.currentList}")
            val list = adapter.currentList.toMutableList()
            val result = adapter.currentList.find { it.name == ing.name }
            if (result != null) {
                list.removeIf { it.name == ing.name }
                list.add(ing)
                adapter.submitList(list)
            } else {
                adapter.submitList(adapter.currentList.toMutableList().apply {
                    add(ing)
                })
            }
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveIngredients(adapter.currentList)
    }

    companion object {
        fun newInstance(): RecipeIngsEditFragment = RecipeIngsEditFragment()
    }
}