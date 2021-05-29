package com.moliverac8.recipevault.ui.common

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.fromHtml
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.moliverac8.domain.DietType
import com.moliverac8.domain.Ingredient
import com.moliverac8.domain.Recipe
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.Strings
import java.text.DateFormat
import java.util.*

@BindingAdapter("text")
fun TextView.setString(string: String?) {
    if (!string.isNullOrEmpty()) text = string
}

@BindingAdapter("timeToCook")
fun TextView.setTimeToCook(time: Int) {
    text = resources.getString(R.string.timeToCook, time)
}

@BindingAdapter("image")
fun ImageView.setImage(image: String?) {
    if (!image.isNullOrEmpty())
        Glide.with(this)
            .load(Uri.parse(image))
            .into(this)
}

@BindingAdapter("dietImage")
fun ImageView.setDietImage(recipe: Recipe?) {
    if (recipe != null) {
        val id = when (recipe.dietType) {
            DietType.REGULAR -> R.drawable.regular
            DietType.VEGETARIAN -> R.drawable.vegetarian
            else -> R.drawable.vegan
        }

        val uri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(id)
                    + '/' + resources.getResourceTypeName(id) + '/' + resources.getResourceEntryName(
                id
            )
        )

        Glide.with(this).load(uri).into(this)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("ingUnits")
fun TextView.setIngUnits(ingredient: Ingredient) {
    text = "${ingredient.quantity.toInt()} ${ingredient.unit}"
}

@BindingAdapter("localBackupTime")
fun TextView.setLocalBackupTime(date: Date) {
    text = if (date.time != 0L) {
        val localDate = android.text.format.DateFormat.format("E dd-MM-yyyy kk:mm:ss", date)
        fromHtml(Strings.get(R.string.local, localDate), HtmlCompat.FROM_HTML_MODE_LEGACY)
    } else Strings.get(R.string.no_local_backup_jet)
}

@BindingAdapter("cloudBackupTime")
fun TextView.setCloudBackupTime(date: Date) {
    text = if (date.time != 0L) {
        val localDate = android.text.format.DateFormat.format("E dd-MM-yyyy kk:mm:ss", date)
        fromHtml(Strings.get(R.string.cloud, localDate), HtmlCompat.FROM_HTML_MODE_LEGACY)

    }
    else Strings.get(R.string.backup_time_error)
}

@BindingAdapter("backupSize")
fun TextView.setBackupSize(sizeInBytes: Long) {
    val inMB = if (sizeInBytes != 0L) {
        (sizeInBytes.toFloat() / 1024F) / 1024F
    } else {
        0F
    }
    text = fromHtml(Strings.get(R.string.size, inMB), HtmlCompat.FROM_HTML_MODE_LEGACY)

}