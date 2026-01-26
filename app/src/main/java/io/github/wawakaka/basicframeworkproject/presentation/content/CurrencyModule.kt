package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.basicframeworkproject.utilities.SystemTimeProvider
import io.github.wawakaka.basicframeworkproject.utilities.TimeProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val currencyModule = module {
    single<TimeProvider> { SystemTimeProvider() }

    factory {
        CurrencyDependencies(
            getLatestRatesUsecase = get(),
            timeProvider = get()
        )
    }

    viewModel { CurrencyViewModel(dependencies = get()) }
}
