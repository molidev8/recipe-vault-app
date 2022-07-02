package com.moliverac8.recipevault.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.Strings
import com.moliverac8.recipevault.ui.recipeList.RecipeListAdapter
import com.moliverac8.recipevault.ui.recipeList.RecipeListVM

class SwipeToDeleteRecipeList(
    private val context: Context,
    private val viewModel: RecipeListVM,
    private val adapter: RecipeListAdapter,
    private val fab: FloatingActionButton
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private lateinit var recipeToRemove: RecipeWithIng
    private lateinit var snackBar: Snackbar
    private val trashIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)
    private val circleColor = ContextCompat.getColor(context, R.color.deleteRed)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = circleColor }
    private val reverseSurfaceColor = ContextCompat.getColor(context, R.color.primaryTextColor)
    private val CIRCLE_ACCELERATION = 6f

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (dX == 0f) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        val left = viewHolder.itemView.left.toFloat()
        val top = viewHolder.itemView.top.toFloat()
        val right = viewHolder.itemView.right.toFloat()
        val bottom = viewHolder.itemView.bottom.toFloat()
        // Android draws with the center of the axis in the top left corner
        val width = right - left
        val height = bottom - top
        // Saves the canvas state to restore it at the end
        val saveCount = c.save()

        var iconColor = circleColor
        // Limits the child view to the space left by the viewholder while its swiped
        c.clipRect(left, top, left + dX, bottom)
        // ELEGIR OTRO COLOR CUANDO FUNCIONE
        c.drawColor(ContextCompat.getColor(context, R.color.colorSurfaceAccent))

        // The percentage the child view has swiped
        val progress = dX / width

        val swipeThreshold = getSwipeThreshold(viewHolder)
        val iconPopThreshold = swipeThreshold + 0.125f
        val iconPopFinishedThreshold = iconPopThreshold + 0.125f
        var circleRadius = 0f
        val iconScale: Float

        when (progress) {
            in 0f..swipeThreshold -> {
                iconScale = 1f - (progress * 0.2f)
            }
            else -> {
                // The radius is the progress relative to the swipeThreshold multiplied by the width and the acceleration
                // The usage of the width allows the radius to adapt to the different screen sizes dynamically in every device
                circleRadius = (progress - swipeThreshold) * width * CIRCLE_ACCELERATION
                iconColor = reverseSurfaceColor
                iconScale = when(progress) {
                    in iconPopThreshold..iconPopFinishedThreshold -> 1.2f - progress * 0.2f
                    else -> 1f
                }
            }
        }

        trashIcon?.let {
            // 64 is the padding of the icon, divided by 2 to get the center of the icon
            val centerInXAxis = left + 64 + it.intrinsicWidth / 2f
            val centerInYAxis = top + 64 + it.intrinsicHeight / 2f

            //Sets the position of the icon inside the child view
            it.setBounds(
                (centerInXAxis - it.intrinsicWidth * iconScale).toInt(),
                (centerInYAxis - it.intrinsicHeight * iconScale).toInt(),
                (centerInXAxis + it.intrinsicWidth * iconScale).toInt(),
                (centerInYAxis + it.intrinsicHeight * iconScale).toInt()
            )

            // Sets the color of the icon
            it.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

            if (circleRadius > 0) {
                c.drawCircle(centerInXAxis, centerInYAxis, circleRadius, circlePaint)
            }
            it.draw(c)
        }

        c.restoreToCount(saveCount)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.RIGHT) {
            with(viewHolder.absoluteAdapterPosition) {
                recipeToRemove = adapter.currentList[this]
                removeFromAdapterList(recipeToRemove)
                viewModel.deleteRecipeOnDatabase(recipeToRemove)
                setupSnackbar(viewHolder.itemView) {
                    viewModel.addRecipeToDatabase(recipeToRemove)
                    addToAdapterList(recipeToRemove)
                }
                snackBar.show()
            }
        }
    }

    private fun addToAdapterList(recipe: RecipeWithIng) {
        adapter.submitList(adapter.currentList.toMutableList().apply {
            add(recipe)
        }.sortedBy { it.domainRecipe.name })
    }

    private fun removeFromAdapterList(recipe: RecipeWithIng) {
        adapter.submitList(adapter.currentList.toMutableList().apply {
            remove(recipe)
        }.sortedBy { it.domainRecipe.name })
    }

    @SuppressLint("ShowToast")
    private fun setupSnackbar(view: View, undoAction: (View) -> Unit) {
        snackBar =
            Snackbar.make(view, Strings.get(R.string.recipe_removed), Snackbar.LENGTH_SHORT).apply {
                setAction(Strings.get(R.string.undo), undoAction)
                anchorView = fab
                addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
//                            viewModel.deleteRecipeOnDatabase(recipeToRemove)
                        }
                    }
                })
            }
    }
}