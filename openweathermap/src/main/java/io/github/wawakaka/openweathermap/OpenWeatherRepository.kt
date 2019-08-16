package io.github.wawakaka.openweathermap

import io.github.wawakaka.openweathermap.model.response.Weather
import io.reactivex.Observable

class OpenWeatherRepository(private val openWeatherApi: OpenWeatherApi) {

    fun getCurrentWeatherObservable(apiKey: String): Observable<Weather> {
        return openWeatherApi.geCurrentWeatherObservable(id = "2172797", apiKey = apiKey)
    }
}