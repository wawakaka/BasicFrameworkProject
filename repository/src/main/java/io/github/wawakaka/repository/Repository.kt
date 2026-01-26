package io.github.wawakaka.repository

import android.app.Application
import io.github.wawakaka.repository.currencyrates.CurrencyRatesApi
import io.github.wawakaka.repository.currencyrates.CurrencyRatesRepository
import io.github.wawakaka.restapi.RestApi
import retrofit2.Retrofit

object Repository {

    fun getCurrencyRatesRepository(application: Application): CurrencyRatesRepository {
        return CurrencyRatesRepository(
            currencyRatesApi = provideCurrencyRatesApi(
                retrofit = providesCurrencyRatesRetrofit(
                    application = application
                )
            )
        )
    }

    private fun provideCurrencyRatesApi(retrofit: Retrofit): CurrencyRatesApi {
        return retrofit.create(CurrencyRatesApi::class.java)
    }

    private fun providesCurrencyRatesRetrofit(application: Application): Retrofit {
        return RestApi.retrofit(application = application, baseUrl = BuildConfig.BASE_URL)
    }
}