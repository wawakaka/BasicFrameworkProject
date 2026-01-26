package io.github.wawakaka.data.currencyrates

import io.github.wawakaka.data.cache.CacheTimeProvider
import io.github.wawakaka.data.cache.SystemCacheTimeProvider
import io.github.wawakaka.data.currencyrates.model.response.CurrencyRatesResponse
import io.github.wawakaka.data.local.dao.CurrencyRatesDao
import io.github.wawakaka.data.local.entity.CachedCurrencyRatesEntity

class CurrencyRatesRepository(
    private val currencyRatesApi: CurrencyRatesApi,
    private val currencyRatesDao: CurrencyRatesDao,
    private val timeProvider: CacheTimeProvider = SystemCacheTimeProvider()
) {

    companion object {
        private const val CACHE_VALIDITY_MILLIS = 24 * 60 * 60 * 1000L // 24 hours
    }

    suspend fun getLatestCurrencyRates(base: String = "EUR"): CurrencyRatesResponse {
        val cached = currencyRatesDao.getCachedRates(base)
        if (cached != null && isCacheValid(cached.cachedAtMillis)) {
            return cached.toResponse()
        }

        val response = currencyRatesApi.getLatestWithBase(base = base)
        currencyRatesDao.insertCachedRates(response.toEntity(timeProvider.currentTimeMillis()))
        return response
    }

    private fun isCacheValid(cachedAtMillis: Long): Boolean {
        return timeProvider.currentTimeMillis() - cachedAtMillis < CACHE_VALIDITY_MILLIS
    }

    private fun CachedCurrencyRatesEntity.toResponse(): CurrencyRatesResponse {
        return CurrencyRatesResponse(
            base = baseCurrency,
            date = date,
            rates = rates
        )
    }

    private fun CurrencyRatesResponse.toEntity(cachedAtMillis: Long): CachedCurrencyRatesEntity {
        return CachedCurrencyRatesEntity(
            baseCurrency = base ?: "",
            date = date,
            rates = rates ?: emptyMap(),
            cachedAtMillis = cachedAtMillis
        )
    }
}
