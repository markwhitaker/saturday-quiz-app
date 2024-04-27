package uk.co.mainwave.saturdayquizapp.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import uk.co.mainwave.saturdayquizapp.R

// ScoreButton class, extending android.View
class ScoreButton : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        inflate(context, R.layout.view_score_button, this)
    }
}