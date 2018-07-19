package io.github.wawakaka.basicframeworkproject.base

import io.github.wawakaka.basicframeworkproject.datasource.server.retrofit.retrofitModule
import io.github.wawakaka.basicframeworkproject.datasource.server.serverRequestModule
import io.github.wawakaka.basicframeworkproject.sample.sampleModule

/**
 * Created by wawakaka on 17/07/18.
 */
val applicationModules =
    listOf(
        sampleModule,
        retrofitModule,
        serverRequestModule
    )