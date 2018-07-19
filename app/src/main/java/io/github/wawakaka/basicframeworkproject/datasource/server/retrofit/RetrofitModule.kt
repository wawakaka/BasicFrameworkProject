package io.github.wawakaka.basicframeworkproject.datasource.server.retrofit

import com.google.gson.Gson
import io.github.wawakaka.basicframeworkproject.BuildConfig
import io.github.wawakaka.basicframeworkproject.datasource.server.model.ServerApi
import io.github.wawakaka.basicframeworkproject.datasource.server.utils.enableTls12OnPreLollipop
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.Module
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * Created by wawakaka on 17/07/18.
 */
val retrofitModule: Module = org.koin.dsl.module.applicationContext {
    bean("call_adapter_factory") { provideCallAdapterFactory() }
    bean("lower_case_with_underscores_gson") { Gson() }
    bean("gson_converter") { provideConverterFactory(get("lower_case_with_underscores_gson")) }
    bean("base_url") { provideBaseUrl() }
    bean("http_logging_interceptor") { provideHttpLoggingInterceptor(BuildConfig.DEBUG) }
    bean("any_on_empty_converter") { provideAnyOnEmptyConverter() }
    bean("header_interceptor") { provideHeaderInterceptor() }
    bean("okhttp_client") {
        provideOkHttpClient(
            get("header_interceptor"),
            get("http_logging_interceptor")
        )
    }
    bean("server_retrofit") {
        provideServerRetrofit(
            get("okhttp_client"),
            get("base_url"),
            get("call_adapter_factory"),
            get("gson_converter"),
            get("any_on_empty_converter")
        )

    }
    bean { provideServerApi(get("server_retrofit")) }
}

fun provideServerApi(retrofit: Retrofit): ServerApi {
    return retrofit.create(ServerApi::class.java)
}

fun provideServerRetrofit(okHttpClient: OkHttpClient,
                          baseUrl: String,
                          callAdapterFactory: CallAdapter.Factory,
                          gsonConverter: Converter.Factory,
                          anyOnEmptyConverter: Converter.Factory): Retrofit {
    return Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .addCallAdapterFactory(callAdapterFactory)
        .addConverterFactory(anyOnEmptyConverter)
        .addConverterFactory(gsonConverter)
        .build()
}

fun provideOkHttpClient(headerInterceptor: Interceptor,
                        loggingInterceptor: Interceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .enableTls12OnPreLollipop()
        .build()
}

fun provideHttpLoggingInterceptor(logEnabled: Boolean): Interceptor {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = if (logEnabled) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    return httpLoggingInterceptor
}

fun provideAnyOnEmptyConverter(): Converter.Factory {
    return object : Converter.Factory() {
        override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, Any> {
            val delegate: Converter<ResponseBody, Any> = retrofit.nextResponseBodyConverter(this, type, annotations)
            return Converter { body ->
                if (body.contentLength() == 0L) return@Converter null
                delegate.convert(body)
            }
        }
    }
}

fun provideHeaderInterceptor(): Interceptor {
    return Interceptor { chain ->
        val request = chain.request()
        val headerInterceptedRequest = request.newBuilder()
            .header("Content-Type", "application/json")
            .header("User-Agent", "App name" + "-v" + BuildConfig.VERSION_NAME)
            .method(request.method(), request.body())
            .build()
        chain.proceed(headerInterceptedRequest)
    }
}

fun provideCallAdapterFactory(): CallAdapter.Factory {
    return RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
}

fun provideConverterFactory(gson: Gson): Converter.Factory {
    return GsonConverterFactory.create(gson)
}

fun provideBaseUrl(): String {
    return "http://api.openweathermap.org/"
}


