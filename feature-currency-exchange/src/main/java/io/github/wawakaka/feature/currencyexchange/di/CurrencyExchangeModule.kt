package io.github.wawakaka.feature.currencyexchange.di

import io.github.wawakaka.feature.currencyexchange.presentation.CurrencyDependencies
import io.github.wawakaka.feature.currencyexchange.presentation.CurrencyViewModel
import io.github.wawakaka.feature.currencyexchange.util.SystemTimeProvider
import io.github.wawakaka.feature.currencyexchange.util.TimeProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureCurrencyExchangeModule = module {
    single<TimeProvider> { SystemTimeProvider() }

    factory {
        CurrencyDependencies(
            getLatestRatesUsecase = get(),
            timeProvider = get()
        )
    }

    viewModel { CurrencyViewModel(dependencies = get()) }
}
