package io.github.wawakaka.basicframeworkproject.sample.model

import io.github.wawakaka.basicframeworkproject.base.model.Emptiable
import java.io.Serializable

/**
 * Created by wawakaka on 19/07/18.
 */
data class CurrentWeather(val coord: Coord?,
                          val weather: MutableList<Weather>?,
                          val base: String?,
                          val main: Main?,
                          val visibility: Int?,
                          val wind: Wind?,
                          val clouds: Clouds?,
                          val dt: Int?,
                          val sys: Sys?,
                          val id: Int?,
                          val name: String?,
                          val cod: Int?) : Serializable, Emptiable {

    companion object {
        val empty = CurrentWeather(null, null, null, null, null, null, null, null, null, null, null, null)
    }

    override fun isEmpty(): Boolean = id == 0 || id == null

    override fun isNotEmpty(): Boolean = !isEmpty()
}