package com.moliverac8.recipevault

import androidx.annotation.StringRes

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return RecipeVaultApplication.instance.getString(stringRes, *formatArgs)
    }
}