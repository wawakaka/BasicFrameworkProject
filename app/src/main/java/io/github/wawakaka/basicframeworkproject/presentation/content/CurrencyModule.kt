package io.github.wawakaka.basicframeworkproject.presentation.content

import org.koin.core.qualifier.named
import org.koin.dsl.module

val currencyModule = module {
    scope(named<CurrencyFragment>()) {
        scoped { CurrencyPresenter(get()) }
    }
}