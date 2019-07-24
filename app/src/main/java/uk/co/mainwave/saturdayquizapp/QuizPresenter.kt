package uk.co.mainwave.saturdayquizapp

import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionType

class QuizPresenter {
    private lateinit var view: View

    private enum class Pass {
        QUESTION,
        QUESTION_AND_ANSWER
    }

    private var currentPass = Pass.QUESTION
    private var currentQuestionIndex = 0

    private val questions = listOf(
        Question(
            1,
            QuestionType.NORMAL,
            "Question 1",
            "Answer 1"
        ),
        Question(
            2,
            QuestionType.NORMAL,
            "Question 2",
            "Answer 2"
        ),
        Question(
            3,
            QuestionType.WHAT_LINKS,
            "Question 3",
            "Answer 3"
        ),
        Question(
            4,
            QuestionType.WHAT_LINKS,
            "Question 4",
            "Answer 4"
        )
    )

    fun onViewCreated(view: View) {
        this.view = view
        showQuestion()
    }

    fun onViewDestroyed() {

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
        view.showAnswer(when (currentPass) {
            Pass.QUESTION_AND_ANSWER -> question.answer
            else -> ""}
        )
    }

    interface View {
        fun showNumber(number: Int)
        fun showQuestion(question: String, showWhatLinksPrefix: Boolean)
        fun showAnswer(answer: String)
    }
}
