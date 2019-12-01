package uk.co.mainwave.saturdayquizapp.presenter

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionType
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.model.Theme
import uk.co.mainwave.saturdayquizapp.repository.PreferencesRepository
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import java.util.Date

@ExperimentalCoroutinesApi
class QuizPresenterTest {
    // Mocks
    private val mockView = mockk<QuizPresenter.View>(relaxUnitFun = true)
    private val mockRepository = mockk<QuizRepository>(relaxUnitFun = true)
    private val mockPreferencesRepository = mockk<PreferencesRepository>(relaxUnitFun = true)

    private val uiDispatcher = TestCoroutineDispatcher()

    private val presenter = QuizPresenter(
        mockRepository,
        mockPreferencesRepository,
        uiDispatcher
    )

    @Before
    fun setUp() {
        presenter.onViewCreated(mockView)
        every {
            mockPreferencesRepository.themeTipTimeoutMs
        } returns 0
    }

    @Test
    fun `GIVEN presenter initialised WHEN onViewDisplayed() THEN loading view shown and quiz loaded`() {
        // Given
        every {
            mockPreferencesRepository.theme
        } returns Theme.LIGHT
        // When
        presenter.onViewDisplayed()
        // Then
        verifyAll {
            mockPreferencesRepository.theme
            mockView.setTheme(Theme.LIGHT)
            mockView.showLoading()
            mockRepository.loadQuiz(presenter)
        }
    }

    @Test
    fun `GIVEN quiz was requested from repository WHEN onQuizLoaded() THEN loading view is hidden and questions title is shown`() {
        // Given
        val date = Date()
        val quiz = Quiz(
            "id",
            date,
            "title",
            emptyList()
        )
        // When
        presenter.onQuizLoaded(quiz)
        // Then
        verifyAll {
            mockView.hideLoading()
            mockView.showQuestionsTitle(date)
        }
    }

    @Test
    fun `GIVEN view created WHEN onQuizLoadFailed() THEN view quits`() {
        // Given
        // When
        presenter.onQuizLoadFailed()
        // Then
        verifyAll {
            mockView.quit()
        }
    }

    @Test
    fun `GIVEN view created and quiz loaded WHEN onNext() THEN next scene is shown`() {
        // Given
        excludeRecords {
            mockView.hideLoading()
        }
        presenter.onQuizLoaded(TEST_QUIZ)
        // When
        repeat(9) {
            presenter.onNext()
        }
        // Then
        verifyOrder {
            // onNext() 1
            mockView.hideTitle()
            mockView.showNumber(1)
            mockView.showQuestion("question1", false)
            mockView.showAnswer("")
            // onNext() 2
            mockView.hideTitle()
            mockView.showNumber(2)
            mockView.showQuestion("question2", true)
            mockView.showAnswer("")
            // onNext() 3
            mockView.showAnswersTitle()
            // onNext() 4
            mockView.hideTitle()
            mockView.showNumber(1)
            mockView.showQuestion("question1", false)
            mockView.showAnswer("")
            // onNext() 5
            mockView.hideTitle()
            mockView.showNumber(1)
            mockView.showQuestion("question1", false)
            mockView.showAnswer("answer1")
            // onNext() 6
            mockView.hideTitle()
            mockView.showNumber(2)
            mockView.showQuestion("question2", true)
            mockView.showAnswer("")
            // onNext() 7
            mockView.hideTitle()
            mockView.showNumber(2)
            mockView.showQuestion("question2", true)
            mockView.showAnswer("answer2")
            // onNext() 8
            mockView.showEndTitle()
            // onNext() 9
            mockView.quit()
        }
    }

    @Test
    fun `GIVEN quiz loaded and last question and answer shown WHEN onPrevious() THEN previous scene is shown`() {
        // Given
        presenter.onQuizLoaded(TEST_QUIZ)
        repeat(8) {
            presenter.onNext()
        }
        // When/Then
        // 1
        repeat(9) {
            presenter.onPrevious()
        }
        // Then
        verifyOrder {
            // onPrevious() 1
            mockView.hideTitle()
            mockView.showNumber(2)
            mockView.showQuestion("question2", true)
            mockView.showAnswer("answer2")
            // onPrevious() 2
            mockView.hideTitle()
            mockView.showNumber(2)
            mockView.showQuestion("question2", true)
            mockView.showAnswer("")
            // onPrevious() 3
            mockView.hideTitle()
            mockView.showNumber(1)
            mockView.showQuestion("question1", false)
            mockView.showAnswer("answer1")
            // onPrevious() 4
            mockView.hideTitle()
            mockView.showNumber(1)
            mockView.showQuestion("question1", false)
            mockView.showAnswer("")
            // onPrevious() 5
            mockView.showAnswersTitle()
            // onPrevious() 6
            mockView.hideTitle()
            mockView.showNumber(2)
            mockView.showQuestion("question2", true)
            mockView.showAnswer("")
            // onPrevious() 7
            mockView.hideTitle()
            mockView.showNumber(1)
            mockView.showQuestion("question1", false)
            mockView.showAnswer("")
            // onPrevious() 8 (and 9 is ignored)
            mockView.showQuestionsTitle(TEST_DATE)
        }
    }

    @Test
    fun `GIVEN theme is light WHEN down is pressed THEN theme is set to medium`() = runBlockingTest {
        // Given
        every {
            mockPreferencesRepository.theme
        } returns Theme.LIGHT
        // When
        presenter.onDown()
        // Then
        verifyAll {
            mockPreferencesRepository.theme
            mockPreferencesRepository.theme = Theme.MEDIUM
            mockView.setTheme(Theme.MEDIUM)
            mockView.showThemeTip(Theme.MEDIUM)
            mockPreferencesRepository.themeTipTimeoutMs
            mockView.hideThemeTip()
        }
        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
    }

    @Test
    fun `GIVEN theme is medium WHEN down is pressed THEN theme is set to dark`() = runBlockingTest {
        // Given
        every {
            mockPreferencesRepository.theme
        } returns Theme.MEDIUM
        // When
        presenter.onDown()
        // Then
        verifyAll {
            mockPreferencesRepository.theme
            mockPreferencesRepository.theme = Theme.DARK
            mockView.setTheme(Theme.DARK)
            mockView.showThemeTip(Theme.DARK)
            mockPreferencesRepository.themeTipTimeoutMs
            mockView.hideThemeTip()
        }
        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
    }

    @Test
    fun `GIVEN theme is dark WHEN down is pressed THEN nothing happens`() = runBlockingTest {
        // Given
        every {
            mockPreferencesRepository.theme
        } returns Theme.DARK
        // When
        presenter.onDown()
        // Then
        verify {
            mockPreferencesRepository.theme
        }
        verify(exactly = 0) {
            mockPreferencesRepository.theme = any() as Theme
            mockView.setTheme(any())
        }

        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
    }

    @Test
    fun `GIVEN theme is dark WHEN up is pressed THEN theme is set to medium`() = runBlockingTest {
        // Given
        every {
            mockPreferencesRepository.theme
        } returns Theme.DARK
        // When
        presenter.onUp()
        // Then
        verifyAll {
            mockPreferencesRepository.theme
            mockPreferencesRepository.theme = Theme.MEDIUM
            mockView.setTheme(Theme.MEDIUM)
            mockView.showThemeTip(Theme.MEDIUM)
            mockPreferencesRepository.themeTipTimeoutMs
            mockView.hideThemeTip()
        }
        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
    }

    @Test
    fun `GIVEN theme is medium WHEN up is pressed THEN theme is set to light`() = runBlockingTest {
        // Given
        every {
            mockPreferencesRepository.theme
        } returns Theme.MEDIUM
        // When
        presenter.onUp()
        // Then
        verifyAll {
            mockPreferencesRepository.theme
            mockPreferencesRepository.theme = Theme.LIGHT
            mockView.setTheme(Theme.LIGHT)
            mockView.showThemeTip(Theme.LIGHT)
            mockPreferencesRepository.themeTipTimeoutMs
            mockView.hideThemeTip()
        }
        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
    }

    @Test
    fun `GIVEN theme is light WHEN up is pressed THEN nothing happens`() = runBlockingTest {
        // Given
        every {
            mockPreferencesRepository.theme
        } returns Theme.LIGHT
        // When
        presenter.onUp()
        // Then
        verify {
            mockPreferencesRepository.theme
        }
        verify(exactly = 0) {
            mockPreferencesRepository.theme = any() as Theme
            mockView.setTheme(any())
        }

        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
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
