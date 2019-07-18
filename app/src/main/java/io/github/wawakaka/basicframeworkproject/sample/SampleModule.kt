package io.github.wawakaka.basicframeworkproject.sample

import org.koin.dsl.module

/**
 * Created by wawakaka on 17/07/18.
 */
val sampleModule = module {
    single { MyPresenter(get()) }
}