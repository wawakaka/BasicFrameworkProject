package io.github.wawakaka.basicframeworkproject.sample.model

import java.io.Serializable

/**
 * Created by wawakaka on 19/07/18.
 */
data class Weather(
    val id: Int?,
    val main: String?,
    val description: String?,
    val icon: String?
) : Serializable