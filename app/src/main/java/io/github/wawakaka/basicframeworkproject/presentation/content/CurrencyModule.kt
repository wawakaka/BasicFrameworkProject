package io.github.wawakaka.basicframeworkproject.presentation.content

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val currencyModule = module {
    // ViewModel for Currency screen with TOAD architecture
    viewModel { CurrencyViewModel(getRatesUseCase = get()) }
}