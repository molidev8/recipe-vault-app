package com.moliverac8.recipevault.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moliverac8.recipevault.ui.search.SearchVM
import com.moliverac8.usecases.GetAllRecipes

@Suppress("UNCHECKED_CAST")
class SearchVMFactory(
    private val getAllRecipes: GetAllRecipes
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        (SearchVM(getAllRecipes) as T)

}