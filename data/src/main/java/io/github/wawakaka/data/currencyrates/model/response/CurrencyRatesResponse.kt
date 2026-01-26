package io.github.wawakaka.data.currencyrates.model.response

import com.google.gson.annotations.SerializedName

data class CurrencyRatesResponse(
    @SerializedName("base")
    val base: String? = "",
    @SerializedName("date")
    val date: String? = "",
    @SerializedName("rates")
    val rates: Map<String, String>? = null
)