package uk.co.mainwave.saturdayquizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uk.co.mainwave.saturdayquizapp.R
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.model.Theme
import uk.co.mainwave.saturdayquizapp.repository.PreferencesRepository
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import java.util.Date


class QuizViewModel(
    private val repository: QuizRepository,
    private val prefsRepository: PreferencesRepository
) : ViewModel(), QuizRepository.Listener {
    private val data = Data()
    private val scenes = mutableListOf<Scene>()
    private var sceneIndex = 0
    private var timerJob: Job? = null

    val showLoading: LiveData<Boolean> = data.showLoading
    val quizDate: LiveData<Date?> = data.quizDate
    val titleResId: LiveData<Int> = data.titleResId
    val questionNumber: LiveData<Int> = data.questionNumber
    val questionText: LiveData<String> = data.questionText
    val answerText: LiveData<String> = data.answerText
    val isWhatLinks: LiveData<Boolean> = data.isWhatLinks
    val theme: LiveData<Theme> = data.theme
    val quit: LiveData<Boolean> = data.quit
    val themeTip: LiveData<Theme?> = data.themeTip

    fun start() {
        data.theme.value = prefsRepository.theme
        data.showLoading.value = true

        scenes.clear()
        sceneIndex = 0
        repository.loadQuiz(this)
    }

    override fun onQuizLoaded(quiz: Quiz) {
        buildScenes(quiz)
        data.showLoading.value = false
        showScene()
    }

    override fun onQuizLoadFailed() {
        data.quit.value = true
    }

    fun onNext() {
        if (sceneIndex != scenes.lastIndex) {
            sceneIndex++
            showScene()
        } else {
            data.quit.value = true
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
        data.theme.value = theme
        data.themeTip.value = theme

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            delay(prefsRepository.themeTipTimeoutMs)
            if (isActive) {
                data.themeTip.value = null
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
                data.titleResId.value = R.string.title_questions
                data.quizDate.value = scene.date
            }
            is Scene.AnswersTitleScene -> {
                data.titleResId.value = R.string.title_answers
                data.quizDate.value = null
            }
            is Scene.QuestionScene -> {
                data.questionNumber.value = scene.question.number
                data.questionText.value = scene.question.question
                data.isWhatLinks.value = scene.question.isWhatLinks
                data.answerText.value = null
            }
            is Scene.QuestionAnswerScene -> {
                data.questionNumber.value = scene.question.number
                data.questionText.value = scene.question.question
                data.answerText.value = scene.question.answer
                data.isWhatLinks.value = scene.question.isWhatLinks
            }
            is Scene.EndTitleScene -> {
                data.titleResId.value = R.string.title_end
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

    private class Data {
        val showLoading = MutableLiveData<Boolean>(false)
        val quizDate = MutableLiveData<Date?>()
        val titleResId = MutableLiveData<Int>()
        val questionNumber = MutableLiveData<Int>()
        val questionText = MutableLiveData<String>()
        val answerText = MutableLiveData<String>()
        val isWhatLinks = MutableLiveData<Boolean>(false)
        val theme = MutableLiveData<Theme>()
        val themeTip = MutableLiveData<Theme?>()
        val quit = MutableLiveData<Boolean>(false)
    }
}