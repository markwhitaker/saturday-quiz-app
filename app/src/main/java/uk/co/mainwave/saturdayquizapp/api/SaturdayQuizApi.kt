package uk.co.mainwave.saturdayquizapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import uk.co.mainwave.saturdayquizapp.model.Quiz

interface SaturdayQuizApi {
    @GET("quiz")
    fun getLatestQuiz(
//        @Query(value = "id")
//        id: String = "lifeandstyle/2019/nov/16/quiz-tamara-in-a-green-bugatti-crow-red-legs-agatha-christie-thomas-eaton",

        @Query(value = "delaySeconds")
        delaySeconds: Int = 0
    ): Call<Quiz>
}
