package uk.co.mainwave.saturdayquizapp

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import uk.co.mainwave.saturdayquizapp.api.SaturdayQuizApi
import uk.co.mainwave.saturdayquizapp.model.Quiz

class QuizRepository(
    private val retrofit: Retrofit
) : Callback<Quiz> {
    private lateinit var listener: Listener

    fun loadQuiz(listener: Listener) {
        this.listener = listener
        retrofit
            .create(SaturdayQuizApi::class.java)
            .getLatestQuiz()
            .enqueue(this)
    }

    override fun onResponse(call: Call<Quiz>, response: Response<Quiz>) {
        val quiz = response.body()
        if (response.isSuccessful && quiz != null) {
            listener.onQuizLoaded(quiz)
        } else {
            listener.onQuizLoadFailed()
        }
    }

    override fun onFailure(call: Call<Quiz>, t: Throwable) {
        listener.onQuizLoadFailed()
    }

    interface Listener {
        fun onQuizLoaded(quiz: Quiz)
        fun onQuizLoadFailed()
    }
}
