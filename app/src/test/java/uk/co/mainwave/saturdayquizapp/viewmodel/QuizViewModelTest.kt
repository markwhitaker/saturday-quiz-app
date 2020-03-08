package uk.co.mainwave.saturdayquizapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verifyAll
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import uk.co.mainwave.saturdayquizapp.R
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionType
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import uk.co.mainwave.saturdayquizapp.repository.ScoresRepository
import java.util.Date

@ExperimentalCoroutinesApi
class QuizViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mockQuizRepository = mockk<QuizRepository>(relaxUnitFun = true)
    private val mockScoresRepository = mockk<ScoresRepository>(relaxUnitFun = true)

    private val viewModel = QuizViewModel(
        mockQuizRepository,
        mockScoresRepository
    )

    @Test
    fun `GIVEN viewModel initialised WHEN onQuizLoaded() THEN loading UI is hidden, first scene is shown`() {
        // Given
        val quiz = Quiz(
            "id",
            TEST_DATE,
            "title",
            emptyList()
        )
        val mockShowLoadingObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        val mockTitleResIdObserver = mockk<Observer<Int>>(relaxUnitFun = true)
        val mockDateObserver = mockk<Observer<Date?>>(relaxUnitFun = true)
        viewModel.apply {
            showLoading.observeForever(mockShowLoadingObserver)
            titleResId.observeForever(mockTitleResIdObserver)
            quizDate.observeForever(mockDateObserver)
        }
        // When
        viewModel.onQuizLoaded(quiz)
        // Then
        verifyAll {
            mockShowLoadingObserver.onChanged(false)
            mockTitleResIdObserver.onChanged(R.string.title_questions)
            mockDateObserver.onChanged(TEST_DATE)
        }
    }

    @Test
    fun `GIVEN viewModel initialised WHEN onQuizLoadFailed() THEN quit`() {
        // Given
        val mockQuitObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        viewModel.apply {
            quit.observeForever(mockQuitObserver)
        }
        // When
        viewModel.onQuizLoadFailed()
        // Then
        verifyAll {
            mockQuitObserver.onChanged(true)
        }
    }

    @Test
    fun `GIVEN view created and quiz loaded WHEN onNext() THEN next scene is shown`() {
        // Given
        val mockShowLoadingObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        val mockTitleResIdObserver = mockk<Observer<Int>>(relaxUnitFun = true)
        val mockQuizDateObserver = mockk<Observer<Date?>>(relaxUnitFun = true)
        val mockQuestionNumberObserver = mockk<Observer<Int>>(relaxUnitFun = true)
        val mockQuestionTextObserver = mockk<Observer<String>>(relaxUnitFun = true)
        val mockAnswerTextObserver = mockk<Observer<String>>(relaxUnitFun = true)
        val mockIsWhatLinksObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        val mockQuitObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        viewModel.apply {
            showLoading.observeForever(mockShowLoadingObserver)
            titleResId.observeForever(mockTitleResIdObserver)
            quizDate.observeForever(mockQuizDateObserver)
            questionNumber.observeForever(mockQuestionNumberObserver)
            questionText.observeForever(mockQuestionTextObserver)
            answerText.observeForever(mockAnswerTextObserver)
            isWhatLinks.observeForever(mockIsWhatLinksObserver)
            quit.observeForever(mockQuitObserver)
        }
        // When
        viewModel.onQuizLoaded(TEST_QUIZ)
        repeat(9) {
            viewModel.onNext()
        }
        // Then
        verifyOrder {
            // onQuizLoaded()
            mockShowLoadingObserver.onChanged(false)
            mockTitleResIdObserver.onChanged(R.string.title_questions)
            mockQuizDateObserver.onChanged(TEST_DATE)
            // onNext() 1
            mockQuestionNumberObserver.onChanged(1)
            mockQuestionTextObserver.onChanged("question1")
            mockAnswerTextObserver.onChanged("")
            mockIsWhatLinksObserver.onChanged(false)
            // onNext() 2
            mockQuestionNumberObserver.onChanged(2)
            mockQuestionTextObserver.onChanged("question2")
            mockAnswerTextObserver.onChanged("")
            mockIsWhatLinksObserver.onChanged(true)
            // onNext() 3
            mockTitleResIdObserver.onChanged(R.string.title_answers)
            mockQuizDateObserver.onChanged(null)
            // onNext() 4
            mockQuestionNumberObserver.onChanged(1)
            mockQuestionTextObserver.onChanged("question1")
            mockAnswerTextObserver.onChanged("")
            mockIsWhatLinksObserver.onChanged(false)
            // onNext() 5
            mockQuestionNumberObserver.onChanged(1)
            mockQuestionTextObserver.onChanged("question1")
            mockAnswerTextObserver.onChanged("answer1")
            mockIsWhatLinksObserver.onChanged(false)
            // onNext() 6
            mockQuestionNumberObserver.onChanged(2)
            mockQuestionTextObserver.onChanged("question2")
            mockAnswerTextObserver.onChanged("")
            mockIsWhatLinksObserver.onChanged(true)
            // onNext() 7
            mockQuestionNumberObserver.onChanged(2)
            mockQuestionTextObserver.onChanged("question2")
            mockAnswerTextObserver.onChanged("answer2")
            mockIsWhatLinksObserver.onChanged(true)
            // onNext() 8
            mockTitleResIdObserver.onChanged(R.string.title_end)
            // onNext() 9
            mockQuitObserver.onChanged(true)
        }
        confirmVerified(
            mockShowLoadingObserver,
            mockTitleResIdObserver,
            mockQuizDateObserver,
            mockQuestionNumberObserver,
            mockQuestionTextObserver,
            mockAnswerTextObserver,
            mockIsWhatLinksObserver,
            mockQuitObserver
        )
    }

    @Test
    fun `GIVEN quiz loaded and last question and answer shown WHEN onPrevious() THEN previous scene is shown`() {
        // Given
        viewModel.onQuizLoaded(TEST_QUIZ)
        repeat(8) {
            viewModel.onNext()
        }
        val mockShowLoadingObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        val mockTitleResIdObserver = mockk<Observer<Int>>(relaxUnitFun = true)
        val mockQuizDateObserver = mockk<Observer<Date?>>(relaxUnitFun = true)
        val mockQuestionNumberObserver = mockk<Observer<Int>>(relaxUnitFun = true)
        val mockQuestionTextObserver = mockk<Observer<String>>(relaxUnitFun = true)
        val mockAnswerTextObserver = mockk<Observer<String>>(relaxUnitFun = true)
        val mockIsWhatLinksObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        val mockQuitObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        viewModel.apply {
            showLoading.observeForever(mockShowLoadingObserver)
            titleResId.observeForever(mockTitleResIdObserver)
            quizDate.observeForever(mockQuizDateObserver)
            questionNumber.observeForever(mockQuestionNumberObserver)
            questionText.observeForever(mockQuestionTextObserver)
            answerText.observeForever(mockAnswerTextObserver)
            isWhatLinks.observeForever(mockIsWhatLinksObserver)
            quit.observeForever(mockQuitObserver)
        }
        // When
        repeat(9) {
            viewModel.onPrevious()
        }
        // Then
        verifyOrder {
            // onPrevious() 1
            mockQuestionNumberObserver.onChanged(2)
            mockQuestionTextObserver.onChanged("question2")
            mockAnswerTextObserver.onChanged("answer2")
            mockIsWhatLinksObserver.onChanged(true)
            // onPrevious() 2
            mockQuestionNumberObserver.onChanged(2)
            mockQuestionTextObserver.onChanged("question2")
            mockAnswerTextObserver.onChanged("")
            mockIsWhatLinksObserver.onChanged(true)
            // onPrevious() 3
            mockQuestionNumberObserver.onChanged(1)
            mockQuestionTextObserver.onChanged("question1")
            mockAnswerTextObserver.onChanged("answer1")
            mockIsWhatLinksObserver.onChanged(false)
            // onPrevious() 4
            mockQuestionNumberObserver.onChanged(1)
            mockQuestionTextObserver.onChanged("question1")
            mockAnswerTextObserver.onChanged("")
            mockIsWhatLinksObserver.onChanged(false)
            // onPrevious() 5
            mockTitleResIdObserver.onChanged(R.string.title_answers)
            mockQuizDateObserver.onChanged(null)
            // onPrevious() 6
            mockQuestionNumberObserver.onChanged(2)
            mockQuestionTextObserver.onChanged("question2")
            mockAnswerTextObserver.onChanged("")
            mockIsWhatLinksObserver.onChanged(true)
            // onPrevious() 7
            mockQuestionNumberObserver.onChanged(1)
            mockQuestionTextObserver.onChanged("question1")
            mockAnswerTextObserver.onChanged("")
            mockIsWhatLinksObserver.onChanged(false)
            // onPrevious() 8
            mockTitleResIdObserver.onChanged(R.string.title_questions)
            mockQuizDateObserver.onChanged(TEST_DATE)
            // onPrevious() 9 is ignored
        }
    }

    companion object {
        private val TEST_DATE = Date()
        private val TEST_QUIZ = Quiz(
            "id",
            TEST_DATE,
            "title",
            listOf(
                Question(
                    1,
                    QuestionType.NORMAL,
                    "question1",
                    "answer1"
                ),
                Question(
                    2,
                    QuestionType.WHAT_LINKS,
                    "question2",
                    "answer2"
                )
            )
        )
    }
}
