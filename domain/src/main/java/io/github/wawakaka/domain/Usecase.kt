package io.github.wawakaka.domain

import android.app.Application
import io.github.wawakaka.data.Repository
import io.github.wawakaka.data.currencyrates.CurrencyRatesRepository
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase

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