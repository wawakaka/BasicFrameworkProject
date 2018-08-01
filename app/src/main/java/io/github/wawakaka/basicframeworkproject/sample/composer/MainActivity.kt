package io.github.wawakaka.basicframeworkproject.sample.composer

/**
 * Created by wawakaka on 17/07/18.
 */

import android.Manifest
import android.util.Log
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.composer.BaseActivity
import io.github.wawakaka.basicframeworkproject.sample.model.CurrentWeather
import io.github.wawakaka.basicframeworkproject.sample.presenter.MyPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

/**
 * Created by wawakaka on 17/07/18.
 */
class MainActivity : BaseActivity() {

    companion object {
        val TAG: String? = MainActivity::class.java.simpleName
    }

    private val presenter: MyPresenter by inject()

    init {
        initLayout()
        initTestPermission()
        initEditText()
        initGoButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_main)
            }
    }

    private fun initTestPermission() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                rxPermissions.setLogging(true)
                testPermission()
            }
    }

    private fun testPermission() {
        rxPermissions
            .requestEachCombined(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE)
            .subscribe(
                { permission ->
                    when {
                        permission.granted -> Log.d(TAG, "permission granted")
                        permission.shouldShowRequestPermissionRationale -> Log.d(TAG, "permission shouldShowRequestPermissionRationale")
                        else -> Log.d(TAG, "permission denied")
                    }
                },
                { Log.e(TAG, "requestInternetPermissionsObservable error", it) },
                { Log.d(TAG, "requestInternetPermissionsObservable complete") }
            )
    }

    private fun initEditText() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxTextView.afterTextChangeEvents(this.api_key) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                { onInitEditTextSucceed() },
                { Log.e(TAG, "error initEditText ", it) }
            )
    }

    private fun onInitEditTextSucceed() {
        go.isEnabled = !(getApiKey() == null || getApiKey() == "")
    }

    private fun initGoButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(go) }
            .observeOn(Schedulers.io())
            .flatMap {
                presenter
                    .geCurrentWeatherObservable(getApiKey() ?: "")
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function { onGeCurrentWeatherError(it) })
            }
            .map { it }
            .filter { it.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe(
                { onInitGoButtonSucceed(it) },
                { onInitGoButtonError(it) },
                { Log.d(TAG, "initGoButton complete") }
            )
    }

    private fun onGeCurrentWeatherError(throwable: Throwable): Observable<CurrentWeather> {
        Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()
        Log.e(TAG, "error initGoButton.geCurrentWeatherObservable ", throwable)
        return Observable.just(CurrentWeather.empty)
    }

    private fun onInitGoButtonSucceed(currentWeather: CurrentWeather) {
        result.text = currentWeather.toString()
        Log.d(TAG, "initGoButton next")
    }

    private fun onInitGoButtonError(throwable: Throwable) {
        Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()
        Log.e(TAG, "error initGoButton ", throwable)
    }

    private fun getApiKey(): String? = api_key.text.toString()
}
