package com.moliverac8.recipevault.ui.recipeDetail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialContainerTransform
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipePagerBinding
import com.moliverac8.recipevault.ui.recipeDetail.edit.RecipeDetailEditFragment
import com.moliverac8.recipevault.ui.recipeDetail.edit.RecipeIngsEditFragment
import com.moliverac8.recipevault.ui.recipeDetail.ingredients.RecipeIngsFragment
import com.moliverac8.recipevault.ui.recipeDetail.instructions.RecipeDetailFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipePagerFragment : Fragment() {

    private val args by navArgs<RecipePagerFragmentArgs>()
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { this })
    private val isTablet: Boolean by lazy { requireContext().resources.getBoolean(R.bool.isTablet) }
    private val bottomBarView: BottomAppBar by lazy {
        requireActivity().findViewById(R.id.bottomBar)
    }
    private val newRecipeBtn: FloatingActionButton by lazy {
        requireActivity().findViewById(R.id.newRecipeBtn)
    }
    private lateinit var binding: FragmentRecipePagerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipePagerBinding.inflate(layoutInflater)
        if (isTablet/* && args.firstLoad*/) viewModel.getRecipe(1)
        else viewModel.getRecipe(args.recipeID)

        // Cargo la receta nueva o la existente en el viewModel compartido por los fragmentos del viewpager
        Log.d(GENERAL, "PG - Receta ID ${args.recipeID}")

        binding.pager.adapter = RecipePager(this, args.isEditable)
        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.recipe)
                else -> getString(R.string.ings)
            }
        }.attach()

        binding.topBar.run {
            navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.recipeID == -1) {
            enterTransition = MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.newRecipeBtn)
                endView = binding.recipePager
                scrimColor = Color.TRANSPARENT
                endContainerColor = resources.getColor(R.color.white)
                startContainerColor = resources.getColor(R.color.white)
                containerColor = resources.getColor(R.color.white)
                duration = 300
            }
            returnTransition = Slide().apply {
                duration = 225
                addTarget(R.id.recipe_pager)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        bottomBarView.visibility = View.VISIBLE
        bottomBarView.performShow()
    }
}


class RecipePager(fragment: Fragment, private val isEditable: Boolean) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (isEditable) {
            when (position) {
                0 -> RecipeDetailEditFragment.newInstance()
                else -> RecipeIngsEditFragment.newInstance()
            }
        } else {
            when (position) {
                0 -> RecipeDetailFragment.newInstance()
                else -> RecipeIngsFragment.newInstance()
            }
        }
    }
}