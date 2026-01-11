package io.github.wawakaka.domain.usecase

import io.github.wawakaka.repository.currencyrates.CurrencyRatesRepository

class GetLatestRatesUsecase(private val currencyRatesRepository: CurrencyRatesRepository) {

    suspend fun getLatestCurrencyRates(): List<Pair<String, Double>> {
        val response = currencyRatesRepository.getLatestCurrencyRates()
        return response.rates?.toList() ?: emptyList()
    }
}