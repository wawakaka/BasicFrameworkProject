package io.github.wawakaka.basicframeworkproject.presentation.content

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.math.BigDecimal

/**
 * Unit tests for CurrencyViewModel (TOAD pattern)
 * Tests state transitions, event handling, and effect emissions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest {

    // Rule to execute LiveData operations instantly
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var getRatesUseCase: GetLatestRatesUsecase

    private lateinit var viewModel: CurrencyViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = CurrencyViewModel(getRatesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== STATE TESTS ==========

    @Test
    fun `initial state should be Idle`() = runTest {
        // Assert
        val initialState = viewModel.state.value
        assertTrue(initialState is CurrencyUiState.Idle)
    }

    @Test
    fun `handleEvent OnLoadRates should transition to Loading state`() = runTest {
        // Arrange
        val mockRates = listOf("USD" to BigDecimal("1.0"), "EUR" to BigDecimal("0.92"))
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(mockRates)

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)

        // Assert - Check immediate Loading state
        val loadingState = viewModel.state.value
        assertTrue(loadingState is CurrencyUiState.Loading)
    }

    @Test
    fun `handleEvent OnLoadRates success should emit Success state with data`() = runTest {
        // Arrange
        val mockRates = listOf(
            "USD" to BigDecimal("1.0"),
            "EUR" to BigDecimal("0.92"),
            "GBP" to BigDecimal("0.78")
        )
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(mockRates)

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
        advanceUntilIdle() // Wait for all coroutines to complete

        // Assert
        val finalState = viewModel.state.value
        assertTrue(finalState is CurrencyUiState.Success)
        assertEquals(mockRates, (finalState as CurrencyUiState.Success).rates)
        assertTrue(finalState.timestamp.isNotEmpty())
    }

    @Test
    fun `handleEvent OnLoadRates failure should emit Error state`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        whenever(getRatesUseCase.getLatestCurrencyRates())
            .thenThrow(RuntimeException(errorMessage))

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.value
        assertTrue(finalState is CurrencyUiState.Error)
        assertEquals(errorMessage, (finalState as CurrencyUiState.Error).message)
        assertTrue(finalState.canRetry)
    }

    @Test
    fun `handleEvent OnRetry should reload data`() = runTest {
        // Arrange
        val mockRates = listOf("USD" to BigDecimal("1.0"))
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(mockRates)

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnRetry)
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.value
        assertTrue(finalState is CurrencyUiState.Success)
        assertEquals(mockRates, (finalState as CurrencyUiState.Success).rates)
    }

    @Test
    fun `handleEvent OnRefresh should reload data`() = runTest {
        // Arrange
        val mockRates = listOf("JPY" to BigDecimal("149.56"))
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(mockRates)

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnRefresh)
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.value
        assertTrue(finalState is CurrencyUiState.Success)
        assertEquals(mockRates, (finalState as CurrencyUiState.Success).rates)
    }

    // ========== EFFECT TESTS ==========

    @Test
    fun `error should emit ShowToast effect`() = runTest {
        // Arrange
        val errorMessage = "API Error"
        whenever(getRatesUseCase.getLatestCurrencyRates())
            .thenThrow(RuntimeException(errorMessage))

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
        advanceUntilIdle()

        // Assert - Collect effect
        val effect = viewModel.effect.first()
        assertTrue(effect is CurrencyUiEffect.ShowToast)
        assertTrue((effect as CurrencyUiEffect.ShowToast).message.contains("Failed to load rates"))
    }

    // ========== STATE TRANSITION TESTS ==========

    @Test
    fun `state should transition from Idle to Loading to Success`() = runTest {
        // Arrange
        val mockRates = listOf("USD" to BigDecimal("1.0"))
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(mockRates)

        val states = mutableListOf<CurrencyUiState>()

        // Collect states
        val job = launch {
            viewModel.state.collect { state ->
                states.add(state)
            }
        }

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
        advanceUntilIdle()

        job.cancel()

        // Assert - Should have Idle, Loading, Success
        assertTrue(states.size >= 3)
        assertTrue(states[0] is CurrencyUiState.Idle)
        assertTrue(states[1] is CurrencyUiState.Loading)
        assertTrue(states.last() is CurrencyUiState.Success)
    }

    @Test
    fun `state should transition from Idle to Loading to Error on failure`() = runTest {
        // Arrange
        whenever(getRatesUseCase.getLatestCurrencyRates())
            .thenThrow(RuntimeException("Error"))

        val states = mutableListOf<CurrencyUiState>()

        // Collect states
        val job = launch {
            viewModel.state.collect { state ->
                states.add(state)
            }
        }

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
        advanceUntilIdle()

        job.cancel()

        // Assert
        assertTrue(states.size >= 3)
        assertTrue(states[0] is CurrencyUiState.Idle)
        assertTrue(states[1] is CurrencyUiState.Loading)
        assertTrue(states.last() is CurrencyUiState.Error)
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    fun `empty rates list should still emit Success state`() = runTest {
        // Arrange
        val emptyRates = emptyList<Pair<String, BigDecimal>>()
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(emptyRates)

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.value
        assertTrue(finalState is CurrencyUiState.Success)
        assertTrue((finalState as CurrencyUiState.Success).rates.isEmpty())
    }

    @Test
    fun `null error message should use default message`() = runTest {
        // Arrange
        whenever(getRatesUseCase.getLatestCurrencyRates())
            .thenThrow(RuntimeException(null as String?))

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.value
        assertTrue(finalState is CurrencyUiState.Error)
        assertEquals("Unknown error occurred", (finalState as CurrencyUiState.Error).message)
    }

    @Test
    fun `multiple rapid events should handle gracefully`() = runTest {
        // Arrange
        val mockRates = listOf("USD" to BigDecimal("1.0"))
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(mockRates)

        // Act - Fire multiple events rapidly
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
        viewModel.handleEvent(CurrencyUiEvent.OnRefresh)
        viewModel.handleEvent(CurrencyUiEvent.OnRetry)
        advanceUntilIdle()

        // Assert - Should end in Success state
        val finalState = viewModel.state.value
        assertTrue(finalState is CurrencyUiState.Success)
    }
}
