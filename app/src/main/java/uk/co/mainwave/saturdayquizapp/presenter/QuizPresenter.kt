package uk.co.mainwave.saturdayquizapp.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uk.co.mainwave.saturdayquizapp.model.ColourSet
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.mvp.MvpPresenter
import uk.co.mainwave.saturdayquizapp.mvp.MvpView
import uk.co.mainwave.saturdayquizapp.repository.PreferencesRepository
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import java.util.Date

class QuizPresenter(
    private val repository: QuizRepository,
    private val prefsRepository: PreferencesRepository
) : MvpPresenter<QuizPresenter.View>(),
    QuizRepository.Listener {

    private val scenes = mutableListOf<Scene>()
    private var sceneIndex = 0
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    override fun onViewDisplayed() {
        view.setColours(prefsRepository.colourSet)
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
        when (prefsRepository.colourSet) {
            ColourSet.LIGHT -> return
            ColourSet.MEDIUM -> setColourSet(ColourSet.LIGHT)
            ColourSet.DARK -> setColourSet(ColourSet.MEDIUM)
        }
    }

    fun onDown() {
        when (prefsRepository.colourSet) {
            ColourSet.LIGHT -> setColourSet(ColourSet.MEDIUM)
            ColourSet.MEDIUM -> setColourSet(ColourSet.DARK)
            ColourSet.DARK -> return
        }
    }

    private fun setColourSet(colourSet: ColourSet) {
        prefsRepository.colourSet = colourSet
        view.setColours(colourSet)
        view.showColoursTip(colourSet)

        timerJob?.cancel()
        timerJob = uiScope.launch {
            delay(prefsRepository.colourSetTipTimeoutMs)
            if (isActive) {
                view.hideColoursTip()
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
        fun setColours(colourSet: ColourSet)
        fun showColoursTip(colourSet: ColourSet)
        fun hideColoursTip()
        fun quit()
    }
}
