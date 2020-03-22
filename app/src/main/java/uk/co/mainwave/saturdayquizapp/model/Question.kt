package uk.co.mainwave.saturdayquizapp.model

data class Question(
    val number: Int,
    val type: QuestionType,
    val question: String,
    val questionHtml: String,
    val answer: String,
    val answerHtml: String
) {
    val isWhatLinks
        get() = type == QuestionType.WHAT_LINKS
}
