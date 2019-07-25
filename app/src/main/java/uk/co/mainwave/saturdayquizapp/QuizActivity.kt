package uk.co.mainwave.saturdayquizapp

import android.app.Activity
import android.os.Bundle
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
            KeyEvent.KEYCODE_DPAD_RIGHT -> presenter.onNext()
            KeyEvent.KEYCODE_DPAD_LEFT -> presenter.onPrevious()
            else -> return super.onKeyDown(keyCode, event)
        }
        return true
    }

    override fun showLoading() {
        progressView.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressView.visibility = View.GONE
    }

    override fun showNumber(number: Int) {
        numberView.text = number.toString()
    }

    override fun showQuestion(
        question: String,
        showWhatLinksPrefix: Boolean
    ) {
        questionView.text = if (showWhatLinksPrefix) {
            "$whatLinksPrefix $question"
        } else {
            question
        }
    }

    override fun showAnswer(answer: String) {
        answerView.text = answer
    }

    override fun quit() {
        finish()
    }
}
