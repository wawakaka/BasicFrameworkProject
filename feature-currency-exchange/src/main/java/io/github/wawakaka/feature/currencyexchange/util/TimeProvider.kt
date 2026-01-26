package io.github.wawakaka.feature.currencyexchange.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal interface TimeProvider {
    fun nowTimestamp(): String
}

internal class SystemTimeProvider(
    private val locale: Locale = Locale.getDefault()
) : TimeProvider {

    override fun nowTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale).format(Date())
    }
}
