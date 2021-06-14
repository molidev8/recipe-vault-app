package com.moliverac8.recipevault.ui.search

/**
 * Defines the navigation transactions from the SearchFragment
 */
interface SearchFragmentNavigation {
    fun navigateHomeFromSearch()
    fun navigateToDetails(id: Int)
}