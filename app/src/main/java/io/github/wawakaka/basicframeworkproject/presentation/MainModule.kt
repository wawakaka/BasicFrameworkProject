package io.github.wawakaka.basicframeworkproject.presentation

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    // ========== TOAD Pattern (Milestone 6) ==========
    // ViewModel for Main screen with TOAD architecture (permission handling)
    viewModel { MainViewModel() }

    // ========== MVP Pattern (Deprecated - will remove in M7) ==========
    // Old presenter-based architecture - kept for gradual migration
    scope<MainActivity> {
        scoped<MainContract.Presenter> { MainPresenter() }
    }
}