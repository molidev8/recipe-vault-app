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
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.dropbox.core.android.Auth
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.ActivityMainBinding
import com.moliverac8.recipevault.framework.workmanager.BackupUserData
import com.moliverac8.recipevault.framework.workmanager.DropboxManager
import com.moliverac8.recipevault.ui.recipeDetail.RecipePager
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerNavigate
import com.moliverac8.recipevault.ui.recipeList.RecipeListFragmentDirections
import com.moliverac8.recipevault.ui.recipeList.RecipeListNavigation
import com.moliverac8.recipevault.ui.search.SearchFragmentDirections
import com.moliverac8.recipevault.ui.search.SearchFragmentNavigation
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener,
    RecipeListNavigation,
    RecipePagerNavigate,
    SearchFragmentNavigation {

    private lateinit var binding: ActivityMainBinding
    private val dropboxManager: DropboxManager by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            BackupUserData.BackupEntryPoint::class.java
        ).dropboxManager()
    }
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
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
        }

        setupBottomNavigation()
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

    /* ----------- NAVIGATION SETUP ----------- */
    /**
     * Prepares all the animated transition for the fragment transactions aside the ones inside
     * the RecipePagerFragment. Every transition must be called in the implementation of
     * onDestinationChanged() method
     */


    private fun animateNavigationToAccount() {
        currentNavigationFragment?.exitTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
    }

    private fun animateNavigationToRecipeList() {
        currentNavigationFragment?.exitTransition = MaterialFadeThrough()
    }

    private fun animateNavigationToNewRecipe() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false).apply {
                resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                resources.getInteger(R.integer.motion_duration_large).toLong()
            }
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
        binding.bottomNav.setupWithNavController(navController)

    }

    override fun onBackPressed() {
        if (currentNavigationFragment is RecipePagerFragment)
            (currentNavigationFragment as RecipePagerFragment).onBackPressed()
        else
            super.onBackPressed()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.RecipeListFragment -> {
                binding.newRecipeBtn.show()
                showBottomAppBar()
                animateNavigationToRecipeList()
            }
            R.id.AccountFragment -> {
                // FAB hides onViewCreated in the fragment to avoid stutter
                animateNavigationToAccount()
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

    /* ----------- RECIPE LIST NAVIGATION ----------- */
    /**
     * Manages the calls to the navigation component to launch the fragment transactions from the
     * list of recipes to all the available destinations from this fragment
     */

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

    /* ----------- RECIPE PAGER NAVIGATION ----------- */
    /**
     * Manages the transactions to get out of the RecipePagerFragment and the edit mode launched from
     * the RecipePagerFragment
     */

    override fun navigateHomeFromPager() {
        showBottomAppBar()
        navController.navigateUp()
    }

    override fun navigateToDetailsFromEdit(pager2: ViewPager2) {
        currentNavigationFragment?.let {
            pager2.visibility = View.GONE
            pager2.adapter = RecipePager(it, false)
            pager2.visibility = View.VISIBLE
        }
    }

    /* ----------- SEARCH NAVIGATION ----------- */
    /**
     * Manages the calls to the navigation component to launch the search recipe screen and access the
     * details of the recipes returned by the search query
     */

    override fun navigateHomeFromSearch() {
        showBottomAppBar()
        navController.navigateUp()
    }

    override fun navigateToDetails(id: Int) {
        navController.navigate(SearchFragmentDirections.actionSearchFragmentToRecipePagerFragment(id))
    }

}