package com.moliverac8.recipevault.ui.common

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.recipevault.R
import kotlin.math.abs

class SwipeBackgroundHelper {

    companion object {

        private const val THRESHOLD = 4

        private const val OFFSET_PX = 40

        @JvmStatic
        fun paintDrawCommandToStart(canvas: Canvas, viewItem: View, @DrawableRes iconResId: Int, dX: Float) {
            val drawCommand = createDrawCommand(viewItem, dX, iconResId)
            paintDrawCommand(drawCommand, canvas, dX, viewItem)
        }

        private fun createDrawCommand(viewItem: View, dX: Float, iconResId: Int): DrawCommand {
            val context = viewItem.context
            var icon = ContextCompat.getDrawable(context, iconResId)
            // Wrap the drawable so that it has a tintable background and mutate it so that its changes
            // doesn't affect other drawables instances with the same drawable origin xml file
            icon = DrawableCompat.wrap(icon!!).mutate()
            // Make the pixels of the drawable white
            icon.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.primaryTextColor),
                PorterDuff.Mode.SRC_IN)
            val backgroundColor = getBackgroundColor(R.color.deleteRed, R.color.colorSurface, dX, viewItem)
            return DrawCommand(icon, backgroundColor)
        }

        /**
         * Sets the background color depending on the X axis translation of the view
         */
        private fun getBackgroundColor(firstColor: Int, secondColor: Int, dX: Float, viewItem: View): Int {
            return when (willActionBeTriggered(dX, viewItem.width)) {
                true -> ContextCompat.getColor(viewItem.context, firstColor)
                false -> ContextCompat.getColor(viewItem.context, secondColor)
            }
        }

        /**
         * Determines if the view has moved in the X axis above the calculated threshold
         */
        private fun willActionBeTriggered(dX: Float, viewWidth: Int): Boolean {
            return abs(dX) >= viewWidth / THRESHOLD
        }

        private fun paintDrawCommand(drawCommand: DrawCommand, canvas: Canvas, dX: Float, viewItem: View) {
            drawBackground(canvas, viewItem, dX, drawCommand.backgroundColor)
            drawIcon(canvas, viewItem, dX, drawCommand.icon)
        }


        private fun drawIcon(canvas: Canvas, viewItem: View, dX: Float, icon: Drawable) {
            val topMargin = calculateTopMargin(icon, viewItem)
//            icon.bounds = getStartContainerRectangle(viewItem, icon.intrinsicWidth, topMargin, OFFSET_PX, dX)
            icon.bounds = Rect(OFFSET_PX, viewItem.top + OFFSET_PX, icon.intrinsicWidth + OFFSET_PX, viewItem.top + icon.intrinsicHeight + OFFSET_PX)
            icon.draw(canvas)
        }

        /**
         * Calculates the margin for the bottom and top of the icon so that it stays vertically centered
         */
        private fun calculateTopMargin(icon: Drawable, viewItem: View): Int {
            return (viewItem.height - icon.intrinsicHeight) / 2
        }

        /**
         *
         */
        private fun getStartContainerRectangle(viewItem: View, iconWidth: Int, topMargin: Int, sideOffset: Int,
                                               dx: Float): Rect {
            val leftBound = viewItem.left + dx.toInt() + sideOffset
            val rightBound = viewItem.left + dx.toInt() + iconWidth + sideOffset
            val topBound = viewItem.top + topMargin
            val bottomBound = viewItem.bottom - topMargin

            Log.d(GENERAL, "$dx ------- ${Rect(leftBound, topBound, rightBound, bottomBound)}")

            return Rect(leftBound, topBound, rightBound, bottomBound)
        }


        /**
         * Sets the color of the background and its bounds
         */
        private fun drawBackground(canvas: Canvas, viewItem: View, dX: Float, color: Int) {
            // Paint contains the style and color to use later in a canvas object
            val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            backgroundPaint.color = color
            val backgroundRectangle = getBackGroundRectangle(viewItem, dX)
            canvas.drawRect(backgroundRectangle, backgroundPaint)
        }

        /**
         * Gets the rectangle in which the background it's being drawn (in this case the background
         * grows to the left so negative X axis)
         * Left bound of background matches the left bound of the view + dX (which is 0 in the beginning)
         * and the right bound of the background matches the left bound of the view always
         */
        private fun getBackGroundRectangle(viewItem: View, dX: Float): RectF {
            return RectF(viewItem.left.toFloat() + dX, viewItem.top.toFloat(), viewItem.left.toFloat(),
                viewItem.bottom.toFloat())
        }
    }

    private class DrawCommand(val icon: Drawable, val backgroundColor: Int)

}