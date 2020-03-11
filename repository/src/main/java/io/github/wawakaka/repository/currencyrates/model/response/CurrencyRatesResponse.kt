package io.github.wawakaka.repository.currencyrates.model.response


import com.google.gson.annotations.SerializedName

data class CurrencyRatesResponse(
    @SerializedName("base")
    val base: String? = "",
    @SerializedName("date")
    val date: String? = "",
    @SerializedName("rates")
    val rates: Rates? = Rates()
) {
    data class Rates(
        @SerializedName("AUD")
        val aUD: Double? = 0.0,
        @SerializedName("BGN")
        val bGN: Double? = 0.0,
        @SerializedName("BRL")
        val bRL: Double? = 0.0,
        @SerializedName("CAD")
        val cAD: Double? = 0.0,
        @SerializedName("CHF")
        val cHF: Double? = 0.0,
        @SerializedName("CNY")
        val cNY: Double? = 0.0,
        @SerializedName("CZK")
        val cZK: Double? = 0.0,
        @SerializedName("DKK")
        val dKK: Double? = 0.0,
        @SerializedName("EUR")
        val eUR: Double? = 0.0,
        @SerializedName("GBP")
        val gBP: Double? = 0.0,
        @SerializedName("HKD")
        val hKD: Double? = 0.0,
        @SerializedName("HRK")
        val hRK: Double? = 0.0,
        @SerializedName("HUF")
        val hUF: Double? = 0.0,
        @SerializedName("IDR")
        val iDR: Double? = 0.0,
        @SerializedName("ILS")
        val iLS: Double? = 0.0,
        @SerializedName("INR")
        val iNR: Double? = 0.0,
        @SerializedName("ISK")
        val iSK: Double? = 0.0,
        @SerializedName("JPY")
        val jPY: Double? = 0.0,
        @SerializedName("KRW")
        val kRW: Double? = 0.0,
        @SerializedName("MXN")
        val mXN: Double? = 0.0,
        @SerializedName("MYR")
        val mYR: Double? = 0.0,
        @SerializedName("NOK")
        val nOK: Double? = 0.0,
        @SerializedName("NZD")
        val nZD: Double? = 0.0,
        @SerializedName("PHP")
        val pHP: Double? = 0.0,
        @SerializedName("PLN")
        val pLN: Double? = 0.0,
        @SerializedName("RON")
        val rON: Double? = 0.0,
        @SerializedName("RUB")
        val rUB: Double? = 0.0,
        @SerializedName("SEK")
        val sEK: Double? = 0.0,
        @SerializedName("SGD")
        val sGD: Double? = 0.0,
        @SerializedName("THB")
        val tHB: Double? = 0.0,
        @SerializedName("TRY")
        val tRY: Double? = 0.0,
        @SerializedName("USD")
        val uSD: Double? = 0.0,
        @SerializedName("ZAR")
        val zAR: Double? = 0.0
    ) {
        fun toList(): List<Pair<String, Double>> = listOf(
            Pair("aUD", aUD ?: 0.0),
            Pair("bGN", bGN ?: 0.0),
            Pair("bRL", bRL ?: 0.0),
            Pair("cAD", cAD ?: 0.0),
            Pair("cHF", cHF ?: 0.0),
            Pair("cNY", cNY ?: 0.0),
            Pair("cZK", cZK ?: 0.0),
            Pair("dKK", dKK ?: 0.0),
            Pair("eUR", eUR ?: 0.0),
            Pair("gBP", gBP ?: 0.0),
            Pair("hKD", hKD ?: 0.0),
            Pair("hRK", hRK ?: 0.0),
            Pair("hUF", hUF ?: 0.0),
            Pair("iDR", iDR ?: 0.0),
            Pair("iLS", iLS ?: 0.0),
            Pair("iNR", iNR ?: 0.0),
            Pair("iSK", iSK ?: 0.0),
            Pair("jPY", jPY ?: 0.0),
            Pair("kRW", kRW ?: 0.0),
            Pair("mXN", mXN ?: 0.0),
            Pair("mYR", mYR ?: 0.0),
            Pair("nOK", nOK ?: 0.0),
            Pair("nZD", nZD ?: 0.0),
            Pair("pHP", pHP ?: 0.0),
            Pair("pLN", pLN ?: 0.0),
            Pair("rON", rON ?: 0.0),
            Pair("rUB", rUB ?: 0.0),
            Pair("sEK", sEK ?: 0.0),
            Pair("sGD", sGD ?: 0.0),
            Pair("tHB", tHB ?: 0.0),
            Pair("tRY", tRY ?: 0.0),
            Pair("uSD", uSD ?: 0.0)
        )
    }
}