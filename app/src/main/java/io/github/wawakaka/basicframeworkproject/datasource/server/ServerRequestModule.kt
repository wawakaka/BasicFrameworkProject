package io.github.wawakaka.basicframeworkproject.datasource.server

import org.koin.dsl.module.Module

/**
 * Created by wawakaka on 19/07/18.
 */
val serverRequestModule: Module = org.koin.dsl.module.applicationContext {
    bean { ServerRequestManager(get()) }
}