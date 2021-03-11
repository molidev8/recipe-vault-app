package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailEditBinding
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailEditBindingImpl

class RecipeDetailEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentRecipeDetailEditBinding.inflate(layoutInflater)

        binding.addBtn.setOnClickListener {

        }

        return binding.root
    }
}