package io.github.wawakaka.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RatesTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromRatesMap(rates: Map<String, String>): String {
        return gson.toJson(rates)
    }

    @TypeConverter
    fun toRatesMap(ratesJson: String): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(ratesJson, type)
    }
}
