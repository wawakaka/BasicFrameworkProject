package io.github.wawakaka.basicframeworkproject.presentation

import android.Manifest
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class MainPresenter(
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
) : MainContract.Presenter {

    private lateinit var view: MainContract.View

    override fun checkPermission(rxPermissions: RxPermissions) {
        rxPermissions
            .requestEachCombined(
                Manifest.permission.CAMERA
            )
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { permission -> checkPermissionStatus(permission) },
                { view.onPermissionError(it) }
            ).let(compositeDisposable::add)
    }

    private fun checkPermissionStatus(permission: Permission) {
        when {
            permission.granted -> view.onPermissionGranted()
            permission.shouldShowRequestPermissionRationale -> view.onShouldShowPermissionRationale()
            else -> view.onPermissionDenied()
        }
    }

    override fun detach() {
        compositeDisposable.clear()
    }

    override fun attach(view: MainContract.View) {
        this.view = view
    }
}