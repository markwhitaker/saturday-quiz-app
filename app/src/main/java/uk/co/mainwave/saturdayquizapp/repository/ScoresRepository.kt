package uk.co.mainwave.saturdayquizapp.repository

import android.content.SharedPreferences
import uk.co.mainwave.saturdayquizapp.model.QuestionScore
import java.text.SimpleDateFormat
import java.util.Date

class ScoresRepository(
    private val sharedPreferences: SharedPreferences
) {
    fun setDate(date: Date) {
        val dateString = DATE_FORMAT.format(date)
        sharedPreferences.getString(KEY_DATE, null)?.let { storedDateString ->
            if (storedDateString != dateString) {
                val keysToRemove = sharedPreferences.all.keys.filter { k -> k.startsWith(KEY_SCORE_PREFIX) }
                val prefsEditor = sharedPreferences.edit()
                keysToRemove.forEach { key ->
                    prefsEditor.remove(key)
                }
                prefsEditor.apply()
            }
        }
        sharedPreferences
            .edit()
            .putString(KEY_DATE, dateString)
            .apply()
    }

    fun getScore(questionNumber: Int): QuestionScore = QuestionScore.valueOf(
        sharedPreferences.getFloat("$KEY_SCORE_PREFIX$questionNumber", 0f)
    )

    fun setScore(questionNumber: Int, score: QuestionScore) =
        sharedPreferences
            .edit()
            .putFloat("$KEY_SCORE_PREFIX$questionNumber", score.value)
            .apply()

    companion object {
        private const val KEY_DATE = "date"
        private const val KEY_SCORE_PREFIX = "score-question-"
        private val DATE_FORMAT = SimpleDateFormat.getDateInstance()
    }
}
