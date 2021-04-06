package es.uam.eps.tfg.menuPlanner.util

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.ArrayAdapter
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moliverac8.domain.Ingredient
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.IngUnitDialogBinding
import com.moliverac8.recipevault.databinding.ItemIngEditListBinding
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.edit.RecipeIngsEditAdapter

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

class IngQuantityDialog(
    private val adapter: RecipeIngsEditAdapter,
    private val pos: Int,
    private val ings: MutableList<Ingredient>
) : DialogFragment() {

    lateinit var binding: IngUnitDialogBinding

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        if (window != null) {
            val size = Point()

            val display = window.windowManager.defaultDisplay
            display.getSize(size)
            val width: Int = size.x

            window.setLayout((width * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.CENTER)
        }
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

        binding.save.setOnClickListener {
            val ing = ings[pos]
            ings[pos] = Ingredient(
                ing.id,
                ing.name,
                binding.menu.editText?.text.toString(),
                binding.number.value.toDouble()
            )
            adapter.notifyItemChanged(pos)
            onStop()
        }

        binding.cancel.setOnClickListener {
            onStop()
        }

        return binding.root
    }
}