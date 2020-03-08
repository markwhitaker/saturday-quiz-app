package uk.co.mainwave.saturdayquizapp.repository

import android.content.SharedPreferences
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import uk.co.mainwave.saturdayquizapp.di.PREFS_NAME_SCORES
import uk.co.mainwave.saturdayquizapp.model.QuestionScore
import java.text.SimpleDateFormat
import java.util.Date

class ScoresRepository : KoinComponent {
    private val sharedPreferences: SharedPreferences by inject(named(PREFS_NAME_SCORES))

    fun setDate(date: Date) {
        val dateString = DATE_FORMAT.format(date)
        sharedPreferences.getString(KEY_DATE, null)?.let { storedDateString ->
            if (storedDateString != dateString) {
                sharedPreferences.edit().clear().apply()
            }
        }
        sharedPreferences
            .edit()
            .putString(KEY_DATE, dateString)
            .apply()
    }

    fun getScore(questionNumber: Int): QuestionScore = QuestionScore.valueOf(
        sharedPreferences.getFloat("$KEY_QUESTION_PREFIX$questionNumber", 0f)
    )

    fun setScore(questionNumber: Int, score: QuestionScore) =
        sharedPreferences
            .edit()
            .putFloat("$KEY_QUESTION_PREFIX$questionNumber", score.value)
            .apply()

    companion object {
        private const val KEY_DATE = "date"
        private const val KEY_QUESTION_PREFIX = "question-"
        private val DATE_FORMAT = SimpleDateFormat.getDateInstance()
    }
}
