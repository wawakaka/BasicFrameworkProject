package io.github.wawakaka.domain.usecase

import io.github.wawakaka.data.currencyrates.CurrencyRatesRepository
import java.math.BigDecimal

class GetLatestRatesUsecase(private val currencyRatesRepository: CurrencyRatesRepository) {

    suspend fun getLatestCurrencyRates(): List<Pair<String, BigDecimal>> {
        val response = currencyRatesRepository.getLatestCurrencyRates()
        return response.rates?.map { it.key to BigDecimal(it.value) } ?: emptyList()
    }
}