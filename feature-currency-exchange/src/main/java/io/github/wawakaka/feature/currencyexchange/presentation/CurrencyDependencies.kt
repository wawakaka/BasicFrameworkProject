package io.github.wawakaka.feature.currencyexchange.presentation

import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import io.github.wawakaka.feature.currencyexchange.util.TimeProvider
import io.github.wawakaka.toad.ActionDependencies

internal class CurrencyDependencies(
    val getLatestRatesUsecase: GetLatestRatesUsecase,
    val timeProvider: TimeProvider
) : ActionDependencies
