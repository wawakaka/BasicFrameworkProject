package io.github.wawakaka.basicframeworkproject.presentation

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    // ViewModel for Main screen with TOAD architecture (permission handling)
    viewModel { MainViewModel() }
}