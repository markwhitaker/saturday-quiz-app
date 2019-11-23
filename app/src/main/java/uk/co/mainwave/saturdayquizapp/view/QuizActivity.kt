package uk.co.mainwave.saturdayquizapp.view

import android.app.Activity
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
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.view_colour_set_icon.*
import kotlinx.android.synthetic.main.view_question.*
import kotlinx.android.synthetic.main.view_title.*
import org.koin.android.ext.android.inject
import uk.co.mainwave.saturdayquizapp.R
import uk.co.mainwave.saturdayquizapp.model.ColourSet
import uk.co.mainwave.saturdayquizapp.presenter.QuizPresenter
import java.text.DateFormat
import java.util.Date
import java.util.Locale

class QuizActivity : Activity(), QuizPresenter.View {

    private val presenter: QuizPresenter by inject()
    private lateinit var whatLinksPrefix: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_quiz)
        whatLinksPrefix = getString(R.string.what_links_prefix)
        presenter.onViewCreated(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewDisplayed()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_CENTER ->
                presenter.onNext()
            KeyEvent.KEYCODE_DPAD_LEFT ->
                presenter.onPrevious()
            KeyEvent.KEYCODE_DPAD_UP ->
                presenter.onUp()
            KeyEvent.KEYCODE_DPAD_DOWN ->
                presenter.onDown()
            else ->
                return super.onKeyUp(keyCode, event)
        }
        return true
    }

    override fun showLoading() {
        loadingView.show()
    }

    override fun hideLoading() {
        loadingView.hide()
    }

    override fun showQuestionsTitle(date: Date?) {
        if (date != null) {
            quizDateView.text = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK).format(date)
            quizDateView.show()
        } else {
            quizDateView.hide()
        }
        titleView.setText(R.string.title_questions)
        titleLayout.show()
    }

    override fun showAnswersTitle() {
        quizDateView.hide()
        titleView.setText(R.string.title_answers)
        titleLayout.show()
    }

    override fun showEndTitle() {
        quizDateView.hide()
        titleView.setText(R.string.title_end)
        titleLayout.show()
    }

    override fun hideTitle() {
        titleLayout.hide()
    }

    override fun showNumber(number: Int) {
        numberView.text = getString(R.string.question_number_format, number)
    }

    override fun showQuestion(
        question: String,
        isWhatLinks: Boolean
    ) {
        whatLinksView.visibility = if (isWhatLinks) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        questionView.text = fromHtml(question)
    }

    override fun showAnswer(answer: String) {
        answerView.text = fromHtml(answer)
    }

    override fun setColours(colourSet: ColourSet) {
        titleView.setColour(colourSet.foreground)
        questionView.setColour(colourSet.foreground)
        numberView.setColour(colourSet.foreground)
        answerView.setColour(colourSet.foregroundHighlight)
        quizDateView.setColour(colourSet.foregroundHighlight)
        whatLinksView.setColour(colourSet.foregroundDimmed)
    }

    override fun showColoursTip(colourSet: ColourSet) {
        val tintList = ColorStateList.valueOf(resources.getColor(colourSet.foreground, null))
        colourSetIconDots.apply {
            setImageResource(colourSet.dotsDrawable)
            colourSetIconDots.supportImageTintList = tintList
        }
        colourSetIconDial.apply {
            supportImageTintList = tintList
            animate()
                .rotation(colourSet.dialRotation)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(TIP_DIAL_ROTATE_DURATION_MS)
                .start()
        }
        colourSetIconView
            .animate()
            .alpha(1f)
            .setInterpolator(AccelerateInterpolator())
            .setDuration(TIP_FADE_IN_DURATION_MS)
            .start()
    }

    override fun hideColoursTip() {
        colourSetIconView
            .animate()
            .alpha(0f)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(TIP_FADE_OUT_DURATION_MS)
            .start()
    }

    override fun quit() {
        finish()
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