package io.github.wawakaka.data.currencyrates

import io.github.wawakaka.data.BuildConfig
import io.github.wawakaka.data.currencyrates.model.response.CurrencyRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRatesApi {

    @GET("latest")
    suspend fun getLatestWithBase(
        @Query("access_key") apiKey: String = BuildConfig.API_KEY,
        @Query("base") base: String = "USD"
    ): CurrencyRatesResponse
}