package com.moliverac8.recipevault.ui.common

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.Drawables
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.Strings
import com.moliverac8.recipevault.ui.recipeList.RecipeListAdapter
import com.moliverac8.recipevault.ui.recipeList.RecipeListVM

class SwipeToDeleteRecipeList(
    private val viewModel: RecipeListVM,
    private val adapter: RecipeListAdapter,
    private val fab: FloatingActionButton
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private lateinit var recipeToRemove: RecipeWithIng
    private lateinit var snackBar: Snackbar

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

        SwipeBackgroundHelper.paintDrawCommandToStart(c, viewHolder.itemView, R.drawable.ic_baseline_delete_24, dX)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.RIGHT) {
            with(viewHolder.absoluteAdapterPosition) {
                recipeToRemove = adapter.currentList[this]
                //viewModel.removeRecipeFromObservable(recipeToRemove)
                removeFromAdapterList(recipeToRemove)
                setupSnackbar(viewHolder.itemView) {
                    //viewModel.addRecipeFromObservable(recipeToRemove)
                    addToAdapterList(recipeToRemove)
                }
                snackBar.show()
            }
        }
    }

    private fun addToAdapterList(recipe: RecipeWithIng) {
        adapter.submitList(adapter.currentList.toMutableList().apply {
            add(recipe)
        })
    }

    private fun removeFromAdapterList(recipe: RecipeWithIng) {
        adapter.submitList(adapter.currentList.toMutableList().apply {
            remove(recipe)
        })
    }

    @SuppressLint("ShowToast")
    private fun setupSnackbar(view: View, undoAction: (View) -> Unit) {
        snackBar =
            Snackbar.make(view, Strings.get(R.string.recipe_removed), Snackbar.LENGTH_LONG).apply {
                setAction(Strings.get(R.string.undo), undoAction)
                anchorView = fab
                addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
                            viewModel.deleteRecipeOnDatabase(recipeToRemove)
                        }
                    }
                })
            }
    }
}