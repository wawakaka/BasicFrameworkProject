package io.github.wawakaka.basicframeworkproject.presentation.content

import org.koin.core.qualifier.named
import org.koin.dsl.module

val weatherListModule = module {
    scope(named<WeatherListFragment>()) {
        scoped { WeatherListPresenter(get()) }
    }
}