package io.github.wawakaka.basicframeworkproject.presentation

import io.github.wawakaka.basicframeworkproject.base.BasePresenter

/**
 * Main Presenter using MVP pattern
 * @deprecated Migrate to TOAD pattern with MainViewModel (Milestone 6).
 * This class will be removed in Milestone 7.
 */
@Deprecated(
    message = "Migrate to TOAD pattern with MainViewModel (Milestone 6)",
    replaceWith = ReplaceWith(
        expression = "MainViewModel",
        imports = ["io.github.wawakaka.basicframeworkproject.presentation.MainViewModel"]
    ),
    level = DeprecationLevel.WARNING
)
class MainPresenter : BasePresenter<MainContract.View>(), MainContract.Presenter {

    override fun checkPermission() {
        // Delegate permission request to View (Activity)
        view?.requestCameraPermission()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            view?.onPermissionGranted()
        } else {
            view?.onPermissionDenied()
        }
    }
}