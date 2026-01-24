package io.github.wawakaka.basicframeworkproject.presentation

import io.github.wawakaka.basicframeworkproject.base.BaseContract

/**
 * Main Contract using MVP pattern
 * @deprecated Migrate to TOAD pattern with MainUiState/Event/Effect (Milestone 6).
 * This class will be removed in Milestone 7.
 */
@Deprecated(
    message = "Migrate to TOAD pattern with MainUiState/Event/Effect (Milestone 6)",
    replaceWith = ReplaceWith(
        expression = "MainUiState, MainUiEvent, MainUiEffect",
        imports = [
            "io.github.wawakaka.basicframeworkproject.presentation.MainUiState",
            "io.github.wawakaka.basicframeworkproject.presentation.MainUiEvent",
            "io.github.wawakaka.basicframeworkproject.presentation.MainUiEffect"
        ]
    ),
    level = DeprecationLevel.WARNING
)
class MainContract {

    interface View : BaseContract.View {
        fun requestCameraPermission()
        fun onPermissionGranted()
        fun onPermissionDenied()
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun checkPermission()
        fun onPermissionResult(granted: Boolean)
    }
}