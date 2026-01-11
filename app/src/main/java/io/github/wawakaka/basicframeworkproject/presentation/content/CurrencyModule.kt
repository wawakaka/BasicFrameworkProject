package io.github.wawakaka.basicframeworkproject.presentation.content

import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module

val currencyModule = module {
    scope<CurrencyFragment> {
        scoped { CurrencyPresenter(get()) }
    }
}