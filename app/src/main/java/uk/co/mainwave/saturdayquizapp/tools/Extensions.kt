package uk.co.mainwave.saturdayquizapp.tools

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes

fun Float.toPrettyString(): String = toInt().toString() + if (this % 1f == 0.5f) "½" else ""

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.remove() {
    visibility = View.GONE
}

fun View.showIf(condition: Boolean) {
    if (condition) {
        show()
    } else {
        hide()
    }
}

fun View.setColour(@ColorRes colorResId: Int) {
    val colour = resources.getColor(colorResId, null)
    val tintList = ColorStateList.valueOf(colour)

    when (this) {
        is TextView -> {
            setTextColor(colour)
            compoundDrawableTintList = tintList
        }
        is ImageView -> {
            imageTintList = tintList
        }
    }
}
