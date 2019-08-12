package io.github.wawakaka.basicframeworkproject.data.repositories

import io.github.wawakaka.repository.Repository
import org.koin.dsl.module

/**
 * Created by wawakaka on 19/07/18.
 */
val repositoryModule = module {
    single { Repository.getOpenWeather(get()) }
}