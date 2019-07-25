package uk.co.mainwave.saturdayquizapp

import android.app.Activity
import android.os.Bundle
import android.text.Html
import android.view.KeyEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_quiz.*
import javax.inject.Inject

class QuizActivity : Activity(), QuizPresenter.View {

    @Inject
    internal lateinit var presenter : QuizPresenter
    private lateinit var whatLinksPrefix: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).component.inject(this)

        setContentView(R.layout.activity_quiz)
        whatLinksPrefix = getString(R.string.what_links_prefix)
        presenter.onViewCreated(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroyed()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_CENTER ->
                presenter.onNext()
            KeyEvent.KEYCODE_DPAD_LEFT ->
                presenter.onPrevious()
            else ->
                return super.onKeyDown(keyCode, event)
        }
        return true
    }

    override fun showLoading() {
        progressView.show()
    }

    override fun hideLoading() {
        progressView.hide()
    }

    override fun showQuestionsTitle() {
        titleView.setText(R.string.title_questions)
        titleView.show()
    }

    override fun showAnswersTitle() {
        titleView.setText(R.string.title_answers)
        titleView.show()
    }

    override fun showEndTitle() {
        titleView.setText(R.string.title_end)
        titleView.show()
    }

    override fun hideTitle() {
        titleView.hide()
    }

    override fun showNumber(number: Int) {
        numberView.text = number.toString()
    }

    override fun showQuestion(
        question: String,
        showWhatLinksPrefix: Boolean
    ) {
        val questionText = if (showWhatLinksPrefix) {
            "$whatLinksPrefix $question"
        } else {
            question
        }

        questionView.text = Html.fromHtml(questionText, Html.FROM_HTML_MODE_COMPACT)
    }

    override fun showAnswer(answer: String) {
        answerView.text = Html.fromHtml(answer, Html.FROM_HTML_MODE_COMPACT)
    }

    override fun quit() {
        finish()
    }

    private fun View.show() {
        visibility = View.VISIBLE
    }

    private fun View.hide() {
        visibility = View.GONE
    }
}
