package io.github.wawakaka.domain

import android.app.Application
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import io.github.wawakaka.repository.Repository
import io.github.wawakaka.repository.currencyrates.CurrencyRatesRepository

object Usecase {

    fun getRatesUsecase(application: Application): GetLatestRatesUsecase {
        return GetLatestRatesUsecase(
            currencyRatesRepository = providesCurrencyRatesRepository(
                application = application
            )
        )
    }

    private fun providesCurrencyRatesRepository(application: Application): CurrencyRatesRepository {
        return Repository.getCurrencyRatesRepository(application)
    }
}