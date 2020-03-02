package io.github.wawakaka.basicframeworkproject

import io.github.wawakaka.basicframeworkproject.domain.domainModules
import io.github.wawakaka.basicframeworkproject.presentation.content.currencyModule
import io.github.wawakaka.basicframeworkproject.presentation.mainModule

val applicationModules =
    listOf(
        mainModule,
        domainModules,
        currencyModule
    )