package io.github.wawakaka.basicframeworkproject.presentation

import org.koin.core.qualifier.named
import org.koin.dsl.module

val mainModule = module {
    scope(named<MainActivity>()) {
        scoped { MainPresenter() }
    }
}