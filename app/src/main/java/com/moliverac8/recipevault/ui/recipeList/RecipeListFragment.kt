package com.moliverac8.recipevault.ui.recipeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.moliverac8.recipevault.databinding.FragmentRecipeListBinding

class RecipeListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecipeListBinding.inflate(layoutInflater)

        val adapter = RecipeListAdapter(RecipeListAdapter.OnClickListener {

        })

        binding.recipeList.adapter = adapter
        binding.lifecycleOwner = this
        return binding.root
    }
}