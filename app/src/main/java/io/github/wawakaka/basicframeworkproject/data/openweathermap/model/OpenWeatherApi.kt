package io.github.wawakaka.basicframeworkproject.data.openweathermap.model

import io.github.wawakaka.basicframeworkproject.data.openweathermap.model.response.Weather
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by wawakaka on 17/07/18.
 */
interface OpenWeatherApi {

    @GET("data/2.5/weather")
    fun geCurrentWeatherObservable(
        @Query("id") id: String,
        @Query("appid") apiKey: String
    ): Observable<Weather>
}