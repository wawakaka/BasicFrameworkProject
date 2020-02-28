package io.github.wawakaka.repository

import android.app.Application
import io.github.wawakaka.repository.openweathermap.OpenWeatherApi
import io.github.wawakaka.repository.openweathermap.OpenWeatherRepository
import io.github.wawakaka.restapi.RestApi
import retrofit2.Retrofit

object Repository {

    fun getOpenWeather(application: Application): OpenWeatherRepository {
        return OpenWeatherRepository(provideServerApi(provideRetrofit(application)))
    }

    private fun provideServerApi(retrofit: Retrofit): OpenWeatherApi {
        return retrofit.create(OpenWeatherApi::class.java)
    }

    private fun provideRetrofit(application: Application): Retrofit {
        return RestApi.retrofit(application)
    }

}