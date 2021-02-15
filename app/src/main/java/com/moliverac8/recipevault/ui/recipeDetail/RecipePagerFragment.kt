package com.moliverac8.recipevault.ui.recipeDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipePagerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipePagerFragment : Fragment() {

    private val args by navArgs<RecipePagerFragmentArgs>()
    private val viewModel: RecipeDetailVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecipePagerBinding.inflate(layoutInflater)

        binding.pager.adapter = RecipePager(this)
        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.recipe)
                else -> getString(R.string.ings)
            }
        }.attach()

        binding.lifecycleOwner = this

        viewModel.getRecipe(args.recipeID)

        return binding.root
    }
}


class RecipePager(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> RecipeDetailFragment.newInstance()
            else -> RecipeIngsFragment.newInstance()
        }
    }
}