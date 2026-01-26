package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.toad.ViewEvent

sealed interface CurrencyEvent : ViewEvent {
    data class ShowToast(val message: String) : CurrencyEvent

    data class ShowError(
        val title: String,
        val message: String
    ) : CurrencyEvent

    data object NavigateBack : CurrencyEvent
}
