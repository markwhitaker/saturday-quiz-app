package uk.co.mainwave.saturdayquizapp

import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionType
import uk.co.mainwave.saturdayquizapp.model.Quiz
import javax.inject.Inject

class QuizPresenter @Inject constructor(
    private val repository: QuizRepository
) : QuizRepository.Listener {

    private lateinit var view: View

    private val scenes = mutableListOf<Scene>()
    private var sceneIndex = 0
    fun onViewCreated(view: View) {
        this.view = view
        view.showLoading()
        repository.loadQuiz(this)
    }

    fun onViewDestroyed() {
    }

    override fun onQuizLoaded(quiz: Quiz) {
        buildScenes(quiz)
        view.hideLoading()
        showScene()
    }

    override fun onQuizLoadFailed() {
        view.quit()
    }

    fun onNext() {
        if (sceneIndex != scenes.lastIndex) {
            sceneIndex++
            showScene()
        }
    }

    fun onPrevious() {
        if (sceneIndex != 0) {
            sceneIndex--
            showScene()
        }
    }

    private fun buildScenes(quiz: Quiz) {
        // First view of questions
        quiz.questions.forEach { question ->
            scenes.add(Scene.QuestionScene(question))
        }
        // Recap and answers
        quiz.questions.forEach { question ->
            scenes.add(Scene.QuestionScene(question))
            scenes.add(Scene.QuestionAnswerScene(question))
        }
    }

    private fun showScene() {
        when (val scene = scenes[sceneIndex]) {
            is Scene.QuestionScene -> {
                view.showNumber(scene.question.number)
                view.showQuestion(scene.question.question, scene.question.type == QuestionType.WHAT_LINKS)
                view.showAnswer("")
            }
            is Scene.QuestionAnswerScene -> {
                view.showNumber(scene.question.number)
                view.showQuestion(scene.question.question, scene.question.type == QuestionType.WHAT_LINKS)
                view.showAnswer(scene.question.answer)
            }
        }
    }

    private sealed class Scene {
        class QuestionScene(val question: Question) : Scene()
        class QuestionAnswerScene(val question: Question) : Scene()
    }

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showNumber(number: Int)
        fun showQuestion(question: String, showWhatLinksPrefix: Boolean)
        fun showAnswer(answer: String)
        fun quit()
    }
}
