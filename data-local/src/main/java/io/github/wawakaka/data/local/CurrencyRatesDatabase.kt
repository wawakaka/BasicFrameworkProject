package io.github.wawakaka.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.wawakaka.data.local.converter.RatesTypeConverter
import io.github.wawakaka.data.local.dao.CurrencyRatesDao
import io.github.wawakaka.data.local.entity.CachedCurrencyRatesEntity

@Database(
    entities = [CachedCurrencyRatesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RatesTypeConverter::class)
abstract class CurrencyRatesDatabase : RoomDatabase() {

    abstract fun currencyRatesDao(): CurrencyRatesDao

    companion object {
        private const val DATABASE_NAME = "currency_rates_database"

        @Volatile
        private var INSTANCE: CurrencyRatesDatabase? = null

        fun getInstance(context: Context): CurrencyRatesDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): CurrencyRatesDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                CurrencyRatesDatabase::class.java,
                DATABASE_NAME
            ).build()
        }

        fun buildInMemoryDatabase(context: Context): CurrencyRatesDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                CurrencyRatesDatabase::class.java
            ).allowMainThreadQueries().build()
        }
    }
}
