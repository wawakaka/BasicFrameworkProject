package io.github.wawakaka.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.wawakaka.data.local.dao.CurrencyRatesDao
import io.github.wawakaka.data.local.entity.CachedCurrencyRatesEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencyRatesDaoTest {

    private lateinit var database: CurrencyRatesDatabase
    private lateinit var dao: CurrencyRatesDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = CurrencyRatesDatabase.buildInMemoryDatabase(context)
        dao = database.currencyRatesDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveCachedRates() = runTest {
        val entity = CachedCurrencyRatesEntity(
            baseCurrency = "EUR",
            date = "2026-01-26",
            rates = mapOf("USD" to "1.0856", "GBP" to "0.8432", "JPY" to "162.35"),
            cachedAtMillis = System.currentTimeMillis()
        )

        dao.insertCachedRates(entity)
        val result = dao.getCachedRates("EUR")

        assertNotNull(result)
        assertEquals("EUR", result?.baseCurrency)
        assertEquals("2026-01-26", result?.date)
        assertEquals(3, result?.rates?.size)
        assertEquals("1.0856", result?.rates?.get("USD"))
    }

    @Test
    fun replaceOnDuplicateBaseCurrency() = runTest {
        val entity1 = CachedCurrencyRatesEntity(
            baseCurrency = "EUR",
            date = "2026-01-25",
            rates = mapOf("USD" to "1.0800"),
            cachedAtMillis = 1000L
        )
        val entity2 = CachedCurrencyRatesEntity(
            baseCurrency = "EUR",
            date = "2026-01-26",
            rates = mapOf("USD" to "1.0856", "GBP" to "0.8432"),
            cachedAtMillis = 2000L
        )

        dao.insertCachedRates(entity1)
        dao.insertCachedRates(entity2)
        val result = dao.getCachedRates("EUR")

        assertNotNull(result)
        assertEquals("2026-01-26", result?.date)
        assertEquals(2, result?.rates?.size)
        assertEquals(2000L, result?.cachedAtMillis)
    }

    @Test
    fun deleteSpecificCache() = runTest {
        val eurEntity = CachedCurrencyRatesEntity(
            baseCurrency = "EUR",
            date = "2026-01-26",
            rates = mapOf("USD" to "1.0856"),
            cachedAtMillis = System.currentTimeMillis()
        )
        val usdEntity = CachedCurrencyRatesEntity(
            baseCurrency = "USD",
            date = "2026-01-26",
            rates = mapOf("EUR" to "0.9212"),
            cachedAtMillis = System.currentTimeMillis()
        )

        dao.insertCachedRates(eurEntity)
        dao.insertCachedRates(usdEntity)
        dao.deleteCachedRates("EUR")

        assertNull(dao.getCachedRates("EUR"))
        assertNotNull(dao.getCachedRates("USD"))
    }

    @Test
    fun clearAllCache() = runTest {
        val eurEntity = CachedCurrencyRatesEntity(
            baseCurrency = "EUR",
            date = "2026-01-26",
            rates = mapOf("USD" to "1.0856"),
            cachedAtMillis = System.currentTimeMillis()
        )
        val usdEntity = CachedCurrencyRatesEntity(
            baseCurrency = "USD",
            date = "2026-01-26",
            rates = mapOf("EUR" to "0.9212"),
            cachedAtMillis = System.currentTimeMillis()
        )

        dao.insertCachedRates(eurEntity)
        dao.insertCachedRates(usdEntity)
        dao.clearAllCache()

        assertNull(dao.getCachedRates("EUR"))
        assertNull(dao.getCachedRates("USD"))
    }

    @Test
    fun getCachedRatesReturnsNullWhenNotFound() = runTest {
        val result = dao.getCachedRates("GBP")
        assertNull(result)
    }
}
