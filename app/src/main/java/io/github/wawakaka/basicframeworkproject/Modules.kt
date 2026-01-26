package io.github.wawakaka.basicframeworkproject

import io.github.wawakaka.basicframeworkproject.domain.domainModules
import io.github.wawakaka.feature.currencyexchange.di.featureCurrencyExchangeModule

val applicationModules =
    listOf(
        domainModules,
        featureCurrencyExchangeModule
    )