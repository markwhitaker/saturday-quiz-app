package uk.co.mainwave.saturdayquizapp.view

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.KeyEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.view_question.*
import kotlinx.android.synthetic.main.view_score.*
import kotlinx.android.synthetic.main.view_theme_tip.*
import kotlinx.android.synthetic.main.view_title.*
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.mainwave.saturdayquizapp.R
import uk.co.mainwave.saturdayquizapp.model.QuestionScore
import uk.co.mainwave.saturdayquizapp.model.Theme
import uk.co.mainwave.saturdayquizapp.tools.hide
import uk.co.mainwave.saturdayquizapp.tools.remove
import uk.co.mainwave.saturdayquizapp.tools.setColour
import uk.co.mainwave.saturdayquizapp.tools.show
import uk.co.mainwave.saturdayquizapp.tools.showIf
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
            KeyEvent.KEYCODE_DPAD_UP ->
                viewModel.onUp()
            KeyEvent.KEYCODE_DPAD_DOWN ->
                viewModel.onDown()
            else ->
                return super.onKeyUp(keyCode, event)
        }
        return true
    }

    private fun setTheme(theme: Theme) {
        titleView.setColour(theme.foreground)
        questionView.setColour(theme.foreground)
        numberView.setColour(theme.foreground)
        answerView.setColour(theme.foregroundHighlight)
        quizDateView.setColour(theme.foregroundHighlight)
        whatLinksView.setColour(theme.foregroundDimmed)
        scoreDimmedRingView.setColour(theme.foregroundVeryDimmed)
        scoreHighlightRingView.setColour(theme.foregroundHighlight)
        scoreTickView.setColour(theme.foregroundHighlight)
        totalScoreView.setColour(theme.foregroundHighlight)
    }

    private fun showThemeTip(theme: Theme) {
        val tintList = ColorStateList.valueOf(resources.getColor(theme.foreground, null))
        themeTipDots.apply {
            setImageResource(theme.dotsDrawable)
            imageTintList = tintList
        }
        themeTipDial.apply {
            imageTintList = tintList
            animate()
                .rotation(theme.dialRotation)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(TIP_DIAL_ROTATE_DURATION_MS)
                .start()
        }
        themeTipView
            .animate()
            .alpha(1f)
            .setInterpolator(AccelerateInterpolator())
            .setDuration(TIP_FADE_IN_DURATION_MS)
            .start()
    }

    private fun hideThemeTip() {
        themeTipView
            .animate()
            .alpha(0f)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(TIP_FADE_OUT_DURATION_MS)
            .start()
    }

    private fun connectViewModel() {
        val activity = this
        viewModel.apply {
            showLoading.observe(activity) { show ->
                if (show) {
                    loadingView.show()
                } else {
                    loadingView.remove()
                }
            }

            quizDate.observe(activity) { date ->
                if (date != null) {
                    quizDateView.text =
                        DateFormat.getDateInstance(DateFormat.LONG, Locale.UK).format(date)
                    quizDateView.show()
                } else {
                    quizDateView.remove()
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
                titleLayout.remove()
            }

            answerHtml.observe(activity) { answerHtml ->
                answerView.text = fromHtml(answerHtml)
            }

            questionScore.observe(activity) { score ->
                if (score == null) {
                    scoreLayout.hide()
                } else {
                    scoreLayout.show()
                    scoreDimmedRingView.showIf(score != QuestionScore.FULL)
                    scoreHighlightRingView.showIf(score == QuestionScore.FULL)
                    scoreTickView.showIf(score != QuestionScore.NONE)
                }
            }

            totalScore.observe(activity) { score ->
                if (score == null) {
                    totalScoreView.hide()
                } else {
                    totalScoreView.apply {
                        text = getString(R.string.total_score_format, score.toPrettyString())
                        show()
                    }
                }
            }

            isWhatLinks.observe(activity) { isWhatLinks ->
                whatLinksView.showIf(isWhatLinks)
            }

            theme.observe(activity) { theme ->
                setTheme(theme)
            }

            themeTip.observe(activity) { theme ->
                if (theme != null) {
                    showThemeTip(theme)
                } else {
                    hideThemeTip()
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

    companion object {
        const val TIP_FADE_IN_DURATION_MS = 50L
        const val TIP_FADE_OUT_DURATION_MS = 300L
        const val TIP_DIAL_ROTATE_DURATION_MS = 200L
    }
}
