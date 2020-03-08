package uk.co.mainwave.saturdayquizapp.view

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.view_question.*
import kotlinx.android.synthetic.main.view_theme_tip.*
import kotlinx.android.synthetic.main.view_title.*
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.mainwave.saturdayquizapp.R
import uk.co.mainwave.saturdayquizapp.model.QuestionScore
import uk.co.mainwave.saturdayquizapp.model.Theme
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
            KeyEvent.KEYCODE_DPAD_CENTER ->
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

            questionText.observe(activity) { questionText ->
                questionView.text = fromHtml(questionText)
                titleLayout.hide()
            }

            answerText.observe(activity) { answerText ->
                answerView.text = answerText
            }

            questionScore.observe(activity) { score ->
                if (score == null) {
                    scoreLayout.visibility = View.INVISIBLE
                } else {
                    scoreLayout.visibility = View.VISIBLE
                    when (score) {
                        QuestionScore.NONE -> {
                            val tintList = ColorStateList.valueOf(resources.getColor(R.color.medium_foreground_dimmed, null))
                            scoreRingView.imageTintList = tintList
                            scoreTickView.visibility = View.INVISIBLE
                        }
                        QuestionScore.HALF -> {
                            val ringTintList = ColorStateList.valueOf(resources.getColor(R.color.medium_foreground_dimmed, null))
                            scoreRingView.imageTintList = ringTintList
                            val tickTintList = ColorStateList.valueOf(resources.getColor(R.color.medium_foreground_highlight, null))
                            scoreTickView.apply {
                                visibility = View.VISIBLE
                                imageTintList = tickTintList
                            }
                        }
                        QuestionScore.FULL -> {
                            val tintList = ColorStateList.valueOf(resources.getColor(R.color.medium_foreground_highlight, null))
                            scoreRingView.imageTintList = tintList
                            scoreTickView.apply {
                                visibility = View.VISIBLE
                                imageTintList = tintList
                            }
                        }
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

    private fun TextView.setColour(@ColorRes colorResId: Int) {
        val colour = resources.getColor(colorResId, null)
        setTextColor(colour)
        compoundDrawableTintList = ColorStateList.valueOf(colour)
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

    companion object {
        const val TIP_FADE_IN_DURATION_MS = 50L
        const val TIP_FADE_OUT_DURATION_MS = 300L
        const val TIP_DIAL_ROTATE_DURATION_MS = 200L
    }
}
