package io.github.wawakaka.basicframeworkproject.data.network

import android.app.Application
import io.github.wawakaka.repository.openweathermap.OpenWeatherApi
import io.github.wawakaka.restapi.RestApi
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    single { provideRetrofit(get()) }
    single { provideServerApi(get()) }
}

private fun provideRetrofit(application: Application): Retrofit {
    return RestApi.retrofit(application)
}

private fun provideServerApi(retrofit: Retrofit): OpenWeatherApi {
    return retrofit.create(OpenWeatherApi::class.java)
}
