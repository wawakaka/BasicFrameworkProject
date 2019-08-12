package io.github.wawakaka.basicframeworkproject.data.network

import android.app.Application
import io.github.wawakaka.repository.openweathermap.OpenWeatherApi
import io.github.wawakaka.restapi.RestApi
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Created by wawakaka on 17/07/18.
 */

val networkModule = module {
    single(named("retrofit")) { provideRetrofit(get()) }
    single { provideServerApi(get(named("retrofit"))) }
}

private fun provideRetrofit(application: Application): Retrofit {
    return RestApi.retrofit(application)
}

private fun provideServerApi(retrofit: Retrofit): OpenWeatherApi {
    return retrofit.create(OpenWeatherApi::class.java)
}
