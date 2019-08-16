package io.github.wawakaka.openweathermap

import io.github.wawakaka.openweathermap.model.response.Weather
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    @GET("data/2.5/weather")
    fun geCurrentWeatherObservable(
        @Query("id") id: String,
        @Query("appid") apiKey: String
    ): Observable<Weather>
}