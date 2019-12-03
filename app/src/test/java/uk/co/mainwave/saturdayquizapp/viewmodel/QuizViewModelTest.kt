package uk.co.mainwave.saturdayquizapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import io.mockk.verifyOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.mainwave.saturdayquizapp.R
import uk.co.mainwave.saturdayquizapp.model.Question
import uk.co.mainwave.saturdayquizapp.model.QuestionType
import uk.co.mainwave.saturdayquizapp.model.Quiz
import uk.co.mainwave.saturdayquizapp.model.Theme
import uk.co.mainwave.saturdayquizapp.repository.PreferencesRepository
import uk.co.mainwave.saturdayquizapp.repository.QuizRepository
import java.util.Date

@ExperimentalCoroutinesApi
class QuizViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mockQuizRepository = mockk<QuizRepository>(relaxUnitFun = true)
    private val mockPrefsRepository = mockk<PreferencesRepository>(relaxUnitFun = true)

    private val viewModel = QuizViewModel(
        mockQuizRepository,
        mockPrefsRepository
    )

    @Before
    fun setUp() {
        // Allow viewModelScope to be tested
        Dispatchers.setMain(TestCoroutineDispatcher())

        every {
            mockPrefsRepository.themeTipTimeoutMs
        } returns 0
    }

    @Test
    fun `GIVEN viewModel initialised WHEN start() THEN theme is set, loading UI is shown, quiz is loaded`() {
        // Given
        every {
            mockPrefsRepository.theme
        } returns Theme.LIGHT
        val mockThemeObserver = mockk<Observer<Theme>>(relaxUnitFun = true)
        val mockShowLoadingObserver = mockk<Observer<Boolean>>(relaxUnitFun = true)
        viewModel.apply {
            theme.observeForever(mockThemeObserver)
            showLoading.observeForever(mockShowLoadingObserver)
        }
        // When
        viewModel.start()
        // Then
        verifyAll {
            mockThemeObserver.onChanged(Theme.LIGHT)
            mockShowLoadingObserver.onChanged(true)
            mockQuizRepository.loadQuiz(viewModel)
        }
    }

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

    @Test
    fun `GIVEN theme is light WHEN down is pressed THEN theme is set to medium`() {
        // Given
        every {
            mockPrefsRepository.theme
        } returns Theme.LIGHT
        val mockThemeObserver = mockk<Observer<Theme>>(relaxUnitFun = true)
        val mockThemeTipObserver = mockk<Observer<Theme?>>(relaxUnitFun = true)
        viewModel.apply {
            theme.observeForever(mockThemeObserver)
            themeTip.observeForever(mockThemeTipObserver)
        }
        // When
        viewModel.onDown()
        // Then
        verifyOrder {
            mockPrefsRepository.theme
            mockPrefsRepository.theme = Theme.MEDIUM
            mockThemeObserver.onChanged(Theme.MEDIUM)
            mockThemeTipObserver.onChanged(Theme.MEDIUM)
            mockPrefsRepository.themeTipTimeoutMs
            mockThemeTipObserver.onChanged(null)
        }
        confirmVerified(
            mockPrefsRepository,
            mockThemeObserver,
            mockThemeTipObserver
        )
    }

    @Test
    fun `GIVEN theme is medium WHEN down is pressed THEN theme is set to dark`() {
        // Given
        every {
            mockPrefsRepository.theme
        } returns Theme.MEDIUM
        val mockThemeObserver = mockk<Observer<Theme>>(relaxUnitFun = true)
        val mockThemeTipObserver = mockk<Observer<Theme?>>(relaxUnitFun = true)
        viewModel.apply {
            theme.observeForever(mockThemeObserver)
            themeTip.observeForever(mockThemeTipObserver)
        }
        // When
        viewModel.onDown()
        // Then
        verifyOrder {
            mockPrefsRepository.theme
            mockPrefsRepository.theme = Theme.DARK
            mockThemeObserver.onChanged(Theme.DARK)
            mockThemeTipObserver.onChanged(Theme.DARK)
            mockPrefsRepository.themeTipTimeoutMs
            mockThemeTipObserver.onChanged(null)
        }
        confirmVerified(
            mockPrefsRepository,
            mockThemeObserver,
            mockThemeTipObserver
        )
    }

    @Test
    fun `GIVEN theme is dark WHEN down is pressed THEN nothing happens`() {
        // Given
        every {
            mockPrefsRepository.theme
        } returns Theme.DARK
        val mockThemeObserver = mockk<Observer<Theme>>(relaxUnitFun = true)
        val mockThemeTipObserver = mockk<Observer<Theme?>>(relaxUnitFun = true)
        viewModel.apply {
            theme.observeForever(mockThemeObserver)
            themeTip.observeForever(mockThemeTipObserver)
        }
        // When
        viewModel.onDown()
        // Then
        verifyOrder {
            mockPrefsRepository.theme
        }
        confirmVerified(
            mockPrefsRepository,
            mockThemeObserver,
            mockThemeTipObserver
        )
    }

    @Test
    fun `GIVEN theme is dark WHEN up is pressed THEN theme is set to medium`() {
        // Given
        every {
            mockPrefsRepository.theme
        } returns Theme.DARK
        val mockThemeObserver = mockk<Observer<Theme>>(relaxUnitFun = true)
        val mockThemeTipObserver = mockk<Observer<Theme?>>(relaxUnitFun = true)
        viewModel.apply {
            theme.observeForever(mockThemeObserver)
            themeTip.observeForever(mockThemeTipObserver)
        }
        // When
        viewModel.onUp()
        // Then
        verifyOrder {
            mockPrefsRepository.theme
            mockPrefsRepository.theme = Theme.MEDIUM
            mockThemeObserver.onChanged(Theme.MEDIUM)
            mockThemeTipObserver.onChanged(Theme.MEDIUM)
            mockPrefsRepository.themeTipTimeoutMs
            mockThemeTipObserver.onChanged(null)
        }
        confirmVerified(
            mockPrefsRepository,
            mockThemeObserver,
            mockThemeTipObserver
        )
    }

    @Test
    fun `GIVEN theme is medium WHEN up is pressed THEN theme is set to light`() {
        // Given
        every {
            mockPrefsRepository.theme
        } returns Theme.MEDIUM
        val mockThemeObserver = mockk<Observer<Theme>>(relaxUnitFun = true)
        val mockThemeTipObserver = mockk<Observer<Theme?>>(relaxUnitFun = true)
        viewModel.apply {
            theme.observeForever(mockThemeObserver)
            themeTip.observeForever(mockThemeTipObserver)
        }
        // When
        viewModel.onUp()
        // Then
        verifyOrder {
            mockPrefsRepository.theme
            mockPrefsRepository.theme = Theme.LIGHT
            mockThemeObserver.onChanged(Theme.LIGHT)
            mockThemeTipObserver.onChanged(Theme.LIGHT)
            mockPrefsRepository.themeTipTimeoutMs
            mockThemeTipObserver.onChanged(null)
        }
        confirmVerified(
            mockPrefsRepository,
            mockThemeObserver,
            mockThemeTipObserver
        )
    }

    @Test
    fun `GIVEN theme is light WHEN up is pressed THEN nothing happens`() {
        // Given
        every {
            mockPrefsRepository.theme
        } returns Theme.LIGHT
        val mockThemeObserver = mockk<Observer<Theme>>(relaxUnitFun = true)
        val mockThemeTipObserver = mockk<Observer<Theme?>>(relaxUnitFun = true)
        viewModel.apply {
            theme.observeForever(mockThemeObserver)
            themeTip.observeForever(mockThemeTipObserver)
        }
        // When
        viewModel.onUp()
        // Then
        verifyOrder {
            mockPrefsRepository.theme
        }
        confirmVerified(
            mockPrefsRepository,
            mockThemeObserver,
            mockThemeTipObserver
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
