package com.moliverac8.recipevault.ui.recipeDetail

import androidx.viewpager2.widget.ViewPager2

interface RecipePagerNavigate {
    fun navigateHomeFromPager()
    fun navigateToDetailsFromEdit(pager2: ViewPager2)
}