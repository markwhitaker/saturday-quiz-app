package uk.co.mainwave.saturdayquizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uk.co.mainwave.saturdayquizapp.R
import uk.co.mainwave.saturdayquizapp.model.QuestionScore
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.model.Theme
import uk.co.mainwave.saturdayquizapp.repository.PrefsRepository
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import java.util.Date

class QuizViewModel(
    private val quizRepository: QuizRepository,
    private val prefsRepository: PrefsRepository
) : ViewModel(), QuizRepository.Listener {
    private val data = Data()
    private val scenes = mutableListOf<Scene>()
    private var sceneIndex = 0
    private var timerJob: Job? = null

    val showLoading: LiveData<Boolean> = data.showLoading
    val quizDate: LiveData<Date?> = data.quizDate
    val titleResId: LiveData<Int> = data.titleResId
    val questionNumber: LiveData<Int> = data.questionNumber
    val questionHtml: LiveData<String> = data.questionHtml
    val answerHtml: LiveData<String> = data.answerHtml
    val questionScore: LiveData<QuestionScore?> = data.questionScore
    val totalScore: LiveData<Float?> = data.totalScore
    val isWhatLinks: LiveData<Boolean> = data.isWhatLinks
    val theme: LiveData<Theme> = data.theme
    val themeTip: LiveData<Theme?> = data.themeTip
    val quit: LiveData<Boolean> = data.quit

    fun start() {
        data.theme.value = prefsRepository.theme
        data.showLoading.value = true

        scenes.clear()
        sceneIndex = 0
        quizRepository.loadQuiz(this)
    }

    override fun onQuizLoaded(quiz: Quiz) {
        prefsRepository.initialiseScores(quiz)
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

    fun toggleScore() {
        val scene = scenes[sceneIndex]
        if (scene !is Scene.QuestionAnswerScene) {
            return
        }

        val questionNumber = scene.questionModel.number
        val score = when (prefsRepository.getScore(questionNumber)) {
            QuestionScore.NONE -> QuestionScore.FULL
            QuestionScore.FULL -> QuestionScore.HALF
            QuestionScore.HALF -> QuestionScore.NONE
        }
        prefsRepository.setScore(questionNumber, score)
        data.questionScore.value = score
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
        scenes.add(
            Scene.QuestionsTitleScene(
                quiz.date
            )
        )
        // First just show the questions if we haven't stored scores for this quiz
        if (!prefsRepository.hasScores) {
            quiz.questions.forEach { question ->
                scenes.add(
                    Scene.QuestionScene(
                        question
                    )
                )
            }
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
                data.questionScore.value = null
                data.totalScore.value = null
            }
            is Scene.AnswersTitleScene -> {
                data.titleResId.value = R.string.title_answers
                data.quizDate.value = null
                data.questionScore.value = null
                data.totalScore.value = null
            }
            is Scene.QuestionScene -> {
                data.questionNumber.value = scene.questionModel.number
                data.questionHtml.value = scene.questionModel.question
                data.answerHtml.value = ""
                data.isWhatLinks.value = scene.questionModel.isWhatLinks
                data.questionScore.value = null
                data.totalScore.value = null
            }
            is Scene.QuestionAnswerScene -> {
                data.questionNumber.value = scene.questionModel.number
                data.questionHtml.value = scene.questionModel.question
                data.answerHtml.value = scene.questionModel.answer
                data.isWhatLinks.value = scene.questionModel.isWhatLinks
                data.questionScore.value = prefsRepository.getScore(scene.questionModel.number)
                data.totalScore.value = null
            }
            is Scene.EndTitleScene -> {
                data.titleResId.value = R.string.title_end
                data.questionScore.value = null
                data.totalScore.value = prefsRepository.totalScore
            }
        }
    }

    private class Data {
        val showLoading = MutableLiveData<Boolean>()
        val quizDate = MutableLiveData<Date?>()
        val titleResId = MutableLiveData<Int>()
        val questionNumber = MutableLiveData<Int>()
        val questionHtml = MutableLiveData<String>()
        val answerHtml = MutableLiveData<String>()
        val questionScore = MutableLiveData<QuestionScore?>()
        val totalScore = MutableLiveData<Float?>()
        val isWhatLinks = MutableLiveData<Boolean>()
        val theme = MutableLiveData<Theme>()
        val themeTip = MutableLiveData<Theme?>()
        val quit = MutableLiveData<Boolean>()
    }
}
