package uk.co.mainwave.saturdayquizapp

import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionType
import uk.co.mainwave.saturdayquizapp.model.Quiz
import java.util.Date

class QuizPresenterTest {
    // Mocks
    private val mockRepository: QuizRepository = mock()
    private val mockView: QuizPresenter.View = mock()

    private val presenter = QuizPresenter(
        mockRepository
    )

    @Test
    fun `GIVEN presenter initialised WHEN onViewCreated() THEN loading view shown and quiz loaded`() {
        // Given
        // When
        presenter.onViewCreated(mockView)
        // Then
        verify(mockView).showLoading()
        verify(mockRepository).loadQuiz(presenter)
    }

    @Test
    fun `GIVEN view created WHEN onQuizLoaded() THEN loading view is hidden and questions title is shown`() {
        // Given
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
        verify(mockView).hideLoading()
        verify(mockView).showQuestionsTitle(date)
    }

    @Test
    fun `GIVEN view created WHEN onQuizLoadFailed() THEN view quits`() {
        // Given
        presenter.onViewCreated(mockView)
        // When
        presenter.onQuizLoadFailed()
        // Then
        verify(mockView).quit()
    }

    @Test
    fun `GIVEN view created and quiz loaded WHEN onNext() THEN next scene is shown`() {
        // Given
        val quiz = Quiz(
            "id",
            Date(),
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
        presenter.onViewCreated(mockView)
        presenter.onQuizLoaded(quiz)
        // When
        repeat(9) {
            presenter.onNext()
        }
        // Then
        inOrder(
            mockView
        ) {
            // onNext() 1
            verify(mockView).hideTitle()
            verify(mockView).showNumber(1)
            verify(mockView).showQuestion("question1", false)
            verify(mockView).showAnswer("")
            // onNext() 2
            verify(mockView).hideTitle()
            verify(mockView).showNumber(2)
            verify(mockView).showQuestion("question2", true)
            verify(mockView).showAnswer("")
            // onNext() 3
            verify(mockView).showAnswersTitle()
            // onNext() 4
            verify(mockView).hideTitle()
            verify(mockView).showNumber(1)
            verify(mockView).showQuestion("question1", false)
            verify(mockView).showAnswer("")
            // onNext() 5
            verify(mockView).hideTitle()
            verify(mockView).showNumber(1)
            verify(mockView).showQuestion("question1", false)
            verify(mockView).showAnswer("answer1")
            // onNext() 6
            verify(mockView).hideTitle()
            verify(mockView).showNumber(2)
            verify(mockView).showQuestion("question2", true)
            verify(mockView).showAnswer("")
            // onNext() 7
            verify(mockView).hideTitle()
            verify(mockView).showNumber(2)
            verify(mockView).showQuestion("question2", true)
            verify(mockView).showAnswer("answer2")
            // onNext() 8
            verify(mockView).showEndTitle()
            // onNext() 9
            verify(mockView).quit()
        }
    }

    @Test
    fun `GIVEN view created and last question and answer shown WHEN onPrevious() THEN previous scene is shown`() {
        // Given
        val date = Date()
        val quiz = Quiz(
            "id",
            date,
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
        presenter.onViewCreated(mockView)
        presenter.onQuizLoaded(quiz)
        repeat(8) {
            presenter.onNext()
        }
        // When
        repeat(9) {
            presenter.onPrevious()
        }
        // Then
        inOrder(
            mockView
        ) {
            // onPrevious() 1
            verify(mockView).hideTitle()
            verify(mockView).showNumber(2)
            verify(mockView).showQuestion("question2", true)
            verify(mockView).showAnswer("answer2")
            // onPrevious() 2
            verify(mockView).hideTitle()
            verify(mockView).showNumber(2)
            verify(mockView).showQuestion("question2", true)
            verify(mockView).showAnswer("")
            // onPrevious() 3
            verify(mockView).hideTitle()
            verify(mockView).showNumber(1)
            verify(mockView).showQuestion("question1", false)
            verify(mockView).showAnswer("answer1")
            // onPrevious() 4
            verify(mockView).hideTitle()
            verify(mockView).showNumber(1)
            verify(mockView).showQuestion("question1", false)
            verify(mockView).showAnswer("")
            // onPrevious() 5
            verify(mockView).showAnswersTitle()
            // onPrevious() 6
            verify(mockView).hideTitle()
            verify(mockView).showNumber(2)
            verify(mockView).showQuestion("question2", true)
            verify(mockView).showAnswer("")
            // onPrevious() 7
            verify(mockView).hideTitle()
            verify(mockView).showNumber(1)
            verify(mockView).showQuestion("question1", false)
            verify(mockView).showAnswer("")
            // onPrevious() 8 (and 9 is ignored)
            verify(mockView, times(1)).showQuestionsTitle(date)
        }
    }
}
