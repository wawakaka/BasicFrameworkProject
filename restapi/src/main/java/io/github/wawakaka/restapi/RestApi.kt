package io.github.wawakaka.restapi

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import com.readystatesoftware.chuck.ChuckInterceptor
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object RestApi {

    fun retrofit(application: Application, baseUrl: String): Retrofit {
        return provideServerRetrofit(
            okHttpClient = provideOkHttpClient(
                headerInterceptor = HeaderInterceptor(),
                loggingInterceptor = provideHttpLoggingInterceptor(BuildConfig.DEBUG),
                chuckInterceptor = provideChuckInterceptor(application)
            ),
            baseUrl = baseUrl,
            callAdapterFactory = provideCallAdapterFactory(),
            gson = provideGson()
        )
    }

    private fun provideServerRetrofit(
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

    private fun provideOkHttpClient(
        headerInterceptor: Interceptor,
        loggingInterceptor: Interceptor,
        chuckInterceptor: ChuckInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(chuckInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private fun provideChuckInterceptor(application: Application): ChuckInterceptor {
        return ChuckInterceptor(application)
    }

    private fun provideHttpLoggingInterceptor(logEnabled: Boolean): Interceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level =
            if (logEnabled) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return httpLoggingInterceptor
    }

    private fun provideCallAdapterFactory(): CallAdapter.Factory {
        return RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
    }

    private fun provideGson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
    }

}