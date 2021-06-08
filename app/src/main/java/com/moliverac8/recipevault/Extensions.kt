package com.moliverac8.recipevault

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.drawable.Drawable
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.moliverac8.domain.DishType

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return RecipeVaultApplication.instance.getString(stringRes, *formatArgs)
    }
}

object Drawables {
    fun get(@DrawableRes drawableRes: Int): Drawable? {
        return RecipeVaultApplication.instance.getDrawable(drawableRes)
    }
}

fun Activity.hideSoftKeyboard() {
    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}

fun List<DishType>.toListOfString(): List<String> = map { it.name }