package io.github.wawakaka.basicframeworkproject.presentation

import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module

val mainModule = module {
    scope<MainActivity> {
        scoped { MainPresenter() }
    }
}