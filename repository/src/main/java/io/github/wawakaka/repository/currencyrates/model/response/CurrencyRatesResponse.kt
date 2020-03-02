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
        fun toList() = listOf(
            aUD!!::class.java.fields.asIterable(),
            bGN,
            bRL,
            cAD,
            cHF,
            cNY,
            cZK,
            dKK,
            eUR,
            gBP,
            hKD,
            hRK,
            hUF,
            iDR,
            iLS,
            iNR,
            iSK,
            jPY,
            kRW,
            mXN,
            mYR,
            nOK,
            nZD,
            pHP,
            pLN,
            rON,
            rUB,
            sEK,
            sGD,
            tHB,
            tRY,
            uSD
        )
    }
}