package io.github.wawakaka.repository.currencyrates

import io.github.wawakaka.repository.currencyrates.model.response.CurrencyRatesResponse

class CurrencyRatesRepository(private val currencyRatesApi: CurrencyRatesApi) {

    suspend fun getLatestCurrencyRates(base: String = "USD"): CurrencyRatesResponse {
        return currencyRatesApi.getLatestWithBase(base)
    }
}