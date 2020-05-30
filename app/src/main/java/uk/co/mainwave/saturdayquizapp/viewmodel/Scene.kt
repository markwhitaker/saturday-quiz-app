package uk.co.mainwave.saturdayquizapp.viewmodel

import uk.co.mainwave.saturdayquizapp.model.Question
import java.util.Date

sealed class Scene {
    class QuestionsTitleScene(val date: Date?) : Scene()
    object AnswersTitleScene : Scene()
    class QuestionScene(val question: Question) : Scene()
    class QuestionAnswerScene(val question: Question) : Scene()
    object EndTitleScene : Scene()
}
