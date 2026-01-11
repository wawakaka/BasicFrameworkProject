package io.github.wawakaka.basicframeworkproject.presentation

import android.os.Bundle
import android.util.Log
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.BaseActivity
import io.github.wawakaka.basicframeworkproject.base.FragmentActivityCallbacks
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.scope.createScope
import org.koin.core.scope.Scope

class MainActivity : BaseActivity(), FragmentActivityCallbacks, MainContract.View {

    private val scope: Scope by lazy { createScope(this) }
    private val presenter: MainPresenter by scope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.attach(this)
        init()
    }

    override fun onDestroy() {
        presenter.detach()
        scope.close()
        super.onDestroy()
    }

    override fun setToolbar(title: String, showUpButton: Boolean) {
        supportActionBar?.apply {
            this.title = title
            this.setDisplayShowHomeEnabled(true)
            this.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onPermissionGranted() {
        Log.e(TAG, "permission granted")
    }

    override fun onShouldShowPermissionRationale() {
        Log.e(TAG, "permission shouldShowRequestPermissionRationale")
    }

    override fun onPermissionDenied() {
        Log.e(TAG, "permission denied")
    }

    override fun onPermissionError(throwable: Throwable) {
        Log.e(TAG, "requestInternetPermissionsObservable error", throwable)
    }

    private fun init() {
        setSupportActionBar(toolbar)
        rxPermissions.setLogging(true)
        presenter.checkPermission(rxPermissions)
    }

    companion object {
        val TAG: String? = MainActivity::class.java.canonicalName
    }
}
