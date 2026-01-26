package io.github.wawakaka.basicframeworkproject.presentation.content

import java.math.BigDecimal

/**
 * TOAD Pattern - State/Event/Effect Models for Currency Feature
 * Created for Milestone 6: MVP → TOAD Migration
 */

// ============ UI STATE ============

/**
 * Represents all possible UI states for the Currency screen.
 * This sealed class ensures type-safe state management with immutability.
 */
sealed class CurrencyUiState {
    /**
     * Initial state before any action is taken
     */
    object Idle : CurrencyUiState()

    /**
     * Loading state while fetching currency rates
     */
    object Loading : CurrencyUiState()

    /**
     * Success state with loaded currency rates
     * @param rates List of currency code and rate pairs
     * @param timestamp Optional timestamp of when data was loaded
     */
    data class Success(
        val rates: List<Pair<String, BigDecimal>>,
        val timestamp: String = ""
    ) : CurrencyUiState()

    /**
     * Error state when loading fails
     * @param message Error message to display
     * @param canRetry Whether retry is available
     */
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : CurrencyUiState()
}

// ============ UI EVENTS ============

/**
 * Represents all user interactions/events for the Currency screen.
 * Events are sent from the UI to the ViewModel.
 */
sealed class CurrencyUiEvent {
    /**
     * Event when user wants to load currency rates
     */
    object OnLoadRates : CurrencyUiEvent()

    /**
     * Event when user clicks retry after an error
     */
    object OnRetry : CurrencyUiEvent()

    /**
     * Event when user pulls to refresh or clicks refresh button
     */
    object OnRefresh : CurrencyUiEvent()
}

// ============ UI EFFECTS ============

/**
 * Represents one-time side effects that the UI should handle.
 * Effects are consumed once and don't persist in the state.
 */
sealed class CurrencyUiEffect {
    /**
     * Show a toast message
     * @param message The message to display
     */
    data class ShowToast(val message: String) : CurrencyUiEffect()

    /**
     * Show an error dialog
     * @param title Dialog title
     * @param message Error message
     */
    data class ShowError(
        val title: String,
        val message: String
    ) : CurrencyUiEffect()

    /**
     * Navigate back to previous screen
     */
    object NavigateBack : CurrencyUiEffect()
}
