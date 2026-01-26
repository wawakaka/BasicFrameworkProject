package io.github.wawakaka.feature.currencyexchange.presentation

import io.github.wawakaka.toad.ToadViewModel

internal class CurrencyViewModel(
    dependencies: CurrencyDependencies
) : ToadViewModel<CurrencyState, CurrencyEvent, CurrencyDependencies>(
    initialState = CurrencyState(),
    dependencies = dependencies
) {

    fun runAction(action: CurrencyAction) {
        super.runAction(action)
    }
}
