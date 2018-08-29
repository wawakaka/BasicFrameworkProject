package io.github.wawakaka.basicframeworkproject.sample.model

import java.io.Serializable

/**
 * Created by wawakaka on 19/07/18.
 */
data class Sys(
    val type: Int?,
    val id: Int?,
    val message: Double?,
    val country: String?,
    val sunrise: Int?,
    val sunset: Int?
) : Serializable