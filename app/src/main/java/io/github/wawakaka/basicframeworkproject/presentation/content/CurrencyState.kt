package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.toad.ViewState
import java.math.BigDecimal

data class CurrencyState(
    val isLoading: Boolean = false,
    val rates: List<Pair<String, BigDecimal>> = emptyList(),
    val timestamp: String = "",
    val errorMessage: String? = null
) : ViewState
