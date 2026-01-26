package io.github.wawakaka.toad

/**
 * A typed command that can mutate state and emit events.
 */
interface ViewAction<D : ActionDependencies, S : ViewState, E : ViewEvent> {
    suspend fun execute(dependencies: D, scope: ActionScope<S, E>)
}
