package uk.co.mainwave.saturdayquizapp

import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionType
import uk.co.mainwave.saturdayquizapp.model.Quiz
import javax.inject.Inject

class QuizPresenter @Inject constructor(
    private val repository: QuizRepository
) : QuizRepository.Listener {
    private lateinit var view: View

    private enum class Pass {
        QUESTION,
        QUESTION_AND_ANSWER
    }

    private var currentPass = Pass.QUESTION
    private var currentQuestionIndex = 0

    private lateinit var questions: List<Question>

    fun onViewCreated(view: View) {
        this.view = view
        view.showLoading()
        repository.loadQuiz(this)
    }

    fun onViewDestroyed() {
    }

    override fun onQuizLoaded(quiz: Quiz) {
        questions = quiz.questions
        view.hideLoading()
        showQuestion()
    }

    override fun onQuizLoadFailed() {
        view.quit()
    }

    fun onNext() {
        if (currentQuestionIndex == questions.lastIndex) {
            currentPass = when (currentPass) {
                Pass.QUESTION -> Pass.QUESTION_AND_ANSWER
                else -> return
            }
            currentQuestionIndex = 0
        } else {
            currentQuestionIndex++
        }
        showQuestion()
    }

    fun onPrevious() {
        if (currentQuestionIndex == 0) {
            currentPass = when (currentPass) {
                Pass.QUESTION_AND_ANSWER -> Pass.QUESTION
                else -> return
            }
            currentQuestionIndex = questions.lastIndex
        } else {
            currentQuestionIndex--
        }
        showQuestion()
    }

    private fun showQuestion() {
        val question = questions[currentQuestionIndex]
        view.showNumber(question.number)
        view.showQuestion(question.question, question.type == QuestionType.WHAT_LINKS)
        view.showAnswer(
            when (currentPass) {
                Pass.QUESTION_AND_ANSWER -> question.answer
                else -> ""
            }
        )
    }

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showNumber(number: Int)
        fun showQuestion(question: String, showWhatLinksPrefix: Boolean)
        fun showAnswer(answer: String)
        fun quit()
    }
}
