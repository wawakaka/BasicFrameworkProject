package io.github.wawakaka.basicframeworkproject.presentation.content

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.BaseFragment
import io.github.wawakaka.basicframeworkproject.data.repositories.openweathermap.model.response.Weather
import kotlinx.android.synthetic.main.fragment_weather_list.*
import org.koin.androidx.scope.currentScope

class WeatherListFragment : BaseFragment(), WeatherListContract.View {

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

    private fun init() {
        initEditText()
        initGoButton()
    }

    private fun initEditText() {
        presenter.onApiTextChangesEvent(edit_text_api_key.afterTextChangeEvents())
    }

    private fun initGoButton() {
        presenter.onButtonClickedEvent(button_go.clicks())
    }

    override fun enableButton() {
        button_go.isEnabled = true
    }

    override fun disableButton() {
        button_go.isEnabled = false
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    companion object {
        private val TAG = WeatherListFragment::class.java.canonicalName
    }
}
