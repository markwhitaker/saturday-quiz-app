package uk.co.mainwave.saturdayquizapp.repository

import android.content.SharedPreferences
import uk.co.mainwave.saturdayquizapp.model.Theme

class PreferencesRepository(private val sharedPreferences: SharedPreferences) {
    var theme: Theme
        get() {
            val themeName = sharedPreferences.getString(THEME_KEY, null)
            return if (themeName != null) {
                Theme.valueOf(themeName)
            } else {
                Theme.default
            }
        }
        set(value) {
            sharedPreferences
                .edit()
                .putString(THEME_KEY, value.name)
                .apply()
        }

    val themeTipTimeoutMs: Long
        get() = THEME_TIP_TIMEOUT_MS

    companion object {
        private const val THEME_KEY = "theme"
        private const val THEME_TIP_TIMEOUT_MS = 2000L
    }
}
