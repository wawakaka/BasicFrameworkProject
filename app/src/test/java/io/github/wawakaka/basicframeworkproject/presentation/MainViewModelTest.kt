package io.github.wawakaka.basicframeworkproject.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for MainViewModel (TOAD pattern)
 * Tests permission state transitions, event handling, and effect emissions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    // Rule to execute LiveData operations instantly
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== STATE TESTS ==========

    @Test
    fun `initial state should be CheckingPermission`() = runTest {
        // Assert
        val initialState = viewModel.state.value
        assertTrue(initialState is MainUiState.CheckingPermission)
    }

    @Test
    fun `handleEvent OnCheckPermission should emit RequestPermission effect`() = runTest {
        // Act
        viewModel.handleEvent(MainUiEvent.OnCheckPermission)
        advanceUntilIdle()

        // Assert - State should be CheckingPermission
        val state = viewModel.state.value
        assertTrue(state is MainUiState.CheckingPermission)

        // Assert - Effect should be RequestPermission
        val effect = viewModel.effect.first()
        assertTrue(effect is MainUiEffect.RequestPermission)
    }

    @Test
    fun `handleEvent OnRequestPermission should emit RequestPermission effect`() = runTest {
        // Act
        viewModel.handleEvent(MainUiEvent.OnRequestPermission)
        advanceUntilIdle()

        // Assert
        val effect = viewModel.effect.first()
        assertTrue(effect is MainUiEffect.RequestPermission)
    }

    // ========== PERMISSION RESULT TESTS ==========

    @Test
    fun `onPermissionResult granted should transition to PermissionGranted state`() = runTest {
        // Act
        viewModel.onPermissionResult(granted = true)
        advanceUntilIdle()

        // Assert - State should be PermissionGranted
        val state = viewModel.state.value
        assertTrue(state is MainUiState.PermissionGranted)
    }

    @Test
    fun `onPermissionResult granted should emit NavigateToCurrency effect`() = runTest {
        // Act
        viewModel.onPermissionResult(granted = true)
        advanceUntilIdle()

        // Assert - Effect should be NavigateToCurrency
        val effect = viewModel.effect.first()
        assertTrue(effect is MainUiEffect.NavigateToCurrency)
    }

    @Test
    fun `onPermissionResult denied should transition to PermissionDenied state`() = runTest {
        // Act
        viewModel.onPermissionResult(granted = false)
        advanceUntilIdle()

        // Assert - State should be PermissionDenied
        val state = viewModel.state.value
        assertTrue(state is MainUiState.PermissionDenied)
    }

    @Test
    fun `onPermissionResult denied should not emit NavigateToCurrency effect`() = runTest {
        // Arrange
        val effects = mutableListOf<MainUiEffect>()

        // Collect effects
        val job = kotlinx.coroutines.launch {
            viewModel.effect.collect { effect ->
                effects.add(effect)
            }
        }

        // Act
        viewModel.onPermissionResult(granted = false)
        advanceUntilIdle()

        job.cancel()

        // Assert - No NavigateToCurrency effect should be emitted
        assertTrue(effects.none { it is MainUiEffect.NavigateToCurrency })
    }

    // ========== STATE TRANSITION TESTS ==========

    @Test
    fun `complete permission flow - granted should transition correctly`() = runTest {
        // Arrange
        val states = mutableListOf<MainUiState>()

        // Collect states
        val job = kotlinx.coroutines.launch {
            viewModel.state.collect { state ->
                states.add(state)
            }
        }

        // Act - Full flow: CheckPermission -> Result Granted
        viewModel.handleEvent(MainUiEvent.OnCheckPermission)
        advanceUntilIdle()
        viewModel.onPermissionResult(granted = true)
        advanceUntilIdle()

        job.cancel()

        // Assert
        assertTrue(states.size >= 2)
        assertTrue(states[0] is MainUiState.CheckingPermission)
        assertTrue(states.last() is MainUiState.PermissionGranted)
    }

    @Test
    fun `complete permission flow - denied should transition correctly`() = runTest {
        // Arrange
        val states = mutableListOf<MainUiState>()

        // Collect states
        val job = kotlinx.coroutines.launch {
            viewModel.state.collect { state ->
                states.add(state)
            }
        }

        // Act - Full flow: CheckPermission -> Result Denied
        viewModel.handleEvent(MainUiEvent.OnCheckPermission)
        advanceUntilIdle()
        viewModel.onPermissionResult(granted = false)
        advanceUntilIdle()

        job.cancel()

        // Assert
        assertTrue(states.size >= 2)
        assertTrue(states[0] is MainUiState.CheckingPermission)
        assertTrue(states.last() is MainUiState.PermissionDenied)
    }

    @Test
    fun `retry after denied should request permission again`() = runTest {
        // Arrange - First deny permission
        viewModel.onPermissionResult(granted = false)
        advanceUntilIdle()

        val effects = mutableListOf<MainUiEffect>()
        val job = kotlinx.coroutines.launch {
            viewModel.effect.collect { effect ->
                effects.add(effect)
            }
        }

        // Act - Request permission again
        viewModel.handleEvent(MainUiEvent.OnRequestPermission)
        advanceUntilIdle()

        job.cancel()

        // Assert - Should emit RequestPermission effect again
        assertTrue(effects.any { it is MainUiEffect.RequestPermission })
    }

    // ========== EFFECT EMISSION TESTS ==========

    @Test
    fun `effects should be consumed only once`() = runTest {
        // Arrange
        val effects = mutableListOf<MainUiEffect>()

        // Act - Collect effects twice
        val job1 = kotlinx.coroutines.launch {
            viewModel.effect.collect { effect ->
                effects.add(effect)
            }
        }

        viewModel.handleEvent(MainUiEvent.OnCheckPermission)
        advanceUntilIdle()

        job1.cancel()

        val effectsSecondCollector = mutableListOf<MainUiEffect>()
        val job2 = kotlinx.coroutines.launch {
            viewModel.effect.collect { effect ->
                effectsSecondCollector.add(effect)
            }
        }

        advanceUntilIdle()
        job2.cancel()

        // Assert - Second collector should not receive the same effect
        assertTrue(effects.size == 1)
        assertTrue(effectsSecondCollector.isEmpty())
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    fun `multiple permission results should update state correctly`() = runTest {
        // Act - Rapid permission result changes
        viewModel.onPermissionResult(granted = false)
        advanceUntilIdle()

        viewModel.onPermissionResult(granted = true)
        advanceUntilIdle()

        // Assert - Final state should be PermissionGranted
        val finalState = viewModel.state.value
        assertTrue(finalState is MainUiState.PermissionGranted)
    }

    @Test
    fun `permission flow can be repeated multiple times`() = runTest {
        // Act - First flow
        viewModel.handleEvent(MainUiEvent.OnCheckPermission)
        advanceUntilIdle()
        viewModel.onPermissionResult(granted = false)
        advanceUntilIdle()

        // Act - Second flow
        viewModel.handleEvent(MainUiEvent.OnRequestPermission)
        advanceUntilIdle()
        viewModel.onPermissionResult(granted = true)
        advanceUntilIdle()

        // Assert - Should end in granted state
        val finalState = viewModel.state.value
        assertTrue(finalState is MainUiState.PermissionGranted)
    }

    @Test
    fun `state should remain CheckingPermission until result received`() = runTest {
        // Act
        viewModel.handleEvent(MainUiEvent.OnCheckPermission)
        advanceUntilIdle()

        // Assert - State should still be CheckingPermission
        val state = viewModel.state.value
        assertTrue(state is MainUiState.CheckingPermission)
    }
}
