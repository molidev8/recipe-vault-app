package com.moliverac8.recipevault

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import com.moliverac8.domain.DishType

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return RecipeVaultApplication.instance.getString(stringRes, *formatArgs)
    }
}

fun Activity.hideSoftKeyboard() {
    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}

fun List<DishType>.toListOfString(): List<String> = map { it.name }