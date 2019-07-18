package io.github.wawakaka.basicframeworkproject.data.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import io.github.wawakaka.basicframeworkproject.BuildConfig
import io.github.wawakaka.basicframeworkproject.data.openweathermap.model.OpenWeatherApi
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by wawakaka on 17/07/18.
 */
val networkModule = module {
    single { provideGson() }
    single(named("call_adapter_factory")) { provideCallAdapterFactory() }
    single(named("base_url")) { provideBaseUrl() }
    single(named("http_logging_interceptor")) {
        provideHttpLoggingInterceptor(
            BuildConfig.DEBUG
        )
    }
    single(named("header_interceptor")) { provideHeaderInterceptor() }
    single(named("okhttp_client")) {
        provideOkHttpClient(
            get(named("header_interceptor")),
            get(named("http_logging_interceptor"))
        )
    }
    single(named("server_retrofit")) {
        provideServerRetrofit(
            get(named("okhttp_client")),
            get(named("base_url")),
            get(named("call_adapter_factory")),
            get()
        )
    }
    single { provideServerApi(get(named("server_retrofit"))) }
}

fun provideServerApi(retrofit: Retrofit): OpenWeatherApi {
    return retrofit.create(OpenWeatherApi::class.java)
}

fun provideServerRetrofit(
    okHttpClient: OkHttpClient,
    baseUrl: String,
    callAdapterFactory: CallAdapter.Factory,
    gson: Gson
): Retrofit {
    return Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(baseUrl)
        .addCallAdapterFactory(callAdapterFactory)
        .build()
}

fun provideOkHttpClient(
    headerInterceptor: Interceptor,
    loggingInterceptor: Interceptor
): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
}

fun provideHttpLoggingInterceptor(logEnabled: Boolean): Interceptor {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level =
        if (logEnabled) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    return httpLoggingInterceptor
}

fun provideHeaderInterceptor(): Interceptor {
    return HeaderInterceptor()
}

fun provideCallAdapterFactory(): CallAdapter.Factory {
    return RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
}

fun provideGson(): Gson {
    return GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        .registerTypeAdapter(Date::class.java, DateTypeAdapter())
        .create()
}

fun provideBaseUrl(): String {
    return "http://api.openweathermap.org/"
}
