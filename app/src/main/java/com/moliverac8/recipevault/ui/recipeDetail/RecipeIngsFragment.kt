package com.moliverac8.recipevault.ui.recipeDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moliverac8.recipevault.databinding.FragmentIngListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeIngsFragment : Fragment() {

    // Obtengo el ViewModel generado en RecipePagerFragment (asociado a su contexto/ciclo de vida)
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentIngListBinding.inflate(layoutInflater)



        binding.lifecycleOwner = this
        return binding.root
    }

    companion object {
        fun newInstance(): RecipeIngsFragment = RecipeIngsFragment()
    }
}