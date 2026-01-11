package io.github.wawakaka.repository.currencyrates

import io.github.wawakaka.repository.currencyrates.model.response.CurrencyRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRatesApi {

    @GET("latest")
    suspend fun getLatestWithBase(
        @Query("base") base: String = "USD"
    ): CurrencyRatesResponse

    companion object {
        const val BASE_URL = "https://api.ratesapi.io/api/"
    }

}