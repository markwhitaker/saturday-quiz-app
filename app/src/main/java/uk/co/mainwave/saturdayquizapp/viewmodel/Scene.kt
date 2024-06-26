package uk.co.mainwave.saturdayquizapp.viewmodel

import uk.co.mainwave.saturdayquizapp.model.QuestionModel
import java.util.Date

sealed class Scene {
    class QuestionsTitleScene(val date: Date?) : Scene()
    data object AnswersTitleScene : Scene()
    class QuestionScene(val questionModel: QuestionModel) : Scene()
    class QuestionAnswerScene(val questionModel: QuestionModel) : Scene()
    data object EndTitleScene : Scene()
}
