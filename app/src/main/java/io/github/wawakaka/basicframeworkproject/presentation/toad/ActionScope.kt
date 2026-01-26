package io.github.wawakaka.basicframeworkproject.presentation.toad

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Provides controlled access to state mutations and event emissions.
 */
class ActionScope<S : ViewState, E : ViewEvent>(
    private val stateFlow: MutableStateFlow<S>,
    private val eventChannel: Channel<E>
) {

    val currentState: S
        get() = stateFlow.value

    fun setState(reducer: S.() -> S) {
        stateFlow.update(reducer)
    }

    fun sendEvent(event: E) {
        eventChannel.trySend(event)
    }
}
