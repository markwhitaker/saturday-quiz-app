package uk.co.mainwave.saturdayquizapp.presenter

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.model.Theme
import uk.co.mainwave.saturdayquizapp.mvp.MvpPresenter
import uk.co.mainwave.saturdayquizapp.mvp.MvpView
import uk.co.mainwave.saturdayquizapp.repository.PreferencesRepository
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import java.util.Date
import kotlin.coroutines.CoroutineContext

class QuizPresenter(
    private val repository: QuizRepository,
    private val prefsRepository: PreferencesRepository,
    private val uiDispatcher: CoroutineContext
) : MvpPresenter<QuizPresenter.View>(),
    QuizRepository.Listener {

    private val scenes = mutableListOf<Scene>()
    private var sceneIndex = 0
    private var timerJob: Job? = null

    override fun onViewDisplayed() {
        view.setTheme(prefsRepository.theme)
        view.showLoading()

        scenes.clear()
        sceneIndex = 0
        repository.loadQuiz(this)
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

    fun onUp() {
        when (prefsRepository.theme) {
            Theme.LIGHT -> return
            Theme.MEDIUM -> setTheme(Theme.LIGHT)
            Theme.DARK -> setTheme(Theme.MEDIUM)
        }
    }

    fun onDown() {
        when (prefsRepository.theme) {
            Theme.LIGHT -> setTheme(Theme.MEDIUM)
            Theme.MEDIUM -> setTheme(Theme.DARK)
            Theme.DARK -> return
        }
    }

    private fun setTheme(theme: Theme) {
        prefsRepository.theme = theme
        view.setTheme(theme)
        view.showThemeTip(theme)

        timerJob?.cancel()
        timerJob = launch {
            delay(prefsRepository.themeTipTimeoutMs)
            if (isActive) {
                withContext(uiDispatcher) {
                    view.hideThemeTip()
                }
            }
        }
    }

    private fun buildScenes(quiz: Quiz) {
        // First just show the questions
        scenes.add(
            Scene.QuestionsTitleScene(
                quiz.date
            )
        )
        quiz.questions.forEach { question ->
            scenes.add(
                Scene.QuestionScene(
                    question
                )
            )
        }
        // Then show each question again, then its answer
        scenes.add(Scene.AnswersTitleScene)
        quiz.questions.forEach { question ->
            scenes.add(
                Scene.QuestionScene(
                    question
                )
            )
            scenes.add(
                Scene.QuestionAnswerScene(
                    question
                )
            )
        }
        scenes.add(Scene.EndTitleScene)
    }

    private fun showScene() {
        when (val scene = scenes[sceneIndex]) {
            is Scene.QuestionsTitleScene -> {
                view.showQuestionsTitle(scene.date)
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
        class QuestionsTitleScene(val date: Date?) : Scene()
        object AnswersTitleScene : Scene()
        class QuestionScene(val question: Question) : Scene()
        class QuestionAnswerScene(val question: Question) : Scene()
        object EndTitleScene : Scene()
    }

    interface View : MvpView {
        fun showLoading()
        fun hideLoading()
        fun showQuestionsTitle(date: Date?)
        fun showAnswersTitle()
        fun showEndTitle()
        fun hideTitle()
        fun showNumber(number: Int)
        fun showQuestion(question: String, isWhatLinks: Boolean)
        fun showAnswer(answer: String)
        fun setTheme(theme: Theme)
        fun showThemeTip(theme: Theme)
        fun hideThemeTip()
        fun quit()
    }
}
