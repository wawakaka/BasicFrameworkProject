package io.github.wawakaka.basicframeworkproject.data

import io.github.wawakaka.basicframeworkproject.data.openweathermap.OpenWeatherRepository
import org.koin.dsl.module

/**
 * Created by wawakaka on 19/07/18.
 */
val repositoryModule = module {
    single { OpenWeatherRepository(get()) }
}