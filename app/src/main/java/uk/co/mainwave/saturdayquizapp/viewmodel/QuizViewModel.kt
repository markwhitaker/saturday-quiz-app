package uk.co.mainwave.saturdayquizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uk.co.mainwave.saturdayquizapp.R
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionScore
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import uk.co.mainwave.saturdayquizapp.repository.ScoresRepository
import java.util.Date

class QuizViewModel(
    private val quizRepository: QuizRepository,
    private val scoresRepository: ScoresRepository
) : ViewModel(), QuizRepository.Listener {
    private val data = Data()
    private val scenes = mutableListOf<Scene>()
    private var sceneIndex = 0

    val showLoading: LiveData<Boolean> = data.showLoading
    val quizDate: LiveData<Date?> = data.quizDate
    val titleResId: LiveData<Int> = data.titleResId
    val questionNumber: LiveData<Int> = data.questionNumber
    val questionText: LiveData<String> = data.questionText
    val answerText: LiveData<String> = data.answerText
    val questionScore: LiveData<QuestionScore?> = data.questionScore
    val totalScore: LiveData<Float?> = data.totalScore
    val isWhatLinks: LiveData<Boolean> = data.isWhatLinks
    val quit: LiveData<Boolean> = data.quit

    fun start() {
        data.showLoading.value = true

        scenes.clear()
        sceneIndex = 0
        quizRepository.loadQuiz(this)
    }

    override fun onQuizLoaded(quiz: Quiz) {
        scoresRepository.initialise(quiz)
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

    fun toggleScore() {
        val scene = scenes[sceneIndex]
        if (scene !is Scene.QuestionAnswerScene) {
            return
        }

        val questionNumber = scene.question.number
        val score = when (scoresRepository.getScore(questionNumber)) {
            QuestionScore.NONE -> QuestionScore.FULL
            QuestionScore.FULL -> QuestionScore.HALF
            QuestionScore.HALF -> QuestionScore.NONE
        }
        scoresRepository.setScore(questionNumber, score)
        data.questionScore.value = score
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
                data.questionNumber.value = scene.question.number
                data.questionText.value = scene.question.question
                data.answerText.value = ""
                data.isWhatLinks.value = scene.question.isWhatLinks
                data.questionScore.value = null
                data.totalScore.value = null
            }
            is Scene.QuestionAnswerScene -> {
                data.questionNumber.value = scene.question.number
                data.questionText.value = scene.question.question
                data.answerText.value = scene.question.answer
                data.isWhatLinks.value = scene.question.isWhatLinks
                data.questionScore.value = scoresRepository.getScore(scene.question.number)
                data.totalScore.value = null
            }
            is Scene.EndTitleScene -> {
                data.titleResId.value = R.string.title_end
                data.questionScore.value = null
                data.totalScore.value = scoresRepository.totalScore
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
        val showLoading = MutableLiveData<Boolean>()
        val quizDate = MutableLiveData<Date?>()
        val titleResId = MutableLiveData<Int>()
        val questionNumber = MutableLiveData<Int>()
        val questionText = MutableLiveData<String>()
        val answerText = MutableLiveData<String>()
        val questionScore = MutableLiveData<QuestionScore?>()
        val totalScore = MutableLiveData<Float?>()
        val isWhatLinks = MutableLiveData<Boolean>()
        val quit = MutableLiveData<Boolean>()
    }
}
