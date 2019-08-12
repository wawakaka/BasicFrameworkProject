package io.github.wawakaka.repository

import io.github.wawakaka.repository.openweathermap.OpenWeatherApi
import io.github.wawakaka.repository.openweathermap.OpenWeatherRepository

object Repository {

    fun getOpenWeather(openWeatherApi: OpenWeatherApi): OpenWeatherRepository {
        return OpenWeatherRepository(openWeatherApi)
    }
}