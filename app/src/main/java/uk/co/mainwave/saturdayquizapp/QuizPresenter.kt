package uk.co.mainwave.saturdayquizapp

import uk.co.mainwave.saturdayquizapp.model.Question
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
        } else {
            view.quit()
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
        scenes.add(Scene.QuestionsTitleScene)
        quiz.questions.forEach { question ->
            scenes.add(Scene.QuestionScene(question))
        }
        // Recap and answers
        scenes.add(Scene.AnswersTitleScene)
        quiz.questions.forEach { question ->
            scenes.add(Scene.QuestionScene(question))
            scenes.add(Scene.QuestionAnswerScene(question))
        }
        scenes.add(Scene.EndTitleScene)
    }

    private fun showScene() {
        when (val scene = scenes[sceneIndex]) {
            is Scene.QuestionsTitleScene -> {
                view.showQuestionsTitle()
            }
            is Scene.AnswersTitleScene -> {
                view.showAnswersTitle()
            }
            is Scene.QuestionScene -> {
                view.hideTitle()
                view.showNumber(scene.question.number)
                view.showQuestion(scene.question.question, scene.question.isWhatLinks())
                view.showAnswer("")
            }
            is Scene.QuestionAnswerScene -> {
                view.hideTitle()
                view.showNumber(scene.question.number)
                view.showQuestion(scene.question.question, scene.question.isWhatLinks())
                view.showAnswer(scene.question.answer)
            }
            is Scene.EndTitleScene -> {
                view.showEndTitle()
            }
        }
    }

    private sealed class Scene {
        object QuestionsTitleScene : Scene()
        object AnswersTitleScene: Scene()
        class QuestionScene(val question: Question) : Scene()
        class QuestionAnswerScene(val question: Question) : Scene()
        object EndTitleScene : Scene()
    }

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showQuestionsTitle()
        fun showAnswersTitle()
        fun showEndTitle()
        fun hideTitle()
        fun showNumber(number: Int)
        fun showQuestion(question: String, showWhatLinksPrefix: Boolean)
        fun showAnswer(answer: String)
        fun quit()
    }
}
