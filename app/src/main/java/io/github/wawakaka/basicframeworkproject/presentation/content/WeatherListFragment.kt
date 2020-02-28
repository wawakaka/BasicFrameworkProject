package io.github.wawakaka.basicframeworkproject.presentation.content

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.BaseFragment
import io.github.wawakaka.basicframeworkproject.utilities.makeInvisible
import io.github.wawakaka.basicframeworkproject.utilities.makeVisible
import io.github.wawakaka.repository.openweathermap.model.response.Weather
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_weather_list.*
import org.koin.androidx.scope.currentScope
import java.util.concurrent.TimeUnit

class WeatherListFragment : BaseFragment(), WeatherListContract.View {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val presenter: WeatherListPresenter by currentScope.inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach(this)
        init()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        presenter.detach()
        super.onDestroy()
    }

    override fun enableButton() {
        button_go.isEnabled = true
    }

    override fun disableButton() {
        button_go.isEnabled = false
    }

    override fun showLoading() {
        progress_loading.makeVisible()
    }

    override fun hideLoading() {
        progress_loading.makeInvisible()
    }

    override fun onGetCurrentWeatherSuccess(weather: Weather) {
        result.text = weather.toString()
        Log.d(TAG, "onGetCurrentWeather next")
    }

    override fun onGetCurrentWeatherError(throwable: Throwable) {
        Toast.makeText(activity, throwable.toString(), Toast.LENGTH_LONG).show()
        Log.e(TAG, "error onGetCurrentWeather", throwable)
    }

    override fun getApiKey(): String = edit_text_api_key.text.toString()

    private fun init() {
        initEditText()
        initGoButton()
        initProgressbar()
    }

    private fun initEditText() {
        presenter.onApiTextChangesEvent(edit_text_api_key.afterTextChangeEvents())
    }

    private fun initGoButton() {
        button_go.clicks()
            .observeOn(Schedulers.io())
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { presenter.onButtonClickedEvent() },
                { Log.e(TAG, "initGoButton", it) }
            )
            .let(compositeDisposable::add)
    }

    private fun initProgressbar() {
        context?.let {
            progress_loading.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(
                    it,
                    R.color.colorPrimary
                ), PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    companion object {
        private val TAG = WeatherListFragment::class.java.simpleName
    }
}
