package io.github.wawakaka.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.wawakaka.data.local.entity.CachedCurrencyRatesEntity

@Dao
interface CurrencyRatesDao {

    @Query("SELECT * FROM cached_currency_rates WHERE baseCurrency = :base")
    suspend fun getCachedRates(base: String): CachedCurrencyRatesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedRates(entity: CachedCurrencyRatesEntity)

    @Query("DELETE FROM cached_currency_rates WHERE baseCurrency = :base")
    suspend fun deleteCachedRates(base: String)

    @Query("DELETE FROM cached_currency_rates")
    suspend fun clearAllCache()
}
