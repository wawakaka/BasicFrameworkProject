package io.github.wawakaka.feature.currencyexchange.presentation

import io.github.wawakaka.toad.ViewState
import java.math.BigDecimal

internal data class CurrencyState(
    val isLoading: Boolean = false,
    val rates: List<Pair<String, BigDecimal>> = emptyList(),
    val timestamp: String = "",
    val errorMessage: String? = null
) : ViewState
