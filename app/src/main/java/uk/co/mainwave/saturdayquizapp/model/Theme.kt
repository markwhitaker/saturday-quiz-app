package uk.co.mainwave.saturdayquizapp.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import uk.co.mainwave.saturdayquizapp.R

enum class Theme(
    @param:ColorRes val foreground: Int,
    @param:ColorRes val foregroundHighlight: Int,
    @param:ColorRes val foregroundDimmed: Int,
    @param:ColorRes val foregroundVeryDimmed: Int,
    @param:DrawableRes val dotsDrawable: Int,
    val dialRotation: Float
) {
    LIGHT(
        R.color.light_foreground,
        R.color.light_foreground_highlight,
        R.color.light_foreground_dimmed,
        R.color.light_foreground_very_dimmed,
        R.drawable.ic_dots_light,
        30f
    ),
    MEDIUM(
        R.color.medium_foreground,
        R.color.medium_foreground_highlight,
        R.color.medium_foreground_dimmed,
        R.color.medium_foreground_very_dimmed,
        R.drawable.ic_dots_medium,
        0f
    ),
    DARK(
        R.color.dark_foreground,
        R.color.dark_foreground_highlight,
        R.color.dark_foreground_dimmed,
        R.color.dark_foreground_very_dimmed,
        R.drawable.ic_dots_dark,
        -30f
    );

    companion object {
        val default = MEDIUM
    }
}
