package io.github.wawakaka.data.currencyrates

import io.github.wawakaka.data.currencyrates.model.response.CurrencyRatesResponse

class CurrencyRatesRepository(private val currencyRatesApi: CurrencyRatesApi) {

    suspend fun getLatestCurrencyRates(base: String = "EUR"): CurrencyRatesResponse {
        return currencyRatesApi.getLatestWithBase(base = base)
    }
}