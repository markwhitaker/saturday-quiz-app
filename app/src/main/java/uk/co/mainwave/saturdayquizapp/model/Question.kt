package uk.co.mainwave.saturdayquizapp.model

data class Question(
    val number: Int,
    val type: QuestionType,
    val question: String,
    val answer: String
) {
    fun isWhatLinks() = type == QuestionType.WHAT_LINKS
}
