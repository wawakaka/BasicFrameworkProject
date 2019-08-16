package io.github.wawakaka.repository

import io.github.wawakaka.openweathermap.OpenWeatherApi
import io.github.wawakaka.openweathermap.OpenWeatherRepository

object Repository {

    fun getOpenWeather(openWeatherApi: OpenWeatherApi): OpenWeatherRepository {
        return OpenWeatherRepository(openWeatherApi)
    }
}