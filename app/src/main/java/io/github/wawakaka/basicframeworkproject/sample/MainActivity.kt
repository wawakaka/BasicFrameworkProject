package io.github.wawakaka.basicframeworkproject.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.BaseActivity
import io.github.wawakaka.basicframeworkproject.data.openweathermap.model.response.Weather
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        rxPermissions.setLogging(true)
        testPermission()
        initEditText()
        initGoButton()
    }

    private fun testPermission() {
        rxPermissions
            .requestEachCombined(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE
            )
            .subscribe(
                { permission ->
                    when {
                        permission.granted -> Log.d(TAG, "permission granted")
                        permission.shouldShowRequestPermissionRationale -> Log.d(
                            TAG,
                            "permission shouldShowRequestPermissionRationale"
                        )
                        else -> Log.d(TAG, "permission denied")
                    }
                },
                { Log.e(TAG, "requestInternetPermissionsObservable error", it) },
                { Log.d(TAG, "requestInternetPermissionsObservable complete") }
            ).let(compositeDisposable::add)
    }

    private fun initEditText() {
        RxTextView
            .afterTextChangeEvents(this.api_key)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onInitEditTextSucceed() },
                { Log.e(TAG, "error initEditText ", it) }
            ).let(compositeDisposable::add)
    }

    private fun onInitEditTextSucceed() {
        go.isEnabled = !(getApiKey() == null || getApiKey() == "")
    }

    private fun initGoButton() {
        RxView
            .clicks(go)
            .observeOn(Schedulers.io())
            .flatMap {
                presenter
                    .geCurrentWeatherObservable(getApiKey() ?: "")
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Function { onGeCurrentWeatherError(it) })
            }
            .map { it }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onInitGoButtonSucceed(it) },
                { onInitGoButtonError(it) },
                { Log.d(TAG, "initGoButton complete") }
            ).let(compositeDisposable::add)
    }

    private fun onGeCurrentWeatherError(throwable: Throwable): Observable<Weather> {
        Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()
        Log.e(TAG, "error initGoButton.geCurrentWeatherObservable ", throwable)
        return Observable.just(Weather())
    }

    private fun onInitGoButtonSucceed(weather: Weather) {
        result.text = weather.toString()
        Log.d(TAG, "initGoButton next")
    }

    private fun onInitGoButtonError(throwable: Throwable) {
        Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()
        Log.e(TAG, "error initGoButton ", throwable)
    }

    private fun getApiKey(): String? = api_key.text.toString()

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
