package io.github.wawakaka.data.remote

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object RestApi {

    fun retrofit(application: Application, baseUrl: String): Retrofit {
        return provideServerRetrofit(
            okHttpClient = provideOkHttpClient(
                headerInterceptor = HeaderInterceptor(),
                loggingInterceptor = provideHttpLoggingInterceptor(BuildConfig.DEBUG),
                chuckerInterceptor = provideChuckerInterceptor(application)
            ),
            baseUrl = baseUrl,
            gson = provideGson()
        )
    }

    private fun provideServerRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: String,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .build()
    }

    private fun provideOkHttpClient(
        headerInterceptor: Interceptor,
        loggingInterceptor: Interceptor,
        chuckerInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(chuckerInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private fun provideChuckerInterceptor(application: Application): Interceptor {
        return ChuckerInterceptor.Builder(application).build()
    }

    private fun provideHttpLoggingInterceptor(logEnabled: Boolean): Interceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level =
            if (logEnabled) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return httpLoggingInterceptor
    }

    private fun provideGson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()
    }

}
