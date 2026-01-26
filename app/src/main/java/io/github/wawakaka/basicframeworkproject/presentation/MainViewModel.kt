package io.github.wawakaka.basicframeworkproject.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * TOAD Pattern - State/Event/Effect Models for Main (Permission) Screen
 * Created for Milestone 6: MVP → TOAD Migration
 */

// ========== UI STATE ==========

/**
 * Represents all possible permission states for the Main screen
 */
sealed class MainUiState {
    /**
     * Initial state - checking if permission is needed
     */
    object CheckingPermission : MainUiState()

    /**
     * Permission has been granted
     */
    object PermissionGranted : MainUiState()

    /**
     * Permission was denied
     */
    object PermissionDenied : MainUiState()
}

// ========== UI EVENTS ==========

/**
 * Represents all user interactions for permission handling
 */
sealed class MainUiEvent {
    /**
     * Event to initiate permission check
     */
    object OnCheckPermission : MainUiEvent()

    /**
     * Event when user manually requests permission again
     */
    object OnRequestPermission : MainUiEvent()
}

// ========== UI EFFECTS ==========

/**
 * Represents one-time side effects for permission flow
 */
sealed class MainUiEffect {
    /**
     * Effect to request permission from the system
     */
    object RequestPermission : MainUiEffect()

    /**
     * Effect to navigate to currency screen after permission granted
     */
    object NavigateToCurrency : MainUiEffect()
}

// ========== VIEW MODEL ==========

/**
 * ViewModel for Main screen using TOAD pattern.
 * Manages camera permission state and flow.
 *
 * TOAD Components:
 * - State: Permission state via StateFlow
 * - Events: User permission interactions via handleEvent()
 * - Effects: System permission requests and navigation
 */
class MainViewModel : ViewModel() {

    // ========== STATE ==========

    /**
     * Private mutable state for internal updates
     */
    private val _state = MutableStateFlow<MainUiState>(MainUiState.CheckingPermission)

    /**
     * Public immutable state exposed to UI
     */
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    // ========== EFFECTS ==========

    /**
     * Private channel for sending one-time effects
     */
    private val _effect = Channel<MainUiEffect>(Channel.BUFFERED)

    /**
     * Public flow of effects exposed to UI
     */
    val effect: Flow<MainUiEffect> = _effect.receiveAsFlow()

    // ========== EVENT HANDLER ==========

    /**
     * Handles all user events from the UI
     * @param event The user event to handle
     */
    fun handleEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.OnCheckPermission -> checkPermission()
            is MainUiEvent.OnRequestPermission -> checkPermission()
        }
    }

    // ========== PUBLIC METHODS ==========

    /**
     * Called by the Activity when permission result is received
     * @param granted Whether the permission was granted
     */
    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            _state.value = MainUiState.PermissionGranted
            _effect.trySend(MainUiEffect.NavigateToCurrency)
        } else {
            _state.value = MainUiState.PermissionDenied
        }
    }

    // ========== PRIVATE HELPERS ==========

    /**
     * Initiates permission check flow
     * Sends effect to Activity to request permission
     */
    private fun checkPermission() {
        _state.value = MainUiState.CheckingPermission
        _effect.trySend(MainUiEffect.RequestPermission)
    }

}
