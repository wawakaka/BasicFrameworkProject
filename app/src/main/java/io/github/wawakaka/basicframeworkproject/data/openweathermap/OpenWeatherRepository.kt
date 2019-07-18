package io.github.wawakaka.basicframeworkproject.data.openweathermap

import io.github.wawakaka.basicframeworkproject.data.openweathermap.model.OpenWeatherApi
import io.github.wawakaka.basicframeworkproject.data.openweathermap.model.response.Weather
import io.reactivex.Observable

/**
 * Created by wawakaka on 19/07/18.
 */
class OpenWeatherRepository(private val openWeatherApi: OpenWeatherApi) {

    fun geCurrentWeatherObservable(apiKey: String): Observable<Weather> {
        return openWeatherApi.geCurrentWeatherObservable(id = "2172797",apiKey = apiKey)
    }
}