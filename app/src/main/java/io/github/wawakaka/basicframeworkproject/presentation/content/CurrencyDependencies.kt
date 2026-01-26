package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.basicframeworkproject.presentation.toad.ActionDependencies
import io.github.wawakaka.basicframeworkproject.utilities.TimeProvider
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase

class CurrencyDependencies(
    val getLatestRatesUsecase: GetLatestRatesUsecase,
    val timeProvider: TimeProvider
) : ActionDependencies
