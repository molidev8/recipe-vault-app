package com.moliverac8.recipevault.ui.common

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.moliverac8.domain.DietType
import com.moliverac8.domain.Ingredient
import com.moliverac8.domain.Recipe
import com.moliverac8.recipevault.R

@BindingAdapter("text")
fun TextView.setString(string: String) {
    text = string
}

@BindingAdapter("timeToCook")
fun TextView.setTimeToCook(time: Int) {
    text = resources.getString(R.string.timeToCook, time)
}

@BindingAdapter("image")
fun ImageView.setImage(image: String) {
    Glide.with(this)
        .load(Uri.parse(image))
        .into(this)
}

@BindingAdapter("dietImage")
fun ImageView.setDietImage(recipe: Recipe) {
    val id = when (recipe.dietType) {
        DietType.REGULAR -> R.drawable.regular
        DietType.VEGETARIAN -> R.drawable.vegetarian
        else -> R.drawable.vegan
    }

    val uri = Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(id)
                + '/' + resources.getResourceTypeName(id) + '/' + resources.getResourceEntryName(id)
    )

    Glide.with(this).load(uri).into(this)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("ingUnits")
fun TextView.setIngUnits(ingredient: Ingredient) {
    text = "${ingredient.quantity.toInt()} ${ingredient.unit}"
}