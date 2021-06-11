package com.moliverac8.recipevault.ui.common

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.NestedScrollingChild3
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.moliverac8.domain.Ingredient
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.IngUnitDialogBinding
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM

class Dots(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setBackgroundResource(R.drawable.dotted_line)
    }
}

class IngQuantityDialog(private val viewModel: RecipeDetailVM, private val ing: Ingredient? = null) : DialogFragment() {

    lateinit var binding: IngUnitDialogBinding

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IngUnitDialogBinding.inflate(layoutInflater)
        val UNITS = arrayOf(
            getString(R.string.uds),
            getString(R.string.grams),
            getString(R.string.liters),
            getString(R.string.cups)
        )

        isCancelable = false

        val options = ArrayAdapter(requireContext(), R.layout.ing_unit_dropdown, UNITS)
        binding.options.apply {
            setText(UNITS[0])
            setAdapter(options)
        }
        val range = mutableListOf<Int>()

        repeat(1001) { idx ->
            range.add(idx + 1)
        }

        binding.number.apply {
            displayedValues = range.map { it.toString() }.toTypedArray()
            maxValue = 1000
            minValue = 1
        }

        val watcher = { cont: TextInputLayout ->
            { text: Editable? ->
                if (text.isNullOrBlank()) {
                    cont.error = getString(R.string.required_text)
                } else {
                    cont.isErrorEnabled = false
                }
            }
        }
        val focusListener = { cont: TextInputLayout ->
            View.OnFocusChangeListener { v, hasFocus ->
                with(v as EditText) {
                    if (!hasFocus) {
                        if (text.isNullOrBlank()) {
                            cont.error = getString(R.string.required_text)
                        } else {
                            cont.isErrorEnabled = false
                        }
                    }
                }
            }
        }

        ing?.let {
            binding.ingEdit.setText(it.name)
            binding.number.value = it.quantity.toInt()
            binding.menu.editText?.setText(it.unit)
        }

        binding.ingEdit.apply {
            doAfterTextChanged(watcher(binding.ing))
            onFocusChangeListener = focusListener(binding.ing)
        }

        binding.save.setOnClickListener {
            if (binding.ingEdit.text.toString().isNotBlank()) {
                binding.number.clearFocus()
                val id = ing?.id ?: -1
                viewModel.updateDialogIng(
                    Ingredient(
                        id,
                        binding.ingEdit.text.toString(),
                        binding.menu.editText?.text.toString(),
                        binding.number.value.toDouble()
                    )
                )
                dismiss()
            } else {
                if (binding.ingEdit.text.toString().isEmpty()) {
                    binding.ingEdit.error = getString(R.string.required_text)
                }
            }
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}

class NestedCoordinatorLayout : CoordinatorLayout, NestedScrollingChild3 {

    private var helper: NestedScrollingChildHelper

    constructor(context: Context) : super(context) {
        helper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        helper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        helper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    override fun isNestedScrollingEnabled(): Boolean = helper.isNestedScrollingEnabled

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        helper.isNestedScrollingEnabled = enabled
    }

    override fun hasNestedScrollingParent(type: Int): Boolean =
        helper.hasNestedScrollingParent(type)

    override fun hasNestedScrollingParent(): Boolean = helper.hasNestedScrollingParent()

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        val superResult = super.onStartNestedScroll(child, target, axes, type)
        return startNestedScroll(axes, type) || superResult
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean {
        val superResult = super.onStartNestedScroll(child, target, axes)
        return startNestedScroll(axes) || superResult
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val superConsumed = intArrayOf(0, 0)
        super.onNestedPreScroll(target, dx, dy, superConsumed, type)
        val thisConsumed = intArrayOf(0, 0)
        dispatchNestedPreScroll(dx, dy, consumed, null, type)
        consumed[0] = superConsumed[0] + thisConsumed[0]
        consumed[1] = superConsumed[1] + thisConsumed[1]
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        val superConsumed = intArrayOf(0, 0)
        super.onNestedPreScroll(target, dx, dy, superConsumed)
        val thisConsumed = intArrayOf(0, 0)
        dispatchNestedPreScroll(dx, dy, consumed, null)
        consumed[0] = superConsumed[0] + thisConsumed[0]
        consumed[1] = superConsumed[1] + thisConsumed[1]
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, type)
        super.onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        super.onStopNestedScroll(target, type)
        stopNestedScroll(type)
    }

    override fun onStopNestedScroll(target: View) {
        super.onStopNestedScroll(target)
        stopNestedScroll()
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        val superResult = super.onNestedPreFling(target, velocityX, velocityY)
        return dispatchNestedPreFling(velocityX, velocityY) || superResult
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        val superResult = super.onNestedFling(target, velocityX, velocityY, consumed)
        return dispatchNestedFling(velocityX, velocityY, consumed) || superResult
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean =
        helper.startNestedScroll(axes, type)

    override fun startNestedScroll(axes: Int): Boolean = helper.startNestedScroll(axes)

    override fun stopNestedScroll(type: Int) {
        helper.stopNestedScroll(type)
    }

    override fun stopNestedScroll() {
        helper.stopNestedScroll()
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray
    ) {
        helper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type,
            consumed
        )
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
        offsetInWindow: IntArray?, type: Int
    ): Boolean = helper.dispatchNestedScroll(
        dxConsumed,
        dyConsumed,
        dxUnconsumed,
        dyUnconsumed,
        offsetInWindow,
        type
    )

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?
    ): Boolean = helper.dispatchNestedScroll(
        dxConsumed,
        dyConsumed,
        dxUnconsumed,
        dyUnconsumed,
        offsetInWindow
    )

    override fun dispatchNestedPreScroll(
        dx: Int, dy: Int, consumed: IntArray?,
        offsetInWindow: IntArray?, type: Int
    ): Boolean = helper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean =
        helper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean =
        helper.dispatchNestedFling(velocityX, velocityY, consumed)

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean =
        helper.dispatchNestedPreFling(velocityX, velocityY)
}