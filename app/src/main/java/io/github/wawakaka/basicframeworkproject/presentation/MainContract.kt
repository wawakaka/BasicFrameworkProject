package io.github.wawakaka.basicframeworkproject.presentation

import com.tbruyelle.rxpermissions2.RxPermissions
import io.github.wawakaka.basicframeworkproject.base.BaseContract

class MainContract {

    interface View : BaseContract.View {
        fun onPermissionGranted()
        fun onShouldShowPermissionRationale()
        fun onPermissionDenied()
        fun onPermissionError(throwable: Throwable)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun checkPermission(rxPermissions: RxPermissions)
    }
}