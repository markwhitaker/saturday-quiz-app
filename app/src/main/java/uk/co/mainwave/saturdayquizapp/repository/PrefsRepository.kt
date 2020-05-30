package uk.co.mainwave.saturdayquizapp.repository

import android.content.SharedPreferences
import uk.co.mainwave.saturdayquizapp.model.QuestionScore
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.model.Theme
import java.text.SimpleDateFormat
import java.util.Date

class PrefsRepository(
    private val sharedPreferences: SharedPreferences
) {
    private lateinit var scores: Array<QuestionScore>

    val totalScore: Float get() = scores
        .map { s -> s.value }
        .sum()

    var theme: Theme
        get() {
            val themeName = sharedPreferences.getString(KEY_THEME, null)
            return if (themeName != null) {
                Theme.valueOf(themeName)
            } else {
                Theme.default
            }
        }
        set(value) {
            sharedPreferences
                .edit()
                .putString(KEY_THEME, value.name)
                .apply()
        }

    val themeTipTimeoutMs: Long
        get() = THEME_TIP_TIMEOUT_MS

    fun initialiseScores(quiz: Quiz) {
        val dateString = DATE_FORMAT.format(quiz.date ?: Date())
        sharedPreferences.getString(KEY_DATE, null)?.let { storedDateString ->
            if (storedDateString != dateString) {
                sharedPreferences
                    .edit()
                    .remove(KEY_SCORES)
                    .apply()
            }
        }
        sharedPreferences
            .edit()
            .putString(KEY_DATE, dateString)
            .apply()

        scores = loadScores() ?: Array(quiz.questions.size) { QuestionScore.NONE }
    }

    fun getScore(questionNumber: Int): QuestionScore = scores[questionNumber - 1]

    fun setScore(questionNumber: Int, score: QuestionScore) {
        scores[questionNumber - 1] = score
        saveScores()
    }

    private fun loadScores(): Array<QuestionScore>? {
        return sharedPreferences
            .getString(KEY_SCORES, null)
            ?.split(SCORE_SEPARATOR)
            ?.map { s -> QuestionScore.valueOf(s) }
            ?.toTypedArray()
    }

    private fun saveScores() {
        val scoresString = scores.joinToString(SCORE_SEPARATOR)
        sharedPreferences
            .edit()
            .putString(KEY_SCORES, scoresString)
            .apply()
    }

    companion object {
        private const val KEY_DATE = "date"
        private const val KEY_SCORES = "scores"
        private const val KEY_THEME = "theme"
        private const val SCORE_SEPARATOR = ","
        private const val THEME_TIP_TIMEOUT_MS = 2000L
        private val DATE_FORMAT = SimpleDateFormat.getDateInstance()
    }
}
