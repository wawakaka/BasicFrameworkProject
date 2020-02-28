package io.github.wawakaka.basicframeworkproject

import io.github.wawakaka.basicframeworkproject.data.repositoryModule
import io.github.wawakaka.basicframeworkproject.presentation.content.weatherListModule
import io.github.wawakaka.basicframeworkproject.presentation.mainModule

val applicationModules =
    listOf(
        mainModule,
        repositoryModule,
        weatherListModule
    )