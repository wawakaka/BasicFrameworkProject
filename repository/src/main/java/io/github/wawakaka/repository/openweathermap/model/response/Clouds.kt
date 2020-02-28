package io.github.wawakaka.repository.openweathermap.model.response

import com.google.gson.annotations.SerializedName

data class Clouds(

    @field:SerializedName("all")
    val all: Int? = null
)