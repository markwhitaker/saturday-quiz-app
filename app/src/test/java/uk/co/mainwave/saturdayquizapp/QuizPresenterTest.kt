package uk.co.mainwave.saturdayquizapp

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
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
    fun `GIVEN view created WHEN onQuizLoaded() THEN`() {
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
}
