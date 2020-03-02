package io.github.wawakaka.repository.currencyrates

import io.github.wawakaka.repository.currencyrates.model.response.CurrencyRatesResponse
import io.reactivex.Observable

class CurrencyRatesRepository(private val currencyRatesApi: CurrencyRatesApi) {

    fun getLatestCurrencyRates(base: String = "USD"): Observable<CurrencyRatesResponse> {
        return currencyRatesApi.getLatestWithBase(base)
    }
}