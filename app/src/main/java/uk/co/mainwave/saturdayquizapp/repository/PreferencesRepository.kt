package uk.co.mainwave.saturdayquizapp.repository

import android.content.SharedPreferences
import uk.co.mainwave.saturdayquizapp.model.ColourSet

class PreferencesRepository(private val sharedPreferences: SharedPreferences) {
    var colourSet: ColourSet
        get() {
            val colourSetName = sharedPreferences.getString(COLOUR_SET_KEY, null)
            return if (colourSetName != null) {
                ColourSet.valueOf(colourSetName)
            } else {
                ColourSet.default
            }
        }
        set(value) {
            sharedPreferences
                .edit()
                .putString(COLOUR_SET_KEY, value.name)
                .apply()
        }

    companion object {
        private const val COLOUR_SET_KEY = "colourSet"
    }
}