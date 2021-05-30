package com.moliverac8.recipevault.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.dropbox.core.android.Auth
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.ActivityMainBinding
import com.moliverac8.recipevault.framework.workmanager.BackupUserData
import com.moliverac8.recipevault.framework.workmanager.DropboxManager
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import com.moliverac8.recipevault.ui.recipeList.RecipeListFragment
import com.moliverac8.recipevault.ui.recipeList.RecipeListFragmentDirections
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
    RecipePagerFragment.RecipePagerNavigateInterface {

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
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
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

    private fun setupBottomNavigation() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.fragmentMaster) as NavHostFragment).navController.also {
                it.addOnDestinationChangedListener(this)
            }
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

    }

    override fun navigateToNewRecipe() {
        navController.navigate(
            RecipeListFragmentDirections.actionRecipeListFragmentToRecipePagerFragment(
                -1,
                true
            )
        )
    }

    override fun navigateToExistingRecipe() {

    }

    override fun navigateHomeFromPager() {
        showBottomAppBar()
        navController.navigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (currentNavigationFragment is RecipePagerFragment) {
            navigateHomeFromPager()
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
                animateNavigationToRecipeList()
                binding.newRecipeBtn.show()
            }
            R.id.AccountFragment -> {
                animateNavigationToAccount()
                binding.newRecipeBtn.hide()
            }
            R.id.RecipePagerFragment -> {
                animateNavigationToNewRecipe()
                hideBottomAppBar()
            }
        }
    }
}