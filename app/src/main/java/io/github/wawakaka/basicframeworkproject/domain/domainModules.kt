package io.github.wawakaka.basicframeworkproject.domain

import io.github.wawakaka.domain.Usecase
import org.koin.dsl.module

val domainModules = module {
    single { Usecase.getRatesUsecase(get()) }
}