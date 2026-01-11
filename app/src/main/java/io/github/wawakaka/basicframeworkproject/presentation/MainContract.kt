package io.github.wawakaka.basicframeworkproject.presentation

import io.github.wawakaka.basicframeworkproject.base.BaseContract

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