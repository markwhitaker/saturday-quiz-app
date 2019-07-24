package uk.co.mainwave.saturdayquizapp.api

import retrofit2.Call
import retrofit2.http.GET
import uk.co.mainwave.saturdayquizapp.model.Quiz

interface SaturdayQuizApi {
    @GET("/quiz")
    fun getLatestQuiz() : Call<Quiz>
}
