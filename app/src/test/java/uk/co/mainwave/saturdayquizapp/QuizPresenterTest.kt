package uk.co.mainwave.saturdayquizapp

import io.mockk.excludeRecords
import io.mockk.mockk
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.junit.Test
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionType
import uk.co.mainwave.saturdayquizapp.model.Quiz
import java.util.Date

class QuizPresenterTest {
    // Mocks
    private val mockRepository = mockk<QuizRepository>(relaxUnitFun = true)
    private val mockView = mockk<QuizPresenter.View>(relaxUnitFun = true)

    private val presenter = QuizPresenter(
        mockRepository
    )

    @Test
    fun `GIVEN presenter initialised WHEN onViewCreated() THEN loading view shown and quiz loaded`() {
        // Given
        // When
        presenter.onViewCreated(mockView)
        // Then
        verifyAll {
            mockView.showLoading()
            mockRepository.loadQuiz(presenter)
        }
    }

    @Test
    fun `GIVEN view created WHEN onQuizLoaded() THEN loading view is hidden and questions title is shown`() {
        // Given
        excludeRecords { mockView.showLoading() }
        val date = Date()
        val quiz = Quiz(
            "id",
            date,
            "title",
            emptyList()
        )
        presenter.onViewCreated(mockView)
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
        excludeRecords { mockView.showLoading() }
        presenter.onViewCreated(mockView)
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
            mockView.showLoading()
            mockView.hideLoading()
        }
        presenter.onViewCreated(mockView)
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
    fun `GIVEN view created and last question and answer shown WHEN onPrevious() THEN previous scene is shown`() {
        // Given
        presenter.onViewCreated(mockView)
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
