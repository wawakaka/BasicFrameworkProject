package io.github.wawakaka.basicframeworkproject.presentation.content

import org.koin.dsl.module

val currencyModule = module {
    factory { CurrencyPresenter(get()) }
}