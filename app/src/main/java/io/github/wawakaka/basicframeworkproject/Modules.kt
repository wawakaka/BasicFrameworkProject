package io.github.wawakaka.basicframeworkproject

import io.github.wawakaka.basicframeworkproject.data.network.networkModule
import io.github.wawakaka.basicframeworkproject.data.repositoryModule
import io.github.wawakaka.basicframeworkproject.sample.sampleModule

/**
 * Created by wawakaka on 17/07/18.
 */
val applicationModules =
    listOf(
        sampleModule,
        networkModule,
        repositoryModule
    )