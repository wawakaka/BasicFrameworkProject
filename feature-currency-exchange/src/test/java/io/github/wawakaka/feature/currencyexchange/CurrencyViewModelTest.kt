package io.github.wawakaka.feature.currencyexchange

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import io.github.wawakaka.feature.currencyexchange.presentation.CurrencyAction
import io.github.wawakaka.feature.currencyexchange.presentation.CurrencyDependencies
import io.github.wawakaka.feature.currencyexchange.presentation.CurrencyEvent
import io.github.wawakaka.feature.currencyexchange.presentation.CurrencyState
import io.github.wawakaka.feature.currencyexchange.presentation.CurrencyViewModel
import io.github.wawakaka.feature.currencyexchange.util.TimeProvider
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var getRatesUseCase: GetLatestRatesUsecase

    private val timeProvider = object : TimeProvider {
        override fun nowTimestamp(): String = "2026-01-01 00:00:00"
    }

    private lateinit var viewModel: CurrencyViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = CurrencyViewModel(
            dependencies = CurrencyDependencies(
                getLatestRatesUsecase = getRatesUseCase,
                timeProvider = timeProvider
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be default CurrencyState`() = runTest {
        assertEquals(CurrencyState(), viewModel.state.value)
    }

    @Test
    fun `runAction LoadRates should set isLoading true immediately`() = runTest {
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(emptyList())

        viewModel.runAction(CurrencyAction.LoadRates)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `runAction LoadRates success should update rates and timestamp`() = runTest {
        val mockRates = listOf(
            "USD" to BigDecimal("1.0"),
            "EUR" to BigDecimal("0.92"),
            "GBP" to BigDecimal("0.78")
        )
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(mockRates)

        viewModel.runAction(CurrencyAction.LoadRates)
        advanceUntilIdle()

        val finalState = viewModel.state.value
        assertFalse(finalState.isLoading)
        assertEquals(mockRates, finalState.rates)
        assertEquals("2026-01-01 00:00:00", finalState.timestamp)
        assertNull(finalState.errorMessage)
    }

    @Test
    fun `runAction LoadRates failure should set errorMessage`() = runTest {
        val errorMessage = "Network error"
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenThrow(RuntimeException(errorMessage))

        viewModel.runAction(CurrencyAction.LoadRates)
        advanceUntilIdle()

        val finalState = viewModel.state.value
        assertFalse(finalState.isLoading)
        assertEquals(errorMessage, finalState.errorMessage)
    }

    @Test
    fun `failure should emit ShowToast event`() = runTest {
        val errorMessage = "API Error"
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenThrow(RuntimeException(errorMessage))

        viewModel.runAction(CurrencyAction.LoadRates)
        advanceUntilIdle()

        val event = viewModel.events.first()
        assertTrue(event is CurrencyEvent.ShowToast)
        assertTrue((event as CurrencyEvent.ShowToast).message.contains("Failed to load rates"))
    }

    @Test
    fun `empty rates list should still update timestamp and clear error`() = runTest {
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(emptyList())

        viewModel.runAction(CurrencyAction.LoadRates)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.rates.isEmpty())
        assertEquals("2026-01-01 00:00:00", state.timestamp)
        assertNull(state.errorMessage)
    }

    @Test
    fun `null error message should use default message`() = runTest {
        whenever(getRatesUseCase.getLatestCurrencyRates())
            .thenThrow(RuntimeException(null as String?))

        viewModel.runAction(CurrencyAction.LoadRates)
        advanceUntilIdle()

        assertEquals("Unknown error occurred", viewModel.state.value.errorMessage)
    }

    @Test
    fun `multiple rapid actions should ignore while loading`() = runTest {
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(listOf("USD" to BigDecimal("1.0")))

        val states = mutableListOf<CurrencyState>()
        val job = launch {
            viewModel.state.collect { state ->
                states.add(state)
            }
        }

        viewModel.runAction(CurrencyAction.LoadRates)
        viewModel.runAction(CurrencyAction.RefreshRates)
        viewModel.runAction(CurrencyAction.RetryLoad)
        advanceUntilIdle()

        job.cancel()

        verify(getRatesUseCase, times(1)).getLatestCurrencyRates()
        assertEquals(CurrencyState(), states.first())
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessage)
    }
}
