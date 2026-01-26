package io.github.wawakaka.feature.currencyexchange.presentation

import io.github.wawakaka.toad.ViewEvent

internal sealed interface CurrencyEvent : ViewEvent {
    data class ShowToast(val message: String) : CurrencyEvent

    data class ShowError(
        val title: String,
        val message: String
    ) : CurrencyEvent

    data object NavigateBack : CurrencyEvent
}
