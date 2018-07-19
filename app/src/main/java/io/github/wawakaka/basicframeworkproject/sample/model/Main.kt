package io.github.wawakaka.basicframeworkproject.sample.model

import java.io.Serializable

/**
 * Created by wawakaka on 19/07/18.
 */
data class Main(val temp: Double?,
                val pressure: Int?,
                val humidity: Int?,
                val temp_min: Double?,
                val temp_max: Double?) : Serializable