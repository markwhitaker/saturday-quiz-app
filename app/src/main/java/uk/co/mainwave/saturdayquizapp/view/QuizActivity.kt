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
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.mainwave.saturdayquizapp.R
import uk.co.mainwave.saturdayquizapp.databinding.ActivityQuizBinding
import uk.co.mainwave.saturdayquizapp.databinding.ViewLoadingBinding
import uk.co.mainwave.saturdayquizapp.databinding.ViewQuestionBinding
import uk.co.mainwave.saturdayquizapp.databinding.ViewScoreBinding
import uk.co.mainwave.saturdayquizapp.databinding.ViewThemeTipBinding
import uk.co.mainwave.saturdayquizapp.databinding.ViewTitleBinding
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

    private lateinit var quizActivity: ActivityQuizBinding
    private lateinit var loadingView: ViewLoadingBinding
    private lateinit var questionLayout: ViewQuestionBinding
    private lateinit var scoreLayout: ViewScoreBinding
    private lateinit var themeTipLayout: ViewThemeTipBinding
    private lateinit var titleLayout: ViewTitleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        quizActivity = ActivityQuizBinding.inflate(layoutInflater)
        loadingView = quizActivity.loadingView
        questionLayout = quizActivity.questionLayout
        scoreLayout = questionLayout.scoreLayout
        themeTipLayout = quizActivity.themeTipLayout
        titleLayout = quizActivity.titleLayout

        setContentView(quizActivity.root)
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
        titleLayout.titleView.setColour(theme.foreground)
        titleLayout.totalScoreView.setColour(theme.foregroundHighlight)
        titleLayout.quizDateView.setColour(theme.foregroundHighlight)
        questionLayout.questionView.setColour(theme.foreground)
        questionLayout.numberView.setColour(theme.foreground)
        questionLayout.answerView.setColour(theme.foregroundHighlight)
        questionLayout.whatLinksView.setColour(theme.foregroundDimmed)
        scoreLayout.scoreDimmedRingView.setColour(theme.foregroundVeryDimmed)
        scoreLayout.scoreHighlightRingView.setColour(theme.foregroundHighlight)
        scoreLayout.scoreTickView.setColour(theme.foregroundHighlight)
    }

    private fun showThemeTip(theme: Theme) {
        val tintList = ColorStateList.valueOf(resources.getColor(theme.foreground, null))
        themeTipLayout.themeTipDots.apply {
            setImageResource(theme.dotsDrawable)
            imageTintList = tintList
        }
        themeTipLayout.themeTipDial.apply {
            imageTintList = tintList
            animate()
                .rotation(theme.dialRotation)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(TIP_DIAL_ROTATE_DURATION_MS)
                .start()
        }
        themeTipLayout.themeTipView
            .animate()
            .alpha(1f)
            .setInterpolator(AccelerateInterpolator())
            .setDuration(TIP_FADE_IN_DURATION_MS)
            .start()
    }

    private fun hideThemeTip() {
        themeTipLayout.themeTipView
            .animate()
            .alpha(0f)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(TIP_FADE_OUT_DURATION_MS)
            .start()
    }

    private fun connectViewModel() {
        val activity = this
        viewModel.apply {
            showLoading.observe(activity, { show ->
                if (show) {
                    loadingView.root.show()
                } else {
                    loadingView.root.remove()
                }
            })

            quizDate.observe(activity, { date ->
                if (date != null) {
                    titleLayout.quizDateView.text =
                        DateFormat.getDateInstance(DateFormat.LONG, Locale.UK).format(date)
                    titleLayout.quizDateView.show()
                } else {
                    titleLayout.quizDateView.remove()
                }
            })

            titleResId.observe(activity, { titleResId ->
                titleLayout.titleView.setText(titleResId)
                titleLayout.root.show()
            })

            questionNumber.observe(activity, { number ->
                questionLayout.numberView.text = getString(R.string.question_number_format, number)
            })

            questionHtml.observe(activity, { questionHtml ->
                questionLayout.questionView.text = fromHtml(questionHtml)
                titleLayout.root.remove()
            })

            answerHtml.observe(activity, { answerHtml ->
                questionLayout.answerView.text = fromHtml(answerHtml)
            })

            questionScore.observe(activity, { score ->
                if (score == null) {
                    scoreLayout.root.hide()
                } else {
                    scoreLayout.root.show()
                    scoreLayout.scoreDimmedRingView.showIf(score != QuestionScore.FULL)
                    scoreLayout.scoreHighlightRingView.showIf(score == QuestionScore.FULL)
                    scoreLayout.scoreTickView.showIf(score != QuestionScore.NONE)
                }
            })

            totalScore.observe(activity, { score ->
                if (score == null) {
                    titleLayout.totalScoreView.hide()
                } else {
                    titleLayout.totalScoreView.apply {
                        text = getString(R.string.total_score_format, score.toPrettyString())
                        show()
                    }
                }
            })

            isWhatLinks.observe(activity, { isWhatLinks ->
                questionLayout.whatLinksView.showIf(isWhatLinks)
            })

            theme.observe(activity, { theme ->
                setTheme(theme)
            })

            themeTip.observe(activity, { theme ->
                if (theme != null) {
                    showThemeTip(theme)
                } else {
                    hideThemeTip()
                }
            })

            quit.observe(activity, { quit ->
                if (quit) {
                    finish()
                }
            })
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
