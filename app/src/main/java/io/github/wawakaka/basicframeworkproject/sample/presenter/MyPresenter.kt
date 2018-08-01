package io.github.wawakaka.basicframeworkproject.sample.presenter

import io.github.wawakaka.basicframeworkproject.datasource.server.ServerRequestManager
import io.github.wawakaka.basicframeworkproject.sample.model.CurrentWeather
import io.reactivex.Observable

/**
 * Created by wawakaka on 17/07/18.
 */
class MyPresenter(private val serverRequestManager: ServerRequestManager) {

    fun geCurrentWeatherObservable(apiKey: String): Observable<CurrentWeather> {
        return serverRequestManager
            .geCurrentWeatherObservable(apiKey)
            .map { it }
    }
}