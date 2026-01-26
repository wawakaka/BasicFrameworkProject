package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.basicframeworkproject.presentation.toad.ToadViewModel

class CurrencyViewModel(
    dependencies: CurrencyDependencies
) : ToadViewModel<CurrencyState, CurrencyEvent, CurrencyDependencies>(
    initialState = CurrencyState(),
    dependencies = dependencies
) {

    fun runAction(action: CurrencyAction) {
        super.runAction(action)
    }
}
