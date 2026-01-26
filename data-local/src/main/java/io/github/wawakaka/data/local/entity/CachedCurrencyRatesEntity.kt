package io.github.wawakaka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_currency_rates")
data class CachedCurrencyRatesEntity(
    @PrimaryKey
    val baseCurrency: String,
    val date: String?,
    val rates: Map<String, String>,
    val cachedAtMillis: Long
)
