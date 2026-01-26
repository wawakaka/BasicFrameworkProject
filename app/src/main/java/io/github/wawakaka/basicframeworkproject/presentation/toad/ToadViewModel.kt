package io.github.wawakaka.basicframeworkproject.presentation.toad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

/**
 * Base ViewModel for Medium-style TOAD.
 *
 * The ViewModel is intentionally boring: it owns state/events streams and dispatches actions.
 */
abstract class ToadViewModel<S : ViewState, E : ViewEvent, D : ActionDependencies>(
    initialState: S,
    private val dependencies: D
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _events = Channel<E>(Channel.BUFFERED)
    val events: Flow<E> = _events.receiveAsFlow()

    private val dispatchMutex = Mutex()

    fun runAction(action: ViewAction<D, S, E>) {
        dispatch(action)
    }

    protected fun dispatch(action: ViewAction<D, S, E>) {
        if (!dispatchMutex.tryLock()) {
            return
        }

        viewModelScope.launch {
            try {
                action.execute(dependencies, ActionScope(_state, _events))
            } finally {
                dispatchMutex.unlock()
            }
        }
    }
}
