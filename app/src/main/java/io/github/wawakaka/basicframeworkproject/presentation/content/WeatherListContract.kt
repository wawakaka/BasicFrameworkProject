package io.github.wawakaka.basicframeworkproject.presentation.content

import com.jakewharton.rxbinding3.InitialValueObservable
import com.jakewharton.rxbinding3.widget.TextViewAfterTextChangeEvent
import io.github.wawakaka.basicframeworkproject.base.BaseContract
import io.github.wawakaka.repository.openweathermap.model.response.Weather
import io.reactivex.Observable

class WeatherListContract {

    interface View : BaseContract.View {
        fun enableButton()
        fun disableButton()
        fun onGetCurrentWeatherSuccess(weather: Weather)
        fun onGetCurrentWeatherError(throwable: Throwable)
        fun getApiKey(): String
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun onApiTextChangesEvent(event: InitialValueObservable<TextViewAfterTextChangeEvent>)
        fun onButtonClickedEvent(event: Observable<Unit>)
    }
}