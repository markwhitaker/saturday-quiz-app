package uk.co.mainwave.saturdayquizapp.presenter

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.junit.Before
import org.junit.Test
import uk.co.mainwave.saturdayquizapp.model.ColourSet
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionType
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.repository.PreferencesRepository
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import java.util.Date

class QuizPresenterTest {
    // Mocks
    private val mockView = mockk<QuizPresenter.View>(relaxUnitFun = true)
    private val mockRepository = mockk<QuizRepository>(relaxUnitFun = true)
    private val mockPreferencesRepository = mockk<PreferencesRepository>(relaxUnitFun = true)

    private val presenter = QuizPresenter(
        mockRepository,
        mockPreferencesRepository
    )

    @Before
    fun setUp() {
        presenter.onViewCreated(mockView)
    }

    @Test
    fun `GIVEN presenter initialised WHEN onViewDisplayed() THEN loading view shown and quiz loaded`() {
        // Given
        every {
            mockPreferencesRepository.colourSet
        } returns ColourSet.LIGHT
        // When
        presenter.onViewDisplayed()
        // Then
        verifyAll {
            mockPreferencesRepository.colourSet
            mockView.setColours(ColourSet.LIGHT)
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
    fun `GIVEN colour set is light WHEN down is pressed THEN colour set is set to medium`() {
        // Given
        every {
            mockPreferencesRepository.colourSet
        } returns ColourSet.LIGHT
        // When
        presenter.onDown()
        // Then
        verifyAll {
            mockPreferencesRepository.colourSet
            mockPreferencesRepository.colourSet = ColourSet.MEDIUM
            mockView.setColours(ColourSet.MEDIUM)
            mockView.showColoursTip(ColourSet.MEDIUM)
        }
        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
    }

    @Test
    fun `GIVEN colour set is medium WHEN down is pressed THEN colour set is set to dark`() {
        // Given
        every {
            mockPreferencesRepository.colourSet
        } returns ColourSet.MEDIUM
        // When
        presenter.onDown()
        // Then
        verifyAll {
            mockPreferencesRepository.colourSet
            mockPreferencesRepository.colourSet = ColourSet.DARK
            mockView.setColours(ColourSet.DARK)
            mockView.showColoursTip(ColourSet.DARK)
        }
        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
    }

    @Test
    fun `GIVEN colour set is dark WHEN down is pressed THEN nothing happens`() {
        // Given
        every {
            mockPreferencesRepository.colourSet
        } returns ColourSet.DARK
        // When
        presenter.onDown()
        // Then
        verify {
            mockPreferencesRepository.colourSet
        }
        verify(exactly = 0) {
            mockPreferencesRepository.colourSet = any() as ColourSet
            mockView.setColours(any())
        }

        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
    }

    @Test
    fun `GIVEN colour set is dark WHEN up is pressed THEN colour set is set to medium`() {
        // Given
        every {
            mockPreferencesRepository.colourSet
        } returns ColourSet.DARK
        // When
        presenter.onUp()
        // Then
        verifyAll {
            mockPreferencesRepository.colourSet
            mockPreferencesRepository.colourSet = ColourSet.MEDIUM
            mockView.setColours(ColourSet.MEDIUM)
            mockView.showColoursTip(ColourSet.MEDIUM)
        }
        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
    }

    @Test
    fun `GIVEN colour set is medium WHEN up is pressed THEN colour set is set to light`() {
        // Given
        every {
            mockPreferencesRepository.colourSet
        } returns ColourSet.MEDIUM
        // When
        presenter.onUp()
        // Then
        verifyAll {
            mockPreferencesRepository.colourSet
            mockPreferencesRepository.colourSet = ColourSet.LIGHT
            mockView.setColours(ColourSet.LIGHT)
            mockView.showColoursTip(ColourSet.LIGHT)
        }
        confirmVerified(
            mockPreferencesRepository,
            mockView
        )
    }

    @Test
    fun `GIVEN colour set is light WHEN up is pressed THEN nothing happens`() {
        // Given
        every {
            mockPreferencesRepository.colourSet
        } returns ColourSet.LIGHT
        // When
        presenter.onUp()
        // Then
        verify {
            mockPreferencesRepository.colourSet
        }
        verify(exactly = 0) {
            mockPreferencesRepository.colourSet = any() as ColourSet
            mockView.setColours(any())
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
