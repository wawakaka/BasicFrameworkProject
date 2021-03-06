package io.github.wawakaka.basicframeworkproject.presentation

import android.os.Bundle
import android.util.Log
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.BaseActivity
import io.github.wawakaka.basicframeworkproject.base.FragmentActivityCallbacks
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.scope.currentScope

class MainActivity : BaseActivity(), FragmentActivityCallbacks, MainContract.View {

    private val presenter: MainPresenter by currentScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.attach(this)
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
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
