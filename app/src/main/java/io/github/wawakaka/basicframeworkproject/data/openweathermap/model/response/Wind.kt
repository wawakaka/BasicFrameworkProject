package io.github.wawakaka.basicframeworkproject.data.openweathermap.model.response

import com.google.gson.annotations.SerializedName

data class Wind(

    @field:SerializedName("deg")
    val deg: Int? = null,

    @field:SerializedName("speed")
    val speed: Double? = null
)