package io.github.wawakaka.basicframeworkproject.presentation

import io.github.wawakaka.basicframeworkproject.base.BasePresenter

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