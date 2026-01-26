package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.basicframeworkproject.utilities.TimeProvider
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import io.github.wawakaka.toad.ActionDependencies

class CurrencyDependencies(
    val getLatestRatesUsecase: GetLatestRatesUsecase,
    val timeProvider: TimeProvider
) : ActionDependencies
