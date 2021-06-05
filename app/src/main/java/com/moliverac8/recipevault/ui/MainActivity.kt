package com.moliverac8.recipevault.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.widget.ViewPager2
import com.dropbox.core.android.Auth
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.ActivityMainBinding
import com.moliverac8.recipevault.framework.workmanager.BackupUserData
import com.moliverac8.recipevault.framework.workmanager.DropboxManager
import com.moliverac8.recipevault.ui.account.AccountFragment
import com.moliverac8.recipevault.ui.recipeDetail.RecipePager
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import com.moliverac8.recipevault.ui.recipeList.RecipeListFragment
import com.moliverac8.recipevault.ui.recipeList.RecipeListFragmentDirections
import com.moliverac8.recipevault.ui.search.SearchFragment
import com.moliverac8.recipevault.ui.search.SearchFragmentDirections
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

const val REQUEST_IMAGE_CAPTURE = 1

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener,
    RecipeListFragment.RecipeListNavigationInterface,
    RecipePagerFragment.RecipePagerNavigateInterface,
    SearchFragment.SearchFragmentNavigation {

    private val dropboxManager: DropboxManager by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            BackupUserData.BackupEntryPoint::class.java
        ).dropboxManager()
    }
    private lateinit var binding: ActivityMainBinding
    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragmentMaster)
            ?.childFragmentManager
            ?.fragments
            ?.first()
    private lateinit var navController: NavController

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BackupEntryPoint {
        fun dropboxManager(): DropboxManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.newRecipeBtn.apply {
            /*setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)*/
        }

        setupBottomNavigation()
    }

    private fun animateNavigationToAccount() {
        currentNavigationFragment?.exitTransition = MaterialFadeThrough()
    }

    private fun animateNavigationToRecipeList() {
        currentNavigationFragment?.exitTransition = MaterialFadeThrough()
    }

    private fun animateNavigationToNewRecipe() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false)
            reenterTransition = MaterialElevationScale(true)
        }
    }

    private fun animateNavigationToExistingRecipe() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false)
            reenterTransition = MaterialElevationScale(true)
        }
    }

    private fun animateNavigationToSearch() {
        currentNavigationFragment?.apply {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        }
    }

    private fun setupBottomNavigation() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.fragmentMaster) as NavHostFragment).navController.also {
                it.addOnDestinationChangedListener(this)
            }
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

    }

    override fun navigateToNewRecipe() {
        animateNavigationToNewRecipe()
        navController.navigate(
            RecipeListFragmentDirections.actionRecipeListFragmentToRecipePagerFragment(
                -1,
                true
            )
        )
    }

    override fun navigateToSearch() {
        navController.navigate(RecipeListFragmentDirections.actionRecipeListFragmentToSearchFragment())
    }

    override fun navigateToExistingRecipe(id: Int, recipeCard: View) {
        animateNavigationToExistingRecipe()
        val recipeDetailTransitionName = getString(R.string.recipe_card_detail_transition_name)
        val extras = FragmentNavigatorExtras(recipeCard to recipeDetailTransitionName)
        val directions = RecipeListFragmentDirections.actionRecipeListFragmentToRecipePagerFragment(
            id,
            false
        )
        navController.navigate(directions, extras)
    }

    override fun navigateHomeFromPager() {
        showBottomAppBar()
        navController.navigateUp()
    }

    override fun navigateHomeFromSearch() {
        showBottomAppBar()
        navController.navigateUp()
    }

    override fun navigateToSearchResult() {
        navController.navigate(SearchFragmentDirections.actionSearchFragmentToRecipeListFragment())
    }

    override fun navigateToDetailsFromEdit(pager2: ViewPager2) {
        currentNavigationFragment?.let {
            pager2.visibility = View.GONE
            pager2.adapter = RecipePager(it, false)
            pager2.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (currentNavigationFragment is RecipePagerFragment)
            (currentNavigationFragment as RecipePagerFragment).onBackPressed()
        else
            super.onBackPressed()
    }

    private fun hideBottomAppBar() {
        binding.bottomBar.performHide()
        binding.bottomBar.animate().setListener(object : AnimatorListenerAdapter() {
            var isCanceled = false
            override fun onAnimationEnd(animation: Animator?) {
                if (isCanceled) return

                binding.bottomBar.visibility = View.GONE
                binding.newRecipeBtn.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
                isCanceled = true
            }
        })
    }

    private fun showBottomAppBar() {
        binding.bottomBar.performShow()
        binding.bottomBar.visibility = View.VISIBLE
        binding.newRecipeBtn.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("recipe-vault", MODE_PRIVATE)

        val serializedCredential = prefs.getString("credential", null)

        if (serializedCredential == null) {
            val credential = Auth.getDbxCredential()

            if (credential != null) {
                prefs.edit().putString("credential", credential.toString()).apply()
                dropboxManager.initDropboxClient(credential)
            }
        }
    }

    /**
     * Prepara la transición entre elementos de la navegación inferior
     */
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.RecipeListFragment -> {
                if (currentNavigationFragment !is RecipeListFragment) {
                    binding.newRecipeBtn.show()
                    showBottomAppBar()
                    animateNavigationToRecipeList()
                }
            }
            R.id.AccountFragment -> {
                if (currentNavigationFragment !is AccountFragment) {
                    binding.newRecipeBtn.hide()
                    animateNavigationToAccount()
                }
            }
            R.id.RecipePagerFragment -> {
                hideBottomAppBar()
            }
            R.id.SearchFragment -> {
                animateNavigationToSearch()
                hideBottomAppBar()
            }
        }
    }
}