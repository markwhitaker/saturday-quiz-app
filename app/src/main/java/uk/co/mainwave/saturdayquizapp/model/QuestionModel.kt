package uk.co.mainwave.saturdayquizapp.model

data class QuestionModel(
    val number: Int,
    val type: QuestionType,
    val question: String,
    val answer: String
) {
    val isWhatLinks
        get() = type == QuestionType.WHAT_LINKS
}
