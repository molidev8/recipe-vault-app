package com.moliverac8.recipevault.ui.recipeDetail

import androidx.viewpager2.widget.ViewPager2

/**
 * Defines the navigation transactions from the RecipePagerFragment
 */
interface RecipePagerNavigate {
    fun navigateHomeFromPager()
    fun navigateToDetailsFromEdit(pager2: ViewPager2)
}