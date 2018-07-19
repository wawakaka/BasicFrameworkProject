package io.github.wawakaka.basicframeworkproject.datasource.server

import io.github.wawakaka.basicframeworkproject.datasource.server.model.ServerApi
import io.github.wawakaka.basicframeworkproject.datasource.server.model.UnknownError
import io.github.wawakaka.basicframeworkproject.sample.model.CurrentWeather
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Created by wawakaka on 19/07/18.
 */
class ServerRequestManager(private val serverApi: ServerApi) {

    fun geCurrentWeatherObservable(apiKey: String): Observable<CurrentWeather> {
        return handleServerRequestError(
            serverApi.geCurrentWeatherObservable(apiKey)
        )
    }

    /**
     * Handle error occurred from original observable.
     *
     * @param originalObservable Observable original that will be executed immediately
     * an authorization error. So, it will use the refreshed token.
     */
    private fun <T> handleServerRequestError(originalObservable: Observable<T>): Observable<T> {
        return originalObservable.onErrorResumeNext(Function<Throwable, Observable<T>> {
            Observable.error(UnknownError(it))
        })
    }
}