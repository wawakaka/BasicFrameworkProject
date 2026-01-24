package io.github.wawakaka.basicframeworkproject.presentation.content

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module

val currencyModule = module {
    // ========== TOAD Pattern (Milestone 6) ==========
    // ViewModel for Currency screen with TOAD architecture
    viewModel { CurrencyViewModel(getRatesUseCase = get()) }

    // ========== MVP Pattern (Deprecated - will remove in M7) ==========
    // Old presenter-based architecture - kept for gradual migration
    scope<CurrencyFragment> {
        scoped { CurrencyPresenter(get()) }
    }
}