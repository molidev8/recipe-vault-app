package com.moliverac8.recipevault

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.use
import com.moliverac8.domain.DishType

/**
 * Provides access to string resources in any place of the codebase without needing the context
 */
object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return RecipeVaultApplication.instance.getString(stringRes, *formatArgs)
    }
}

/**
 * Provides access to drawables resources in any place of the codebase without needing the context
 */
object Drawables {
    fun get(@DrawableRes drawableRes: Int): Drawable? {
        return RecipeVaultApplication.instance.getDrawable(drawableRes)
    }
}

/**
 * Extension function of the Activity class that hides the keyboard after the user pressed enter
 */
fun Activity.hideSoftKeyboard() {
    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}

/**
 * Extension functions of List<DishType> that converts the list to String type
 */
fun List<DishType>.toListOfString(): List<String> = map { it.name }