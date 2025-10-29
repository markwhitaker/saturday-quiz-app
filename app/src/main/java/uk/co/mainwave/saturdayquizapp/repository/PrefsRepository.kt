package uk.co.mainwave.saturdayquizapp.repository

import android.content.SharedPreferences
import uk.co.mainwave.saturdayquizapp.model.QuestionScore
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.model.Theme
import java.text.SimpleDateFormat
import java.util.Date
import androidx.core.content.edit

class PrefsRepository(
    private val sharedPreferences: SharedPreferences
) {
    private lateinit var scores: Array<QuestionScore>

    val totalScore: Float get() = scores
        .map { s -> s.value }
        .sum()

    var theme: Theme
        get() = sharedPreferences
            .getString(KEY_THEME, null)
            ?.let { Theme.valueOf(it) } ?: Theme.MEDIUM
        set(value) = sharedPreferences.edit {
            putString(KEY_THEME, value.name)
        }

    val themeTipTimeoutMs: Long
        get() = THEME_TIP_TIMEOUT_MS

    val hasScores: Boolean
        get() = scores.any { s -> s != QuestionScore.NONE }

    fun initialiseScores(quiz: Quiz) {
        val dateString = DATE_FORMAT.format(quiz.date ?: Date())
        sharedPreferences.getString(KEY_DATE, null)?.let {
            if (it != dateString) {
                sharedPreferences.edit {
                    remove(KEY_SCORES)
                }
            }
        }
        sharedPreferences.edit {
            putString(KEY_DATE, dateString)
        }

        scores = loadScores() ?: Array(quiz.questions.size) { QuestionScore.NONE }
    }

    fun getScore(questionNumber: Int): QuestionScore = scores[questionNumber - 1]

    fun setScore(questionNumber: Int, score: QuestionScore) {
        scores[questionNumber - 1] = score
        saveScores()
    }

    private fun loadScores(): Array<QuestionScore>? = sharedPreferences
        .getString(KEY_SCORES, null)
        ?.split(SCORE_SEPARATOR)
        ?.map { s -> QuestionScore.valueOf(s) }
        ?.toTypedArray()

    private fun saveScores() = sharedPreferences.edit {
        putString(KEY_SCORES, scores.joinToString(SCORE_SEPARATOR))
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
