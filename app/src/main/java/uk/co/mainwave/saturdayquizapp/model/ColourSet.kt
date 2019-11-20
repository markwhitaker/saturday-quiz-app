package uk.co.mainwave.saturdayquizapp.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import uk.co.mainwave.saturdayquizapp.R

enum class ColourSet(
    @ColorRes val foreground: Int,
    @ColorRes val foregroundHighlight: Int,
    @ColorRes val foregroundDimmed: Int,
    @DrawableRes val icon: Int
) {
    LIGHT(
        R.color.light_foreground,
        R.color.light_foreground_highlight,
        R.color.light_foreground_dimmed,
        R.drawable.ic_light
    ),
    MEDIUM(
        R.color.medium_foreground,
        R.color.medium_foreground_highlight,
        R.color.medium_foreground_dimmed,
        R.drawable.ic_medium
    ),
    DARK(
        R.color.dark_foreground,
        R.color.dark_foreground_highlight,
        R.color.dark_foreground_dimmed,
        R.drawable.ic_dark
    );

    companion object {
        val default = LIGHT
    }
}
