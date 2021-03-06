package es.uam.eps.tfg.menuPlanner.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.moliverac8.recipevault.R

class Dots (
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