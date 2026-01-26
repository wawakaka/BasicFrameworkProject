package io.github.wawakaka.basicframeworkproject.utilities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface TimeProvider {
    fun nowTimestamp(): String
}

class SystemTimeProvider(
    private val locale: Locale = Locale.getDefault()
) : TimeProvider {

    override fun nowTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale).format(Date())
    }
}
