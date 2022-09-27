package uk.co.mainwave.saturdayquizapp.view

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.KeyEvent
import android.view.animation.*
import androidx.fragment.app.FragmentActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
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
import java.util.Date
import java.util.Locale

class QuizActivity : FragmentActivity() {

    private val viewModel: QuizViewModel by viewModel()
    private lateinit var whatLinksPrefix: String

    private lateinit var quizActivity: ActivityQuizBinding
    private lateinit var loadingLayout: ViewLoadingBinding
    private lateinit var questionLayout: ViewQuestionBinding
    private lateinit var scoreLayout: ViewScoreBinding
    private lateinit var themeTipLayout: ViewThemeTipBinding
    private lateinit var titleLayout: ViewTitleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        quizActivity = ActivityQuizBinding.inflate(layoutInflater)
        loadingLayout = quizActivity.loadingLayout
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

    override fun onStop() {
        super.onStop()
        finish()
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
        scoreLayout.scoreHighlightDiscView.setColour(theme.foregroundHighlight)
        scoreLayout.scoreTickViewHighlight.setColour(theme.foregroundHighlight)
        loadingLayout.loadingView.setColour(theme.foregroundHighlight)
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
            showLoading.observe(activity) { handleShowLoading(it) }
            quizDate.observe(activity) { handleQuizDate(it) }
            titleResId.observe(activity) { handleTitleResId(it) }
            questionNumber.observe(activity) { handleQuestionNumber(it) }
            questionHtml.observe(activity) { handleQuestionHtml(it) }
            answerHtml.observe(activity) { handleAnswerHtml(it) }
            questionScore.observe(activity) { handleQuestionScore(it) }
            totalScore.observe(activity) { handleTotalScore(it) }
            isWhatLinks.observe(activity) { handleIsWhatLinks(it) }
            theme.observe(activity) { setTheme(it) }
            themeTip.observe(activity) { handleThemeTip(it) }
            quit.observe(activity) { handleQuit(it) }
        }
    }

    private fun handleShowLoading(show: Boolean) = if (show) {
        AnimationUtils.loadAnimation(this, R.anim.ease_in_out_rotate).also { animation ->
            loadingLayout.loadingView.startAnimation(animation)
        }
        loadingLayout.root.show()
    } else {
        loadingLayout.root.remove()
        loadingLayout.loadingView.clearAnimation()
    }

    private fun handleQuizDate(date: Date?) = if (date != null) {
        titleLayout.quizDateView.text = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK).format(date)
        titleLayout.quizDateView.show()
    } else {
        titleLayout.quizDateView.remove()
    }

    private fun handleTitleResId(titleResId: Int) = titleLayout.apply {
        titleView.setText(titleResId)
        root.show()
    }

    private fun handleQuestionNumber(number: Int) {
        questionLayout.numberView.text = getString(R.string.question_number_format, number)
    }

    private fun handleQuestionHtml(questionHtml: String) {
        questionLayout.questionView.text = fromHtml(questionHtml)
        titleLayout.root.remove()
    }

    private fun handleAnswerHtml(answerHtml: String) {
        questionLayout.answerView.text = fromHtml(answerHtml)
    }

    private fun handleQuestionScore(score: QuestionScore?) = if (score == null) {
        scoreLayout.root.hide()
    } else {
        scoreLayout.root.show()
        scoreLayout.scoreHighlightDiscView.showIf(score == QuestionScore.FULL)
        scoreLayout.scoreTickViewDark.showIf(score == QuestionScore.FULL)
        scoreLayout.scoreTickViewHighlight.showIf(score == QuestionScore.HALF)
        scoreLayout.scoreDimmedRingView.showIf(score != QuestionScore.FULL)
    }

    private fun handleTotalScore(score: Float?) = if (score == null) {
        titleLayout.totalScoreView.hide()
    } else {
        titleLayout.totalScoreView.run {
            text = getString(R.string.total_score_format, score.toPrettyString())
            show()
        }
    }

    private fun handleIsWhatLinks(isWhatLinks: Boolean) =
        questionLayout.whatLinksView.showIf(isWhatLinks)

    private fun handleThemeTip(theme: Theme?) = if (theme != null) {
        showThemeTip(theme)
    } else {
        hideThemeTip()
    }

    private fun handleQuit(quit: Boolean) {
        if (quit) {
            finish()
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
