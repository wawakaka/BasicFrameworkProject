package io.github.wawakaka.repository.currencyrates

import io.github.wawakaka.repository.currencyrates.model.response.CurrencyRatesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRatesApi {

    @GET("latest")
    fun getLatestWithBase(
        @Query("base") base: String = "USD"
    ): Observable<CurrencyRatesResponse>

    companion object {
        const val BASE_URL = "https://api.ratesapi.io/api/"
    }

}