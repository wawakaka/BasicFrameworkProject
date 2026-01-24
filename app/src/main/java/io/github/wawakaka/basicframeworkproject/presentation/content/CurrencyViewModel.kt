package io.github.wawakaka.basicframeworkproject.presentation.content

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for Currency screen using TOAD pattern.
 * Manages currency rates state and handles user events.
 *
 * TOAD Components:
 * - State: Immutable UI state via StateFlow
 * - Events: User interactions handled via handleEvent()
 * - Effects: One-time side effects via Channel
 *
 * @param getRatesUseCase Use case for fetching currency rates
 */
class CurrencyViewModel(
    private val getRatesUseCase: GetLatestRatesUsecase
) : ViewModel() {

    // ========== STATE ==========

    /**
     * Private mutable state for internal updates
     */
    private val _state = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Idle)

    /**
     * Public immutable state exposed to UI
     * UI observes this with collectAsStateWithLifecycle()
     */
    val state: StateFlow<CurrencyUiState> = _state.asStateFlow()

    // ========== EFFECTS ==========

    /**
     * Private channel for sending one-time effects
     */
    private val _effect = Channel<CurrencyUiEffect>()

    /**
     * Public flow of effects exposed to UI
     * UI collects this in LaunchedEffect
     */
    val effect: Flow<CurrencyUiEffect> = _effect.receiveAsFlow()

    // ========== EVENT HANDLER ==========

    /**
     * Handles all user events from the UI
     * @param event The user event to handle
     */
    fun handleEvent(event: CurrencyUiEvent) {
        when (event) {
            is CurrencyUiEvent.OnLoadRates -> loadRates()
            is CurrencyUiEvent.OnRetry -> loadRates()
            is CurrencyUiEvent.OnRefresh -> loadRates()
        }
    }

    // ========== PRIVATE HELPERS ==========

    /**
     * Loads currency rates from the use case
     * Updates state and sends effects based on result
     */
    private fun loadRates() {
        viewModelScope.launch {
            Log.d(TAG, "Loading currency rates...")

            // Emit loading state
            _state.value = CurrencyUiState.Loading

            try {
                // Call use case to fetch data
                val rates = getRatesUseCase.getLatestCurrencyRates()

                // Generate timestamp
                val timestamp = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

                // Emit success state
                _state.value = CurrencyUiState.Success(
                    rates = rates,
                    timestamp = timestamp
                )

                Log.d(TAG, "Rates loaded successfully: ${rates.size} entries at $timestamp")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to load rates: ${e.message}", e)

                // Emit error state
                _state.value = CurrencyUiState.Error(
                    message = e.message ?: "Unknown error occurred",
                    canRetry = true
                )

                // Send effect for error notification
                _effect.send(
                    CurrencyUiEffect.ShowToast("Failed to load rates: ${e.message}")
                )
            }
        }
    }

    companion object {
        private const val TAG = "CurrencyViewModel"
    }
}
