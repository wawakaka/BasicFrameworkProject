package io.github.wawakaka.basicframeworkproject

import io.github.wawakaka.basicframeworkproject.data.network.networkModule
import io.github.wawakaka.basicframeworkproject.data.repositories.repositoryModule
import io.github.wawakaka.basicframeworkproject.presentation.content.weatherListModule
import io.github.wawakaka.basicframeworkproject.presentation.mainModule

val applicationModules =
    listOf(
        mainModule,
        networkModule,
        repositoryModule,
        weatherListModule
    )