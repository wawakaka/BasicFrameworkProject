package io.github.wawakaka.data.local

import android.content.Context
import io.github.wawakaka.data.local.dao.CurrencyRatesDao

object LocalDataSource {

    fun currencyRatesDao(context: Context): CurrencyRatesDao {
        return CurrencyRatesDatabase.getInstance(context).currencyRatesDao()
    }
}
