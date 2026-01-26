package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.basicframeworkproject.presentation.toad.ActionScope
import io.github.wawakaka.basicframeworkproject.presentation.toad.ViewAction

sealed interface CurrencyAction : ViewAction<CurrencyDependencies, CurrencyState, CurrencyEvent> {

    data object LoadRates : CurrencyAction {
        override suspend fun execute(
            dependencies: CurrencyDependencies,
            scope: ActionScope<CurrencyState, CurrencyEvent>
        ) {
            loadRates(dependencies, scope)
        }
    }

    data object RefreshRates : CurrencyAction {
        override suspend fun execute(
            dependencies: CurrencyDependencies,
            scope: ActionScope<CurrencyState, CurrencyEvent>
        ) {
            loadRates(dependencies, scope)
        }
    }

    data object RetryLoad : CurrencyAction {
        override suspend fun execute(
            dependencies: CurrencyDependencies,
            scope: ActionScope<CurrencyState, CurrencyEvent>
        ) {
            loadRates(dependencies, scope)
        }
    }

    companion object {
        private suspend fun loadRates(
            dependencies: CurrencyDependencies,
            scope: ActionScope<CurrencyState, CurrencyEvent>
        ) {
            if (scope.currentState.isLoading) return

            scope.setState { copy(isLoading = true, errorMessage = null) }

            try {
                val rates = dependencies.getLatestRatesUsecase.getLatestCurrencyRates()
                scope.setState {
                    copy(
                        isLoading = false,
                        rates = rates,
                        timestamp = dependencies.timeProvider.nowTimestamp(),
                        errorMessage = null
                    )
                }
            } catch (t: Throwable) {
                val message = t.message ?: "Unknown error occurred"
                scope.setState { copy(isLoading = false, errorMessage = message) }
                scope.sendEvent(CurrencyEvent.ShowToast("Failed to load rates: $message"))
            }
        }
    }
}
