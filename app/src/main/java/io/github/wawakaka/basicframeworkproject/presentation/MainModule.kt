package io.github.wawakaka.basicframeworkproject.presentation

import org.koin.dsl.module

val mainModule = module {
    scope<MainActivity> {
        scoped<MainContract.Presenter> { MainPresenter() }
    }
}