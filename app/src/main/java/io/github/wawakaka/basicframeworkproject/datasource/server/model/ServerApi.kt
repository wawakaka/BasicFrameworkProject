package io.github.wawakaka.basicframeworkproject.datasource.server.model

import io.github.wawakaka.basicframeworkproject.sample.model.CurrentWeather
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by wawakaka on 17/07/18.
 */
interface ServerApi {

    @GET("data/2.5/weather?id=2172797")
    fun geCurrentWeatherObservable(@Query("APPID") apiKey: String): Observable<CurrentWeather>
}