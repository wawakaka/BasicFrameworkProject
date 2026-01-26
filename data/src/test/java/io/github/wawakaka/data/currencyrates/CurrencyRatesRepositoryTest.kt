package io.github.wawakaka.data.currencyrates

import io.github.wawakaka.data.cache.CacheTimeProvider
import io.github.wawakaka.data.currencyrates.model.response.CurrencyRatesResponse
import io.github.wawakaka.data.local.dao.CurrencyRatesDao
import io.github.wawakaka.data.local.entity.CachedCurrencyRatesEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CurrencyRatesRepositoryTest {

    private lateinit var currencyRatesApi: CurrencyRatesApi
    private lateinit var currencyRatesDao: CurrencyRatesDao
    private lateinit var timeProvider: CacheTimeProvider
    private lateinit var repository: CurrencyRatesRepository

    companion object {
        private const val CACHE_VALIDITY_MILLIS = 24 * 60 * 60 * 1000L // 24 hours
        private const val CURRENT_TIME = 1706284800000L // 2024-01-26 12:00:00
    }

    @Before
    fun setup() {
        currencyRatesApi = mock()
        currencyRatesDao = mock()
        timeProvider = mock()
        whenever(timeProvider.currentTimeMillis()).thenReturn(CURRENT_TIME)
        repository = CurrencyRatesRepository(currencyRatesApi, currencyRatesDao, timeProvider)
    }

    @Test
    fun `returns cached data when cache is valid`() = runTest {
        val cachedEntity = CachedCurrencyRatesEntity(
            baseCurrency = "EUR",
            date = "2026-01-26",
            rates = mapOf("USD" to "1.0856", "GBP" to "0.8432"),
            cachedAtMillis = CURRENT_TIME - (CACHE_VALIDITY_MILLIS / 2) // Cached 12 hours ago
        )
        whenever(currencyRatesDao.getCachedRates("EUR")).thenReturn(cachedEntity)

        val result = repository.getLatestCurrencyRates("EUR")

        assertEquals("EUR", result.base)
        assertEquals("2026-01-26", result.date)
        assertEquals(2, result.rates?.size)
        assertEquals("1.0856", result.rates?.get("USD"))
        verify(currencyRatesApi, never()).getLatestWithBase(any(), any())
        verify(currencyRatesDao, never()).insertCachedRates(any())
    }

    @Test
    fun `fetches from API when cache is expired`() = runTest {
        val expiredCache = CachedCurrencyRatesEntity(
            baseCurrency = "EUR",
            date = "2026-01-24",
            rates = mapOf("USD" to "1.0800"),
            cachedAtMillis = CURRENT_TIME - CACHE_VALIDITY_MILLIS - 1000L // Expired
        )
        val apiResponse = CurrencyRatesResponse(
            base = "EUR",
            date = "2026-01-26",
            rates = mapOf("USD" to "1.0856", "GBP" to "0.8432")
        )
        whenever(currencyRatesDao.getCachedRates("EUR")).thenReturn(expiredCache)
        whenever(currencyRatesApi.getLatestWithBase(any(), eq("EUR"))).thenReturn(apiResponse)

        val result = repository.getLatestCurrencyRates("EUR")

        assertEquals("EUR", result.base)
        assertEquals("2026-01-26", result.date)
        assertEquals(2, result.rates?.size)
        verify(currencyRatesApi).getLatestWithBase(any(), eq("EUR"))
        verify(currencyRatesDao).insertCachedRates(any())
    }

    @Test
    fun `fetches from API when no cache exists`() = runTest {
        val apiResponse = CurrencyRatesResponse(
            base = "EUR",
            date = "2026-01-26",
            rates = mapOf("USD" to "1.0856", "GBP" to "0.8432", "JPY" to "162.35")
        )
        whenever(currencyRatesDao.getCachedRates("EUR")).thenReturn(null)
        whenever(currencyRatesApi.getLatestWithBase(any(), eq("EUR"))).thenReturn(apiResponse)

        val result = repository.getLatestCurrencyRates("EUR")

        assertEquals("EUR", result.base)
        assertEquals("2026-01-26", result.date)
        assertEquals(3, result.rates?.size)
        verify(currencyRatesApi).getLatestWithBase(any(), eq("EUR"))
        verify(currencyRatesDao).insertCachedRates(any())
    }

    @Test
    fun `saves API response to cache after fetching`() = runTest {
        val apiResponse = CurrencyRatesResponse(
            base = "USD",
            date = "2026-01-26",
            rates = mapOf("EUR" to "0.9212")
        )
        whenever(currencyRatesDao.getCachedRates("USD")).thenReturn(null)
        whenever(currencyRatesApi.getLatestWithBase(any(), eq("USD"))).thenReturn(apiResponse)

        repository.getLatestCurrencyRates("USD")

        val expectedEntity = CachedCurrencyRatesEntity(
            baseCurrency = "USD",
            date = "2026-01-26",
            rates = mapOf("EUR" to "0.9212"),
            cachedAtMillis = CURRENT_TIME
        )
        verify(currencyRatesDao).insertCachedRates(expectedEntity)
    }

    @Test
    fun `returns cached data at boundary of validity period`() = runTest {
        val cachedEntity = CachedCurrencyRatesEntity(
            baseCurrency = "EUR",
            date = "2026-01-25",
            rates = mapOf("USD" to "1.0850"),
            cachedAtMillis = CURRENT_TIME - CACHE_VALIDITY_MILLIS + 1000L // Just within validity
        )
        whenever(currencyRatesDao.getCachedRates("EUR")).thenReturn(cachedEntity)

        val result = repository.getLatestCurrencyRates("EUR")

        assertEquals("2026-01-25", result.date)
        verify(currencyRatesApi, never()).getLatestWithBase(any(), any())
    }
}
