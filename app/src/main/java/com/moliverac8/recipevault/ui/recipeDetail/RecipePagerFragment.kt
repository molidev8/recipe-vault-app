package com.moliverac8.recipevault.ui.recipeDetail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialContainerTransform
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipePagerBinding
import com.moliverac8.recipevault.themeColor
import com.moliverac8.recipevault.ui.common.CustomOnBackPressedInterface
import com.moliverac8.recipevault.ui.recipeDetail.edit.RecipeDetailEditFragment
import com.moliverac8.recipevault.ui.recipeDetail.edit.RecipeIngsEditFragment
import com.moliverac8.recipevault.ui.recipeDetail.ingredients.RecipeIngsFragment
import com.moliverac8.recipevault.ui.recipeDetail.instructions.RecipeDetailFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipePagerFragment : Fragment(), RecipeDetailFragment.DetailToEditNavigateInterface,
    CustomOnBackPressedInterface {

    private val args by navArgs<RecipePagerFragmentArgs>()
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { this })
    private lateinit var binding: FragmentRecipePagerBinding
    private val newRecipeBtn: FloatingActionButton by lazy {
        requireActivity().findViewById(R.id.newRecipeBtn)
    }
    private val goBackLogic = {
        // Si es para crear una nueva receta
        if (!viewModel.amIEditing) {
            if (args.isEditable) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.user_confirmation)
                    .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                        (activity as RecipePagerNavigate).navigateHomeFromPager()
                    }
                    .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

                    }
                    .show()
            } else {
                (activity as RecipePagerNavigate).navigateHomeFromPager()
            }
        }
        // Si es para editar una receta existente
        else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.user_confirmation)
                .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                    viewModel.amIEditing = false
                    showSaveButton(false)
                    (activity as RecipePagerNavigate).navigateToDetailsFromEdit(binding.pager)
                }
                .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

                }
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipePagerBinding.inflate(layoutInflater)
        viewModel.getRecipe(args.recipeID)

        // Cargo la receta nueva o la existente en el viewModel compartido por los fragmentos del viewpager

        if (args.isEditable) showSaveButton(true)
        else showSaveButton(false)

        binding.pager.adapter = RecipePager(this, args.isEditable)
        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.recipe)
                else -> getString(R.string.ings)
            }
        }.attach()

        binding.topBar.run {
            navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                goBackLogic()
            }
        }

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.recipeID == -1) {
            enterTransition = MaterialContainerTransform().apply {
                startView = newRecipeBtn
                endView = binding.recipePager
                scrimColor = Color.TRANSPARENT
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }
            returnTransition = Slide().apply {
                addTarget(R.id.recipe_pager)
                resources.getInteger(R.integer.motion_duration_large).toLong()
            }
        } else {
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                drawingViewId = R.id.fragmentMaster
                scrimColor = Color.TRANSPARENT
                endContainerColor = resources.getColor(R.color.white)
            }
        }
    }

    override fun navigateToEdit(prepareNavigation: () -> Unit) {
        viewModel.amIEditing = true
        showSaveButton(true)
        prepareNavigation()
    }

    private fun showSaveButton(show: Boolean) {
        binding.topBar.menu.getItem(0).isVisible = show
    }

    override fun onBackPressed() {
        goBackLogic()
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