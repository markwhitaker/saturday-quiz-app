package uk.co.mainwave.saturdayquizapp.model

enum class QuestionScore(
    val value: Float
) {
    NONE(0f),
    HALF(0.5f),
    FULL(1f);

    companion object {
        fun valueOf(value: Float): QuestionScore = values().single { score -> score.value == value }
    }
}
