package io.github.wawakaka.basicframeworkproject.sample

import io.github.wawakaka.basicframeworkproject.sample.presenter.MyPresenter
import org.koin.dsl.module.Module

/**
 * Created by wawakaka on 17/07/18.
 */
val sampleModule: Module = org.koin.dsl.module.applicationContext {
    bean { MyPresenter(get()) }
}