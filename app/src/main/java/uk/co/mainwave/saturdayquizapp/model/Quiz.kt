package uk.co.mainwave.saturdayquizapp.model

import java.util.Date

data class Quiz(
    val id: String,
    val date: Date?,
    val title: String?,
    val questions: List<Question>
)
