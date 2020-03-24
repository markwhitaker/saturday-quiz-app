package uk.co.mainwave.saturdayquizapp.view

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.view_question.*
import kotlinx.android.synthetic.main.view_title.*
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.mainwave.saturdayquizapp.R
import uk.co.mainwave.saturdayquizapp.model.QuestionScore
import uk.co.mainwave.saturdayquizapp.tools.toPrettyString
import uk.co.mainwave.saturdayquizapp.viewmodel.QuizViewModel
import java.text.DateFormat
import java.util.Locale

class QuizActivity : FragmentActivity() {

    private val viewModel: QuizViewModel by viewModel()
    private lateinit var whatLinksPrefix: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_quiz)
        whatLinksPrefix = getString(R.string.what_links_prefix)

        connectViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_SPACE ->
                viewModel.toggleScore()
            KeyEvent.KEYCODE_DPAD_RIGHT ->
                viewModel.onNext()
            KeyEvent.KEYCODE_DPAD_LEFT ->
                viewModel.onPrevious()
            else ->
                return super.onKeyUp(keyCode, event)
        }
        return true
    }

    private fun connectViewModel() {
        val activity = this
        viewModel.apply {
            showLoading.observe(activity) { show ->
                if (show) {
                    loadingView.show()
                } else {
                    loadingView.hide()
                }
            }

            quizDate.observe(activity) { date ->
                if (date != null) {
                    quizDateView.text =
                        DateFormat.getDateInstance(DateFormat.LONG, Locale.UK).format(date)
                    quizDateView.show()
                } else {
                    quizDateView.hide()
                }
            }

            titleResId.observe(activity) { titleResId ->
                titleView.setText(titleResId)
                titleLayout.show()
            }

            questionNumber.observe(activity) { number ->
                numberView.text = getString(R.string.question_number_format, number)
            }

            questionHtml.observe(activity) { questionHtml ->
                questionView.text = fromHtml(questionHtml)
                titleLayout.hide()
            }

            answerHtml.observe(activity) { answerHtml ->
                answerView.text = fromHtml(answerHtml)
            }

            questionScore.observe(activity) { score ->
                if (score == null) {
                    scoreLayout.visibility = View.INVISIBLE
                } else {
                    scoreLayout.visibility = View.VISIBLE
                    when (score) {
                        QuestionScore.NONE -> {
                            val tintList = ColorStateList.valueOf(resources.getColor(R.color.foreground_very_dimmed, null))
                            scoreRingView.imageTintList = tintList
                            scoreTickView.visibility = View.INVISIBLE
                        }
                        QuestionScore.HALF -> {
                            val ringTintList = ColorStateList.valueOf(resources.getColor(R.color.foreground_very_dimmed, null))
                            scoreRingView.imageTintList = ringTintList
                            val tickTintList = ColorStateList.valueOf(resources.getColor(R.color.foreground_highlight, null))
                            scoreTickView.apply {
                                visibility = View.VISIBLE
                                imageTintList = tickTintList
                            }
                        }
                        QuestionScore.FULL -> {
                            val tintList = ColorStateList.valueOf(resources.getColor(R.color.foreground_highlight, null))
                            scoreRingView.imageTintList = tintList
                            scoreTickView.apply {
                                visibility = View.VISIBLE
                                imageTintList = tintList
                            }
                        }
                    }
                }
            }

            totalScore.observe(activity) { score ->
                if (score == null) {
                    totalScoreView.visibility = View.INVISIBLE
                } else {
                    totalScoreView.apply {
                        text = getString(R.string.total_score_format, score.toPrettyString())
                        visibility = View.VISIBLE
                    }
                }
            }

            isWhatLinks.observe(activity) { isWhatLinks ->
                whatLinksView.visibility = if (isWhatLinks) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }

            quit.observe(activity) { quit ->
                if (quit) {
                    finish()
                }
            }
        }
    }

    private fun fromHtml(text: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(text)
        }
    }

    private fun View.show() {
        visibility = View.VISIBLE
    }

    private fun View.hide() {
        visibility = View.GONE
    }
}
