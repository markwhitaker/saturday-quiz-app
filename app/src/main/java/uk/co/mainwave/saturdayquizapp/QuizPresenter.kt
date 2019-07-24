package uk.co.mainwave.saturdayquizapp

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.co.mainwave.saturdayquizapp.api.SaturdayQuizApi
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionType
import uk.co.mainwave.saturdayquizapp.model.Quiz

class QuizPresenter {
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

        val retrofit = Retrofit.Builder()
            .baseUrl("https://saturday-quiz-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(SaturdayQuizApi::class.java).getLatestQuiz().enqueue(object : Callback<Quiz> {
            override fun onResponse(call: Call<Quiz>, response: Response<Quiz>) {
                questions = response.body()?.questions ?: throw Exception("Erk!")
                view.hideLoading()
                showQuestion()
            }

            override fun onFailure(call: Call<Quiz>, t: Throwable) {
                throw Exception("Double erk!", t)
            }
        })
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
        fun showLoading()
        fun hideLoading()
        fun showNumber(number: Int)
        fun showQuestion(question: String, showWhatLinksPrefix: Boolean)
        fun showAnswer(answer: String)
    }
}
