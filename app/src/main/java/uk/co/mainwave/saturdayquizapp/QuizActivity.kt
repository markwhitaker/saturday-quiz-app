package uk.co.mainwave.saturdayquizapp

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.KeyEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.view_question.*
import kotlinx.android.synthetic.main.view_title.*
import org.koin.android.ext.android.inject
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

    override fun quit() {
        finish()
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
