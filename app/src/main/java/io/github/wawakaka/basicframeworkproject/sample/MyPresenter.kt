package io.github.wawakaka.basicframeworkproject.sample

import io.github.wawakaka.basicframeworkproject.data.openweathermap.OpenWeatherRepository
import io.github.wawakaka.basicframeworkproject.data.openweathermap.model.response.Weather
import io.reactivex.Observable

/**
 * Created by wawakaka on 17/07/18.
 */
class MyPresenter(private val openWeatherRepository: OpenWeatherRepository) {

    fun geCurrentWeatherObservable(apiKey: String): Observable<Weather> {
        return openWeatherRepository
            .geCurrentWeatherObservable(apiKey)
            .map { it }
    }
}