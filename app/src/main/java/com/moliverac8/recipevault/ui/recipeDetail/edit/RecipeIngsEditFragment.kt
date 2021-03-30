package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moliverac8.recipevault.databinding.FragmentIngListEditBinding
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment

class RecipeIngsEditFragment : Fragment() {

    private lateinit var binding: FragmentIngListEditBinding
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIngListEditBinding.inflate(layoutInflater)

        val adapter = RecipeIngsEditAdapter()

        binding.ingredients.adapter = adapter

        return binding.root
    }

    companion object {
        fun newInstance(): RecipeIngsEditFragment = RecipeIngsEditFragment()
    }
}