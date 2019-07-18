package io.github.wawakaka.basicframeworkproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.multidex.MultiDex
import org.jetbrains.anko.notificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Created by wawakaka on 17/07/18.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initNotificationChannel()
    }

    private fun initKoin() {
        startKoin {
            // declare used Android context
            androidContext(this@App)
            // declare modules
            modules(applicationModules)
        }
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "default",
                    "default",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}