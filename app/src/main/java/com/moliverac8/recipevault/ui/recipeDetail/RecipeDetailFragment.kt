package com.moliverac8.recipevault.ui.recipeDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailBinding

class RecipeDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecipeDetailBinding.inflate(layoutInflater)


        //binding.recipe =
        binding.lifecycleOwner = this

        return binding.root
    }
}